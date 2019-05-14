package com.rab.framework.comm.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rab.framework.comm.cachesession.CacheSessionManagerImpl;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: VHSecurityManager</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P> 安全管理基类</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public abstract class BaseAuthorizationManager {
    /**
     * 初始化日志记录对象
     */
	private final static LogWritter logger = LogFactory.getLogger(BaseAuthorizationManager.class);
	/**
	 * 全局的功能权限凭证存储池,用于解决内存中功能权限凭证对象实例的重复创建问题
	 */
	protected final static Map<String,FuncRightPrincipal> funcPrincipalPool = new HashMap<String,FuncRightPrincipal>();
	
	
//	/**
//	 * 全局的数据权限凭证存储池,用于解决内存中数据权限凭证对象实例的重复创建问题 <String,DefaultDataRightPrincipal>
//	 */
//	protected final static Map<String,Map<String,DefaultDataRightPrincipal>> dataPrincipalPool = new HashMap<String,Map<String,DefaultDataRightPrincipal>>();
	
//	private ITicketManager ticketManager;
//	
//	private static String PARENT_SIGN = "";
	
	/**
	 * 授权操作
	 * @param userid 用户编号
	 * @param password 用户口令
	 * @return 包含当前用户所有权限的登陆存根对象
	 */
	public Ticket accredit(String userid, String password) throws BaseCheckedException{
	
		//0. 登录
		User user = login(userid, password);
		logger.info("登录成功！userid = " + userid);
		
		//3. 生成功能权限凭证
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> allFuncPrincipals = this.createFuncPrincipals(user);
		
		
		LogonSubject subject = new LogonSubject(user.getUsercode());
		subject.setFuncPrincipals(allFuncPrincipals);
		
		//4. 生成userSessionId
		String sessionid = this.createSessionId(user.getUsercode());
		logger.info("生成sessionid成功！sessionid = " + sessionid);
		
		//5. 生成登录存根
		TicketImpl ticket = new TicketImpl();
		ticket.setLogonTime(System.currentTimeMillis());
		ticket.setSubject(subject);
		ticket.setUser(user);
		ticket.setUserSessionid(sessionid);
		
		//6.缓存登录存根   
		CacheSessionManagerImpl.singleton().getCacheSession(sessionid).setValue("ticket", ticket); 

		logger.info("登录成功结束！userid = " + userid + ", sessionid = " + sessionid);
		
		return ticket;
	}
	
	/**
	 * 取功能权限，根据用户标识，生成功能权限凭证
	 * 
	 * @param user
	 * @return
	 * @throws SecurityCheckException
	 */
	private Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> createFuncPrincipals(User user) throws BaseCheckedException{
//		List<BaseFuncRightResource> funcRights = this.getFuncRight(user);
		Map<String,Map<String,Map<String, List<FuncRightResource>>>> funcRights = this.getFuncRight(user);
		if(funcRights == null){
			return new HashMap<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>>();
		}
		
		//返回数据对象：单位
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> allFuncPrincipals = new HashMap<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>>();
		
		Iterator<String> iterCompCode = funcRights.keySet().iterator();
		while(iterCompCode.hasNext()){
			//枚举单位
			String compCode = iterCompCode.next();
			Map<String,Map<String, List<FuncRightResource>>> mapComp = funcRights.get(compCode);
			
			//在单位下枚举帐套
			//返回数据对象：帐套
			Map<String,Map<String,Map<String,FuncRightPrincipal>>> mapSOB = new HashMap<String,Map<String,Map<String,FuncRightPrincipal>>>();

			Iterator<String> iterCopyCode = mapComp.keySet().iterator();
			while(iterCopyCode.hasNext()){
				//在帐套下枚举模块
				String copyCode = iterCopyCode.next();
				Map<String, List<FuncRightResource>> mapCopy = mapComp.get(copyCode);				
				
				//返回数据对象：模块
				Map<String,Map<String,FuncRightPrincipal>> mapModel = new HashMap<String,Map<String,FuncRightPrincipal>>();
				if(mapCopy != null && mapCopy.size() > 0){
					Iterator<String> iterModCode = mapCopy.keySet().iterator();
					while(iterModCode.hasNext()){
						String modCode = iterModCode.next();
						List<FuncRightResource> list = mapCopy.get(modCode);
						
//						System.out.print("排序前： ");
//						for(int i=0; i<list.size(); i++){
//							FuncRightResource frr = list.get(i);
//							System.out.print(frr.getSortId() + ", ");
//						}
//						System.out.println();
//						//排序
//						Collections.sort(list, new MenuNodeComparator());
//						System.out.print("排序后： ");
//						for(int i=0; i<list.size(); i++){
//							FuncRightResource frr = list.get(i);
//							System.out.print(frr.getSortId() + ", ");
//						}
//						System.out.println();

						
						Map<String,FuncRightPrincipal> funcPrincipals = new HashMap<String,FuncRightPrincipal>();
						for(int i=0; i<list.size(); i++){
							FuncRightResource funcRes = list.get(i);
							FuncRightPrincipal principal = new FuncRightPrincipal();
							principal.setRightResource(funcRes);
							
							//避免在内存中重复保存相同的凭证实例
							if(funcPrincipalPool.containsKey(principal.getPrincipalId())){
								principal = (FuncRightPrincipal)funcPrincipalPool.get(principal.getPrincipalId());
							}
							else{
								funcPrincipalPool.put(principal.getPrincipalId(), principal);
							}
							
							funcPrincipals.put(principal.getPrincipalId(), principal); 
						}
						
						mapModel.put(modCode, funcPrincipals);
						
					}
				}			
				
				mapSOB.put(copyCode, mapModel);
			}
			
			allFuncPrincipals.put(compCode, mapSOB);
		}
		
		return allFuncPrincipals;
	}
	

	/**
	 * 用于集群复制同步时重复检查
	 * 
	 * 处理重复的凭证内容，以降低系统的内存占用
	 * 本方法的输入参数类型要求为DefaultTicket，如果输入参数和要求不一致，请重写该方法。
	 * @param ticket DefaultTicket
	 * @return 
	 */
	public static Ticket dealRepeatedPrincipal(Ticket ticket){
		if(! (ticket instanceof TicketImpl)){
			logger.error("本方法的输入参数类型默认要求为DefaultTicket，如果输入参数和要求不一致，" +
					"请重写方法 public ITicket dealRepeatedPrincipal(ITicket ticket)," +
					"并在bootstrap.xml文件中做声明配置");
			return ticket;
		}
		LogonSubject subject = (LogonSubject)ticket.getSubject();
		
		//处理功能权限
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> allFuncPrincipals = subject.getFuncPrincipals();
		
		//单位枚举器
		Iterator<Map<String,Map<String,Map<String,FuncRightPrincipal>>>> iterOrg = allFuncPrincipals.values().iterator();
		while(iterOrg.hasNext()){
			Map<String,Map<String,Map<String,FuncRightPrincipal>>> mapOrg = iterOrg.next();
			
			//帐套枚举器
			Iterator<Map<String,Map<String,FuncRightPrincipal>>> iterSOB = mapOrg.values().iterator();
			while(iterSOB.hasNext()){
				Map<String,Map<String,FuncRightPrincipal>> mapSOB = iterSOB.next();
				
				//模块枚举器
				Iterator<Map<String,FuncRightPrincipal>> iterModel = mapSOB.values().iterator();
				while(iterModel.hasNext()){
					Map<String,FuncRightPrincipal> funcPrincipals = iterModel.next();
					Iterator<FuncRightPrincipal> iterFuncPrincipals = funcPrincipals.values().iterator();
					List<FuncRightPrincipal> listFuncPrincipals = new ArrayList<FuncRightPrincipal>();
					while(iterFuncPrincipals.hasNext()){
						listFuncPrincipals.add(iterFuncPrincipals.next());
					}
					
					funcPrincipals = createFuncPrincipals2(listFuncPrincipals);
				}
			}
		}
		
		subject.setFuncPrincipals(allFuncPrincipals);
		
		return ticket;
	}

	/**
	 * 用于集群环境下的凭证复制
	 * 检查功能权限凭证
	 * 
	 * @param listPrincipals
	 * @return
	 */
	private static Map<String,FuncRightPrincipal> createFuncPrincipals2(List<FuncRightPrincipal> listPrincipals){ 
		
		Map<String,FuncRightPrincipal> funcPrincipals = new HashMap<String,FuncRightPrincipal>();
		for(int i=0; i<listPrincipals.size(); i++){
			FuncRightPrincipal funcPrincipal = listPrincipals.get(i);
			
			//检查并处理重复！
			//避免在内存中重复保存相同的凭证实例
			if(funcPrincipalPool.containsKey(funcPrincipal.getPrincipalId())){
				funcPrincipal = (FuncRightPrincipal)funcPrincipalPool.get(funcPrincipal.getPrincipalId());
			}
			else{
				funcPrincipalPool.put(funcPrincipal.getPrincipalId(), funcPrincipal);
			}
			
			funcPrincipals.put(funcPrincipal.getPrincipalId(), funcPrincipal); 
		}

		return funcPrincipals;
	}
	
	/**
	 * 执行登陆操作,检查当前的用户名/口令是否正确
	 * 
	 * @param userid 用户名
	 * @param password 口令
	 * @return 方签该用户的身奔标识信息
	 */
	protected abstract User login(String usercode, String password) throws BaseCheckedException;
	
	/**
	 * 取功能权限
	 * 
	 * @param user 用户对象,其中包含了该用户的相关信息
	 * @return 返回对应的权限资源及操作方式描述等信息的列表
	 */
	protected abstract Map<String,Map<String,Map<String, List<FuncRightResource>>>> getFuncRight(User user)  throws BaseCheckedException;

	/**
	 * 生成用户的EJB Sessionid生成
	 * @param userid 用户编号
	 * @return 返回Sessionid
	 */
    protected String createSessionId(String userid) {
    	String s = "" + Math.random();
    	String postfix = s.substring(s.length()-4);
        return userid + System.currentTimeMillis() + "$" + postfix;
    }
    

	public static Map<String,FuncRightPrincipal> getFuncPrincipalPool() {
		return funcPrincipalPool;
	}

}
