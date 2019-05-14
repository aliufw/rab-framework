package com.rab.framework.web.dynamicsession;

import java.io.Serializable;

/**
 * 
 * <P>Title: SessionData</P>
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
public class SessionData  implements Serializable {
	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 6522268583950545879L;

	/**
	 * 主键
	 */
	private String key;
	
	/**
	 * 值
	 */
	private Object value;
	
	/**
	 * 最后一次访问时间
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
