package com.rab.framework.web.dynamicsession;

import java.io.Serializable;

/**
 * 
 * <P>Title: SessionData</P>
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
public class SessionData  implements Serializable {
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 6522268583950545879L;

	/**
	 * ����
	 */
	private String key;
	
	/**
	 * ֵ
	 */
	private Object value;
	
	/**
	 * ���һ�η���ʱ��
	 */
	private long lastAccess;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	
	
}
