package com.rab.framework.web.dynamicsession;

import java.io.Serializable;

/**
 * 
 * <P>Title: SessionReplicationWrapper</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>动态Session管理数据复制包装类</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class SessionReplicationWrapper implements Serializable {
	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = -3388188184350827468L;

	/**
	 * 用户身份标示，用sessionid表示
	 */
	private String sessionid;

	/**
	 * Session key
	 */
	private String key;
	
	/**
	 * Session value
	 */
	private Object Value;

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return Value;
	}

	public void setValue(Object value) {
		Value = value;
	}
	

}
