package com.rab.framework.comm.threadlocal;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <P>Title: ThreadLocalManager</P>
 * <P>Description: ThreadLocal���������</P>
 * <P>����˵����</P>
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
	 * ThreadLocal����
	 */
	private static ThreadLocal<String> threadlocal = new  ThreadLocal<String>();
	
	/**
	 * ��չ��ThreadLocal������һ��Map����
	 */
	private static Map<Object, Map<Object,Object>> threadLocalMap = new HashMap<Object, Map<Object,Object>>();
	
	/**
	 * ˽�й���������������ⲿ����ʵ��
	 */
	private ThreadLocalManager(){
	}
	
	/**
	 * ���ر�׼���߳���ص�ThreadLocal����
	 * 
	 * @return
	 */
	public static ThreadLocal<String> getThreadLocal(){
		return threadlocal;
	}
	
	/**
	 * ������չ��ThreadLocal������һ��Map����
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
