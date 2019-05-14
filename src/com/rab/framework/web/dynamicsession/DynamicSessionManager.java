package com.rab.framework.web.dynamicsession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.Ticket;

/**
 * 
 * <P>Title: DynamicSessionManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class DynamicSessionManager {

	/**
	 * ��־��¼��
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

	/**
	 * ���ݻ���
	 */
	private Map<String, MemorySession> dataCachePool = new HashMap<String, MemorySession>();
	
	/**
	 * ��������
	 */
	private static DynamicSessionManager instance = null;
	
	/**
	 * Ĭ�϶�̬session������ʱʱ��,����ʱ����DynamicSessionServlet�е����ò��� session-timeout����
	 */
	private int timeout = 5;  //��λ: ����
	
	/**
	 * �����߳�ѭ������
	 * 
	 */
	private int cyc = 3; //��λ: ����
	
	/**
	 * ˽�й�����
	 *
	 */
	private DynamicSessionManager(){
		init();
	}
	
	/**
	 * ��ʼ��
	 *
	 */
	private void init(){

		//������������������
		DynamicSessionMonitor monitor = new DynamicSessionMonitor();
		monitor.setDaemon(true);
		monitor.start();   
	}
	
	public static DynamicSessionManager singleton(){
		if(instance == null){
			instance = new DynamicSessionManager();
		}
		
		return instance;
	}
	
	/**
	 * ��������
	 * 
	 * @param request
	 * @param key
	 * @param value
	 */
	public void setData(HttpServletRequest request, String key, Object value){
		String sessionid = request.getSession().getId();
		Ticket ticket = (Ticket)request.getSession().getAttribute("TICKET");
		if(ticket != null){
			sessionid = ticket.getUser().getUsercode();
		}
		
		this.setData(sessionid, key, value);
		
		//��Ⱥ���ƣ�
        Properties prop = (Properties)ApplicationContext.singleton().getValueByKey("multicast");
    	String state = prop.getProperty("state");
    	if (state != null && state.equalsIgnoreCase("on")) {
    		SessionReplicationManager.singleton().dataSend(sessionid, key, value);
//    		log.debug("���Ͱ���sessionid = " + sessionid);
//    		log.debug("���Ͱ���key       = " + key);
//    		log.debug("���Ͱ���value     = " + value);
        }
	}
	
	
	protected void setData(String sessionid, String key, Object value){
		MemorySession memorySession = (MemorySession)this.dataCachePool.get(sessionid);
		if(memorySession == null){
			memorySession = new MemorySession();
		}
		
		memorySession.setData(key, value);
		
		this.dataCachePool.put(sessionid, memorySession);
		
	}
	
	/**
	 * ��ȡ����
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public Object getData(HttpServletRequest request, String key){
		String sessionid = request.getSession().getId();
		Ticket ticket = (Ticket)request.getSession().getAttribute("TICKET");
		if(ticket != null){
			sessionid = ticket.getUser().getUsercode();
		}
		
//		log.debug("��DynamicSession��ȡ���ݣ�sessionid = " + sessionid);
//		log.debug("��DynamicSession��ȡ���ݣ�key = " + key);
		MemorySession memorySession = (MemorySession)this.dataCachePool.get(sessionid);
//		log.debug("��DynamicSession��ȡ���ݣ�memorySession = " + memorySession);
		
		if(memorySession == null){
			return null;
		}
//		log.debug("��DynamicSession��ȡ���ݣ�value = " + memorySession.getData(key));
		
		return memorySession.getData(key);
	}
	
	
	protected Map<String, MemorySession> getDataCachePool(){
		return this.dataCachePool;
	}
	
	/**
	 * ��鳬ʱ����,�����֮
	 *
	 */
	private void checkTimeout(){
		
		long timeoutMillis = this.timeout * 60 * 1000;
		long currentTime = System.currentTimeMillis();
		
//		System.out.println("--------------------------------------->>");
//		System.out.println("timeoutMillis = " + timeoutMillis);
//		System.out.println("currentTime = " + currentTime);
		
		Iterator<String> DynamicSessionIter = this.dataCachePool.keySet().iterator();
		List<String> sessionIds = new ArrayList<String>();
		while(DynamicSessionIter.hasNext()){
			sessionIds.add(DynamicSessionIter.next());
		}
		
		for(int dynamicSessionIndex=0; dynamicSessionIndex<sessionIds.size(); dynamicSessionIndex++){
			String sessionid = "" + sessionIds.get(dynamicSessionIndex);
			MemorySession memorySession = (MemorySession)this.dataCachePool.get(sessionid);
			if(memorySession == null){
				continue;
			}
			
			Iterator<String> memorySessionIter = memorySession.getPool().keySet().iterator();
			List<String> memorySessionkeys = new ArrayList<String>();
			while(memorySessionIter.hasNext()){
				memorySessionkeys.add(memorySessionIter.next());
			}
			
			for(int memorySessionIndex = 0; memorySessionIndex<memorySessionkeys.size(); memorySessionIndex++){
				String key = "" + memorySessionkeys.get(memorySessionIndex);
				SessionData sessionData = (SessionData)memorySession.getPool().get(key);
//				System.out.println("lastAccess = " + sessionData.getLastAccess());
//				System.out.println("(lastAccess + timeoutMillis) - currentTime = " + ((sessionData.getLastAccess() + timeoutMillis) - currentTime));
				if(sessionData.getLastAccess() + timeoutMillis < currentTime){
					memorySession.getPool().remove(key); //��ʱ,���
					log.debug("�����̬session����: key = " + key);
				}
			}
			
			synchronized(memorySession.getPool()){
				if(memorySession.getPool().isEmpty()){
					this.dataCachePool.remove(sessionid); //MemorySession�л�������Ϊ��ʱ,����ɾ���ö���
					log.debug("���������̬session����������� : sessionid = " + sessionid);
				}
			}
		}
//		System.out.println("---------------------------------------<<");

	}
	
	
	public int getTimeout() {
		return timeout;
	}

	protected void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	
	/**
	 * �ڲ���,��ʱ��,�����趨��ʱ�䴥����ʱ����߼�
	 * 
	 * @author liufw
	 *
	 */
	class DynamicSessionMonitor extends Thread{

		public void run(){
			while(true){
				log.debug("��ʼ��鶯̬session�����Ƿ�ʱ.... !");
				checkTimeout();
				log.debug("������鶯̬session�����Ƿ�ʱ!!!! !");
				try {
					sleep(cyc * 60 * 1000);
				} catch (Exception e) {
					log.error("��̬session�����߳�����ʱ�����쳣!", e);
				}
			}
		}
	}

}
