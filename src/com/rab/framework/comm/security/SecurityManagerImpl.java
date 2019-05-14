package com.rab.framework.comm.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.ExceptionInfo;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.dataright.DataRightHandler;
import com.rab.framework.comm.security.dataright.DataRightHandlerFactory;
import com.rab.framework.comm.util.Constants;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.delegate.BizDelegate;

/**
 * 
 * <P>Title: SecurityManagerImpl</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-9</P>
 *
 */
public class SecurityManagerImpl implements SecurityManager{

	private static final LogWritter logger = LogFactory.getLogger(SecurityManagerImpl.class);

	private Map<String,FuncRightPrincipal> funcPrincipalPool = new HashMap<String,FuncRightPrincipal>();
	
	public SecurityManagerImpl(){
		init();
	}
	
	private void init(){
		SecurityManagerRequestEvent req = new SecurityManagerRequestEvent("VHSecurityManagerBLH");
		req.setMethod("getFuncPrincipals");

		try {
			BaseResponseEvent resp = BizDelegate.delegate(req);
			
			if(resp.isSuccess()){
				SecurityManagerResponseEvent smResp = (SecurityManagerResponseEvent)resp;
				funcPrincipalPool = smResp.getFuncPrincipals();
			}
			else{
				ExceptionInfo info = resp.getExceptionInfo();
				logger.error("系统安全管理模块初始化异常，系统终止初始化！");
				logger.error(info.toString());
				System.exit(-1);
			}
		} 
		catch (Exception e) {
			logger.error("系统安全管理模块初始化异常，系统终止初始化！", e);
			System.exit(-1);
		}
	}
	
	/**
	 * <p>检查用户请求URI是否是合法的授权访问</p>
	 *
	 * @param uri  待检查的URI
	 * @param request  当前的ServletRequest对象
	 * 
	 * @return  true - 有访问权限， false - 无访问权限
	 */
	public boolean securityURICheck(String uri, ServletRequest request){
		//1. 判断是否为null
		if(uri == null){
			return false;
		}
		
		//2. 判断URI的模式是否符合过滤审查要求
		//示例：URI=webfrm?tid=AC175xjllmxbBLH_init
		//示例：URI=ajax?tid=AC175xjllmxbBLH_init
		//示例：URI=webfrm?page=acct/pzgl/pzlr.jsp
		if(!uri.startsWith("webfrm?tid")
				&& !uri.startsWith("ajax?tid")
				&& !uri.startsWith("webfrm?page")){
			return true;
		}
		
		//3. 从URI中摘取permid字符串
		int posB = uri.indexOf("=");
		int posE = uri.indexOf("&");
		if(posE < 0){
			posE = uri.length();
		}
		String permid = uri.substring(posB+1, posE);
		
		return securityPermidCheck(permid, request);
	}
	
	/**
	 * <p>检查当前用户是否拥有指定功能权限</p>
	 *
	 * @param permid  功能权限标识
	 * @param request  当前的ServletRequest对象
	 * @return  true - 有访问权限， false - 无访问权限
	 */
	public boolean securityPermidCheck(String permid, ServletRequest request){
		logger.debug("开始检查当前用户是否拥有指定功能权限...");
		
		//1. 检查permid是否一个受控功能,如果不是，则返回true
		Iterator<FuncRightPrincipal> iterPrincipal = funcPrincipalPool.values().iterator();
		boolean toBeCheck = false;
		while(iterPrincipal.hasNext()){
			FuncRightPrincipal principal = iterPrincipal.next();
			if(permid.equals(principal.getFuncRightRes().getPermId())){
				toBeCheck = true;
				break;
			}
		}
		if(!toBeCheck){
			logger.debug("运行时权限检查：" + permid + " 不是一项受控的功能权限！");
			return true;
		}
		
		//2. 检查当前用户是否具有该功能的访问权限
		//2.1 获取当前用户的授权凭证
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpSession httpsession = httpRequest.getSession();
		Ticket ticket = (Ticket)httpsession.getAttribute(Constants.SESSION_FLAG);
		if(ticket == null){
			logger.debug("运行时权限检查：没有找到用户的登录权限存根！");
			return false;
		}
		
		//2.2 检查当前用户是否是超级管理员，如果是超级管理员，不做权限校验控制，则直接返回true
		User user = ticket.getUser();
		if(user.isSuperadmin()){
			return true;
		}
		
		LogonSubject subject = ticket.getSubject();
		if(subject == null){
			logger.debug("运行时权限检查：没有找到用户的权限信息！");
			return false;
		}
		
		//2.3 检查当前是否具有权限
		boolean flag = this.checkPrincipal(permid, subject);
		
		logger.debug("检查当前用户是否拥有指定功能权限结束，返回状态：flag = " + flag);
		return flag;
	}
	
	
	/**
	 * 
	 * <p>获取数据权限控制SQL字符串</p>
	 *
	 * @param compCode        当前的工作环境参数：单位
	 * @param copyCode        当前的工作环境参数：帐套
	 * @param user            当前用户
	 * @param tableId         提供数据范围标识的字典表名称
	 * @param bizTable        被过滤的业务数据表名称
	 * @param PersistenceDAO  持久层访问对象
	 * 
	 * @return SQL查询过滤字符串
	 */
	public String createDataRightFilter(
			String compCode,
			String copyCode,
			User user,
			String tableId,
			String bizTable,
			PersistenceDAO dao) throws BaseCheckedException{
		
		String retStr = "";
		String codeField = ""; 
		String handlerClass = "";
		
		//0. 预处理，便于数据库检索时做字符串匹配
		tableId = tableId.toLowerCase().trim();
		bizTable = bizTable.toLowerCase().trim();
		
		//1. 检查指定的字典表是否作为系统的数据访问权限控制标识,如果不是，则返回空字符串
		String sql = "select table_id, code_field, handler_class from t_sys_table where table_id=? and is_power=1" ;
		List<Object> params = new ArrayList<Object>();
		params.add(tableId);
		ResultSet rs = dao.queryToCachedRowSetBySQL(sql, params);
		try {
			if(rs.next()){
				codeField = rs.getString("code_field");
				handlerClass = rs.getString("handler_class");
			}
			else{
				return "";
			}
		} catch (SQLException e) {
			logger.error("00000508: 检查指定的字典表是否作为系统的数据访问权限控制标识时出现异常！");
			throw new BaseCheckedException("00000508", e);
		}
		
		//2. 构造数据权限控制SQL字符串
		DataRightHandler drh = DataRightHandlerFactory.createDataRightHandler(handlerClass);
		retStr = drh.createDataRightFilter(compCode, copyCode, user, tableId, codeField, bizTable, dao);
		
		return retStr;
	}
	
	
	private boolean checkPrincipal(String permid, LogonSubject subject){
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> funcRightPrincipals = subject.getFuncPrincipals();
		boolean flag = false;
		Iterator<String> iterCompCode = funcRightPrincipals.keySet().iterator();
		while(iterCompCode.hasNext()){
			//单位
			String compCode = iterCompCode.next();
			Map<String,Map<String,Map<String,FuncRightPrincipal>>> mapComp = funcRightPrincipals.get(compCode);
			
			Iterator<String> iterCopyCode = mapComp.keySet().iterator();
			while(iterCopyCode.hasNext()){
				//帐套
				String copyCode = iterCopyCode.next();
				Map<String,Map<String,FuncRightPrincipal>> mapCopy = mapComp.get(copyCode);
				
				Iterator<String> iterModCode = mapCopy.keySet().iterator();
				while(iterModCode.hasNext()){
					//模块
					String modCode = iterModCode.next();
					Map<String,FuncRightPrincipal> mapMod = mapCopy.get(modCode);
					
					logger.debug("运行时权限检查：检查" + permid + "是否是功能模块" + compCode + "-" + copyCode + "-" + modCode + "内的权限！");
					
					Iterator<String> iterFuncid = mapMod.keySet().iterator();
					while(iterFuncid.hasNext()){
						String funcid = iterFuncid.next();
						FuncRightPrincipal principal = mapMod.get(funcid);

						if(permid.equals(principal.getFuncRightRes().getPermId())){
							flag = true;
							break;
						}
					}

					if(flag){
						break;
					}
				}
				
				if(flag){
					break;
				}
			}
			if(flag){
				break;
			}
		}
		
		return flag;
	}
	
	
}
