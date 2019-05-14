package com.rab.framework.comm.threadlocal;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <P>Title: ThreadLocalManager</P>
 * <P>Description: ThreadLocal对象管理器</P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-8</P>
 *
 */
public class ThreadLocalManager {
	/**
	 * ThreadLocal对象
	 */
	private static ThreadLocal<String> threadlocal = new  ThreadLocal<String>();
	
	/**
	 * 扩展的ThreadLocal对象，是一个Map容器
	 */
	private static Map<Object, Map<Object,Object>> threadLocalMap = new HashMap<Object, Map<Object,Object>>();
	
	/**
	 * 私有构造器，不允许从外部创建实例
	 */
	private ThreadLocalManager(){
	}
	
	/**
	 * 返回标准的线程相关的ThreadLocal对象
	 * 
	 * @return
	 */
	public static ThreadLocal<String> getThreadLocal(){
		return threadlocal;
	}
	
	/**
	 * 返回扩展的ThreadLocal对象，是一个Map容器
	 * 
	 * @return
	 */
	public static Map<Object,Object> getThreadLocalMap(){
		Map<Object,Object> localMap = threadLocalMap.get(Thread.currentThread());
		
		if(localMap == null){
			localMap = new HashMap<Object,Object>();
			threadLocalMap.put(Thread.currentThread(), localMap);
		}
		
		return localMap;
	}
	
}
