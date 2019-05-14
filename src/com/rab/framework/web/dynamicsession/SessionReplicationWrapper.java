package com.rab.framework.web.dynamicsession;

import java.io.Serializable;

/**
 * 
 * <P>Title: SessionReplicationWrapper</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>��̬Session�������ݸ��ư�װ��</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class SessionReplicationWrapper implements Serializable {
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = -3388188184350827468L;

	/**
	 * �û���ݱ�ʾ����sessionid��ʾ
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
