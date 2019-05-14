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
 * <P>程序说明：</P>
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
	 * 日志记录器
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

	/**
	 * 数据缓存
	 */
	private Map<String, MemorySession> dataCachePool = new HashMap<String, MemorySession>();
	
	/**
	 * 单例变量
	 */
	private static DynamicSessionManager instance = null;
	
	/**
	 * 默认动态session变量超时时间,具体时间由DynamicSessionServlet中的配置参数 session-timeout决定
	 */
	private int timeout = 5;  //单位: 分钟
	
	/**
	 * 管理线程循环周期
	 * 
	 */
	private int cyc = 3; //单位: 分钟
	
	/**
	 * 私有构造器
	 *
	 */
	private DynamicSessionManager(){
		init();
	}
	
	/**
	 * 初始化
	 *
	 */
	private void init(){

		//创建并启动监听进程
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
	 * 保存数据
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
		
		//集群复制！
        Properties prop = (Properties)ApplicationContext.singleton().getValueByKey("multicast");
    	String state = prop.getProperty("state");
    	if (state != null && state.equalsIgnoreCase("on")) {
    		SessionReplicationManager.singleton().dataSend(sessionid, key, value);
//    		log.debug("发送包：sessionid = " + sessionid);
//    		log.debug("发送包：key       = " + key);
//    		log.debug("发送包：value     = " + value);
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
	 * 读取数据
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
		
//		log.debug("从DynamicSession中取数据：sessionid = " + sessionid);
//		log.debug("从DynamicSession中取数据：key = " + key);
		MemorySession memorySession = (MemorySession)this.dataCachePool.get(sessionid);
//		log.debug("从DynamicSession中取数据：memorySession = " + memorySession);
		
		if(memorySession == null){
			return null;
		}
//		log.debug("从DynamicSession中取数据：value = " + memorySession.getData(key));
		
		return memorySession.getData(key);
	}
	
	
	protected Map<String, MemorySession> getDataCachePool(){
		return this.dataCachePool;
	}
	
	/**
	 * 检查超时变量,并清除之
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
					memorySession.getPool().remove(key); //超时,清除
					log.debug("清除动态session变量: key = " + key);
				}
			}
			
			synchronized(memorySession.getPool()){
				if(memorySession.getPool().isEmpty()){
					this.dataCachePool.remove(sessionid); //MemorySession中缓存数据为空时,彻底删除该对象
					log.debug("彻底清除动态session缓存变量容器 : sessionid = " + sessionid);
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
	 * 内部类,定时器,按照设定的时间触发超时检查逻辑
	 * 
	 * @author liufw
	 *
	 */
	class DynamicSessionMonitor extends Thread{

		public void run(){
			while(true){
				log.debug("开始检查动态session变量是否超时.... !");
				checkTimeout();
				log.debug("结束检查动态session变量是否超时!!!! !");
				try {
					sleep(cyc * 60 * 1000);
				} catch (Exception e) {
					log.error("动态session管理线程休眠时出现异常!", e);
				}
			}
		}
	}

}
