package com.rab.framework.web.dynamicsession;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * <P>Title: DynamicSessionTest</P>
 * <P>Description: </P>
 * <P>³ÌÐòËµÃ÷£º</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class DynamicSessionTest {

	public void setData(HttpServletRequest request, String key, Object value){
		DynamicSessionManager.singleton().setData(request, key, value);
	}
	
	public void test(){
		Map<String, MemorySession> pool = DynamicSessionManager.singleton().getDataCachePool();
		Iterator<String> memorySessionIter = pool.keySet().iterator();
		while(memorySessionIter.hasNext()){
			String sessionid = "" + memorySessionIter.next();
			System.out.println(sessionid + "------------------------------------------");

			MemorySession memorySession = (MemorySession)pool.get(sessionid);
			Map<String,SessionData> memoryPool = memorySession.getPool();
			Iterator<String> sessionDataIter = memoryPool.keySet().iterator();
			while(sessionDataIter.hasNext()){
				String key = "" + sessionDataIter.next();
				SessionData sessionData = (SessionData)memoryPool.get(key);
				
				Object data = sessionData.getValue();
				long lastAccess = sessionData.getLastAccess();

				System.out.println(key + " = " + key);
				if(data == null){
					System.out.println(data + " = " + data);
				}
				else{
					System.out.println(data + " (class)= " + data.getClass());
				}
				System.out.println("lastAccess = " + lastAccess);
			}
		}
	}

	public static void main(String[] args) {

	}

}
