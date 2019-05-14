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
 * <P>����˵����</P>
 * <P> ��ȫ�������</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public abstract class BaseAuthorizationManager {
    /**
     * ��ʼ����־��¼����
     */
	private final static LogWritter logger = LogFactory.getLogger(BaseAuthorizationManager.class);
	/**
	 * ȫ�ֵĹ���Ȩ��ƾ֤�洢��,���ڽ���ڴ��й���Ȩ��ƾ֤����ʵ�����ظ���������
	 */
	protected final static Map<String,FuncRightPrincipal> funcPrincipalPool = new HashMap<String,FuncRightPrincipal>();
	
	
//	/**
//	 * ȫ�ֵ�����Ȩ��ƾ֤�洢��,���ڽ���ڴ�������Ȩ��ƾ֤����ʵ�����ظ��������� <String,DefaultDataRightPrincipal>
//	 */
//	protected final static Map<String,Map<String,DefaultDataRightPrincipal>> dataPrincipalPool = new HashMap<String,Map<String,DefaultDataRightPrincipal>>();
	
//	private ITicketManager ticketManager;
//	
//	private static String PARENT_SIGN = "";
	
	/**
	 * ��Ȩ����
	 * @param userid �û����
	 * @param password �û�����
	 * @return ������ǰ�û�����Ȩ�޵ĵ�½�������
	 */
	public Ticket accredit(String userid, String password) throws BaseCheckedException{
	
		//0. ��¼
		User user = login(userid, password);
		logger.info("��¼�ɹ���userid = " + userid);
		
		//3. ���ɹ���Ȩ��ƾ֤
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> allFuncPrincipals = this.createFuncPrincipals(user);
		
		
		LogonSubject subject = new LogonSubject(user.getUsercode());
		subject.setFuncPrincipals(allFuncPrincipals);
		
		//4. ����userSessionId
		String sessionid = this.createSessionId(user.getUsercode());
		logger.info("����sessionid�ɹ���sessionid = " + sessionid);
		
		//5. ���ɵ�¼���
		TicketImpl ticket = new TicketImpl();
		ticket.setLogonTime(System.currentTimeMillis());
		ticket.setSubject(subject);
		ticket.setUser(user);
		ticket.setUserSessionid(sessionid);
		
		//6.�����¼���   
		CacheSessionManagerImpl.singleton().getCacheSession(sessionid).setValue("ticket", ticket); 

		logger.info("��¼�ɹ�������userid = " + userid + ", sessionid = " + sessionid);
		
		return ticket;
	}
	
	/**
	 * ȡ����Ȩ�ޣ������û���ʶ�����ɹ���Ȩ��ƾ֤
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
		
		//�������ݶ��󣺵�λ
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> allFuncPrincipals = new HashMap<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>>();
		
		Iterator<String> iterCompCode = funcRights.keySet().iterator();
		while(iterCompCode.hasNext()){
			//ö�ٵ�λ
			String compCode = iterCompCode.next();
			Map<String,Map<String, List<FuncRightResource>>> mapComp = funcRights.get(compCode);
			
			//�ڵ�λ��ö������
			//�������ݶ�������
			Map<String,Map<String,Map<String,FuncRightPrincipal>>> mapSOB = new HashMap<String,Map<String,Map<String,FuncRightPrincipal>>>();

			Iterator<String> iterCopyCode = mapComp.keySet().iterator();
			while(iterCopyCode.hasNext()){
				//��������ö��ģ��
				String copyCode = iterCopyCode.next();
				Map<String, List<FuncRightResource>> mapCopy = mapComp.get(copyCode);				
				
				//�������ݶ���ģ��
				Map<String,Map<String,FuncRightPrincipal>> mapModel = new HashMap<String,Map<String,FuncRightPrincipal>>();
				if(mapCopy != null && mapCopy.size() > 0){
					Iterator<String> iterModCode = mapCopy.keySet().iterator();
					while(iterModCode.hasNext()){
						String modCode = iterModCode.next();
						List<FuncRightResource> list = mapCopy.get(modCode);
						
//						System.out.print("����ǰ�� ");
//						for(int i=0; i<list.size(); i++){
//							FuncRightResource frr = list.get(i);
//							System.out.print(frr.getSortId() + ", ");
//						}
//						System.out.println();
//						//����
//						Collections.sort(list, new MenuNodeComparator());
//						System.out.print("����� ");
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
							
							//�������ڴ����ظ�������ͬ��ƾ֤ʵ��
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
	 * ���ڼ�Ⱥ����ͬ��ʱ�ظ����
	 * 
	 * �����ظ���ƾ֤���ݣ��Խ���ϵͳ���ڴ�ռ��
	 * �������������������Ҫ��ΪDefaultTicket��������������Ҫ��һ�£�����д�÷�����
	 * @param ticket DefaultTicket
	 * @return 
	 */
	public static Ticket dealRepeatedPrincipal(Ticket ticket){
		if(! (ticket instanceof TicketImpl)){
			logger.error("�������������������Ĭ��Ҫ��ΪDefaultTicket��������������Ҫ��һ�£�" +
					"����д���� public ITicket dealRepeatedPrincipal(ITicket ticket)," +
					"����bootstrap.xml�ļ�������������");
			return ticket;
		}
		LogonSubject subject = (LogonSubject)ticket.getSubject();
		
		//������Ȩ��
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> allFuncPrincipals = subject.getFuncPrincipals();
		
		//��λö����
		Iterator<Map<String,Map<String,Map<String,FuncRightPrincipal>>>> iterOrg = allFuncPrincipals.values().iterator();
		while(iterOrg.hasNext()){
			Map<String,Map<String,Map<String,FuncRightPrincipal>>> mapOrg = iterOrg.next();
			
			//����ö����
			Iterator<Map<String,Map<String,FuncRightPrincipal>>> iterSOB = mapOrg.values().iterator();
			while(iterSOB.hasNext()){
				Map<String,Map<String,FuncRightPrincipal>> mapSOB = iterSOB.next();
				
				//ģ��ö����
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
	 * ���ڼ�Ⱥ�����µ�ƾ֤����
	 * ��鹦��Ȩ��ƾ֤
	 * 
	 * @param listPrincipals
	 * @return
	 */
	private static Map<String,FuncRightPrincipal> createFuncPrincipals2(List<FuncRightPrincipal> listPrincipals){ 
		
		Map<String,FuncRightPrincipal> funcPrincipals = new HashMap<String,FuncRightPrincipal>();
		for(int i=0; i<listPrincipals.size(); i++){
			FuncRightPrincipal funcPrincipal = listPrincipals.get(i);
			
			//��鲢�����ظ���
			//�������ڴ����ظ�������ͬ��ƾ֤ʵ��
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
	 * ִ�е�½����,��鵱ǰ���û���/�����Ƿ���ȷ
	 * 
	 * @param userid �û���
	 * @param password ����
	 * @return ��ǩ���û�������ʶ��Ϣ
	 */
	protected abstract User login(String usercode, String password) throws BaseCheckedException;
	
	/**
	 * ȡ����Ȩ��
	 * 
	 * @param user �û�����,���а����˸��û��������Ϣ
	 * @return ���ض�Ӧ��Ȩ����Դ��������ʽ��������Ϣ���б�
	 */
	protected abstract Map<String,Map<String,Map<String, List<FuncRightResource>>>> getFuncRight(User user)  throws BaseCheckedException;

	/**
	 * �����û���EJB Sessionid����
	 * @param userid �û����
	 * @return ����Sessionid
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
