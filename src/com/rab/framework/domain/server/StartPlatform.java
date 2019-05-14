package com.rab.framework.domain.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.SecurityManagerFactory;
import com.rab.framework.component.dictcache.ServerCacheManager;
import com.rab.framework.component.multicast.MulticastServerManager;
import com.rab.framework.component.scheduler.TimerClock;

/**
 * 
 * <P>Title: StartPlatform</P>
 * <P>Description: </P>
 * <P>程序说明：平台启动入口</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class StartPlatform {

	public static boolean initApp_started = false;

	private static final LogWritter logger = LogFactory.getLogger(StartPlatform.class);

	public static void main(String args[]) {
		StartPlatform sp = new StartPlatform();
		sp.start();
	}
	
	public void start() {
		this.initServer();
		if (!initApp_started) {
			this.startInitApp();
		}
	}
	
	public void initServer() {
		// 启动时加载配置文件
		try {
			CoreAppServer.getInstance();
			logger.info("加载配置文件完毕.");
		} catch (Exception ex) {
			logger.error("无法加载配置文件", ex);
		}
	}

	public void startInitApp() {
		if (initApp_started) {
			return;
		}
		logger.info("启动类（服务）初始化......");
		List<Object> initApps = new ArrayList<Object>();

		// 1. 取代码表缓存管理
		Properties codecache = (Properties) ApplicationContext.singleton().getValueByKey("codecache");
		if (codecache != null
				&& codecache.getProperty("state").trim().toLowerCase().equals("on")) {
			initApps.add(ServerCacheManager.class.getName());
		}

		//2. 启动multicast复制监听器
		initApps.add(MulticastServerManager.class.getName());
		
		//3. 取功能权限凭证总集合
		initApps.add(SecurityManagerFactory.class.getName());
		
		//4. 启动定时任务管理器
		initApps.add(TimerClock.class.getName());
		
		
		//5. 加载扩展启动项
		Properties startapps = (Properties) ApplicationContext.singleton().getValueByKey("startapp");
		if(startapps != null){
			List<PositionInfo> lstApp = new ArrayList<PositionInfo>();
			Iterator<Object> iter = startapps.keySet().iterator();
			while(iter.hasNext()){
				String name = "" + iter.next();
				String key = name.substring("startup.".length());
				PositionInfo pi = new PositionInfo(key, startapps.getProperty(name));
				lstApp.add(pi);
			}
			//5.1 排序
			Collections.sort(lstApp, new PositionComparator<PositionInfo>());
			
			//5.2 初始化
			for(PositionInfo pi : lstApp){
				initApps.add(pi.getClassName());
			}
		}
		
		//6. 按照设定的加载顺序，启动程序！
		for (int i = 0; i < initApps.size(); i++) {
			String className = (String) initApps.get(i);
			logger.info(" 开始加载启动类[" + i + "]: className =  " + className);
			long t0 = System.currentTimeMillis();
			try {
				Class<?> claz = Class.forName(className);
				Object[] obj2 = new Object[1];
				obj2[0] = null;

				Class<?> paraType[] = new Class[1];
				paraType[0] = new String[0].getClass();

				// 调用器main方法
				Method main = claz.getMethod("main", paraType);
				
				main.invoke(null, obj2);

			} catch (Exception e) {
				logger.error("无法加载启动类[" + i + "]: " + className, e);
			}
			long t1 = System.currentTimeMillis();
			logger.info(" 加载启动类[" + i + "]耗时时间 t = " + (t1-t0) + " 毫秒");
			logger.info(" 加载启动类[" + i + "]结束： className =  " + className);
			
			
		}
		initApp_started = true;
	}

	class PositionComparator<StartPlatform$PositionInfo> implements Comparator<PositionInfo> {

		public int compare(PositionInfo obj1, PositionInfo obj2) {
			int pos1 = Integer.parseInt(obj1.getKey());
			int pos2 = Integer.parseInt(obj2.getKey());

			return pos1 - pos2;
		}
	}

	class PositionInfo {

		private String key;
		private String className;

		public PositionInfo(String key, String className) {
			this.key = key;
			this.className = className;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
	}
}