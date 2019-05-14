package com.rab.framework.web.dynamicsession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <P>Title: MemorySession</P>
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
public class MemorySession implements Serializable {
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 4305879453099814416L;

	/**
	 * ������������
	 */
	private Map<String,SessionData> pool = new HashMap<String,SessionData>();

	/**
	 * HttpServlet Session����
	 */
	private String sessionid;

	/**
	 * ��������
	 * 
	 * @param key
	 * @param value
	 */
	public void setData(String key, Object value){
		SessionData sessionData = new SessionData();
		sessionData.setValue(value);
		sessionData.setKey(key);
		sessionData.setLastAccess(System.currentTimeMillis());
		
		this.pool.put(key, sessionData);
	}
	
	/**
	 * ��ȡ����
	 * 
	 * @param key
	 * @return
	 */
	public Object getData(String key){
		SessionData sessionData = (SessionData)this.getPool().get(key);
		sessionData.setLastAccess(System.currentTimeMillis());
		
		if(sessionData == null){
			return null;
		}
		
		return sessionData.getValue();
	}
	

	public Map<String,SessionData> getPool() {
		return pool;
	}

	public void setPool(Map<String,SessionData> pool) {
		this.pool = pool;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	
}
