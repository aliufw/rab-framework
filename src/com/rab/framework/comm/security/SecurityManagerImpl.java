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
 * <P>����˵����</P>
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
				logger.error("ϵͳ��ȫ����ģ���ʼ���쳣��ϵͳ��ֹ��ʼ����");
				logger.error(info.toString());
				System.exit(-1);
			}
		} 
		catch (Exception e) {
			logger.error("ϵͳ��ȫ����ģ���ʼ���쳣��ϵͳ��ֹ��ʼ����", e);
			System.exit(-1);
		}
	}
	
	/**
	 * <p>����û�����URI�Ƿ��ǺϷ�����Ȩ����</p>
	 *
	 * @param uri  ������URI
	 * @param request  ��ǰ��ServletRequest����
	 * 
	 * @return  true - �з���Ȩ�ޣ� false - �޷���Ȩ��
	 */
	public boolean securityURICheck(String uri, ServletRequest request){
		//1. �ж��Ƿ�Ϊnull
		if(uri == null){
			return false;
		}
		
		//2. �ж�URI��ģʽ�Ƿ���Ϲ������Ҫ��
		//ʾ����URI=webfrm?tid=AC175xjllmxbBLH_init
		//ʾ����URI=ajax?tid=AC175xjllmxbBLH_init
		//ʾ����URI=webfrm?page=acct/pzgl/pzlr.jsp
		if(!uri.startsWith("webfrm?tid")
				&& !uri.startsWith("ajax?tid")
				&& !uri.startsWith("webfrm?page")){
			return true;
		}
		
		//3. ��URI��ժȡpermid�ַ���
		int posB = uri.indexOf("=");
		int posE = uri.indexOf("&");
		if(posE < 0){
			posE = uri.length();
		}
		String permid = uri.substring(posB+1, posE);
		
		return securityPermidCheck(permid, request);
	}
	
	/**
	 * <p>��鵱ǰ�û��Ƿ�ӵ��ָ������Ȩ��</p>
	 *
	 * @param permid  ����Ȩ�ޱ�ʶ
	 * @param request  ��ǰ��ServletRequest����
	 * @return  true - �з���Ȩ�ޣ� false - �޷���Ȩ��
	 */
	public boolean securityPermidCheck(String permid, ServletRequest request){
		logger.debug("��ʼ��鵱ǰ�û��Ƿ�ӵ��ָ������Ȩ��...");
		
		//1. ���permid�Ƿ�һ���ܿع���,������ǣ��򷵻�true
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
			logger.debug("����ʱȨ�޼�飺" + permid + " ����һ���ܿصĹ���Ȩ�ޣ�");
			return true;
		}
		
		//2. ��鵱ǰ�û��Ƿ���иù��ܵķ���Ȩ��
		//2.1 ��ȡ��ǰ�û�����Ȩƾ֤
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpSession httpsession = httpRequest.getSession();
		Ticket ticket = (Ticket)httpsession.getAttribute(Constants.SESSION_FLAG);
		if(ticket == null){
			logger.debug("����ʱȨ�޼�飺û���ҵ��û��ĵ�¼Ȩ�޴����");
			return false;
		}
		
		//2.2 ��鵱ǰ�û��Ƿ��ǳ�������Ա������ǳ�������Ա������Ȩ��У����ƣ���ֱ�ӷ���true
		User user = ticket.getUser();
		if(user.isSuperadmin()){
			return true;
		}
		
		LogonSubject subject = ticket.getSubject();
		if(subject == null){
			logger.debug("����ʱȨ�޼�飺û���ҵ��û���Ȩ����Ϣ��");
			return false;
		}
		
		//2.3 ��鵱ǰ�Ƿ����Ȩ��
		boolean flag = this.checkPrincipal(permid, subject);
		
		logger.debug("��鵱ǰ�û��Ƿ�ӵ��ָ������Ȩ�޽���������״̬��flag = " + flag);
		return flag;
	}
	
	
	/**
	 * 
	 * <p>��ȡ����Ȩ�޿���SQL�ַ���</p>
	 *
	 * @param compCode        ��ǰ�Ĺ���������������λ
	 * @param copyCode        ��ǰ�Ĺ�����������������
	 * @param user            ��ǰ�û�
	 * @param tableId         �ṩ���ݷ�Χ��ʶ���ֵ������
	 * @param bizTable        �����˵�ҵ�����ݱ�����
	 * @param PersistenceDAO  �־ò���ʶ���
	 * 
	 * @return SQL��ѯ�����ַ���
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
		
		//0. Ԥ�����������ݿ����ʱ���ַ���ƥ��
		tableId = tableId.toLowerCase().trim();
		bizTable = bizTable.toLowerCase().trim();
		
		//1. ���ָ�����ֵ���Ƿ���Ϊϵͳ�����ݷ���Ȩ�޿��Ʊ�ʶ,������ǣ��򷵻ؿ��ַ���
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
			logger.error("00000508: ���ָ�����ֵ���Ƿ���Ϊϵͳ�����ݷ���Ȩ�޿��Ʊ�ʶʱ�����쳣��");
			throw new BaseCheckedException("00000508", e);
		}
		
		//2. ��������Ȩ�޿���SQL�ַ���
		DataRightHandler drh = DataRightHandlerFactory.createDataRightHandler(handlerClass);
		retStr = drh.createDataRightFilter(compCode, copyCode, user, tableId, codeField, bizTable, dao);
		
		return retStr;
	}
	
	
	private boolean checkPrincipal(String permid, LogonSubject subject){
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> funcRightPrincipals = subject.getFuncPrincipals();
		boolean flag = false;
		Iterator<String> iterCompCode = funcRightPrincipals.keySet().iterator();
		while(iterCompCode.hasNext()){
			//��λ
			String compCode = iterCompCode.next();
			Map<String,Map<String,Map<String,FuncRightPrincipal>>> mapComp = funcRightPrincipals.get(compCode);
			
			Iterator<String> iterCopyCode = mapComp.keySet().iterator();
			while(iterCopyCode.hasNext()){
				//����
				String copyCode = iterCopyCode.next();
				Map<String,Map<String,FuncRightPrincipal>> mapCopy = mapComp.get(copyCode);
				
				Iterator<String> iterModCode = mapCopy.keySet().iterator();
				while(iterModCode.hasNext()){
					//ģ��
					String modCode = iterModCode.next();
					Map<String,FuncRightPrincipal> mapMod = mapCopy.get(modCode);
					
					logger.debug("����ʱȨ�޼�飺���" + permid + "�Ƿ��ǹ���ģ��" + compCode + "-" + copyCode + "-" + modCode + "�ڵ�Ȩ�ޣ�");
					
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
