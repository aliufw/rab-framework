package com.rab.framework.comm.security;


/**
 * 
 * <P>Title: DefaultTicket</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>ϵͳȱʡ�ĵ�½���</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class TicketImpl implements Ticket {

	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 8754608931736030271L;

	/**
	 * user Sessionid����
	 */
	private String userSessionid = "";
	
	/**
	 * �û�����
	 */
	private User user = null;
	
	/**
	 * ��Ȩ��Ϣ
	 */
	private LogonSubject subject = null;
	
	/**
	 * ��¼��Ļ���������Ϣ
	 */
	private LogonEnvironment logonEnv = null;
	
	/**
	 * �û���¼ʱ��
	 */
	private long logonTime = 0l;
	
	/**
	 * �û����һ�η���ʱ��
	 */
	private long lastAccessTime = 0l;
	
	public TicketImpl(){
		
	}
	
	public long getLastAccessTime() {
		this.lastAccessTime = System.currentTimeMillis();
		return lastAccessTime;
	}
	
	public long getLogonTime() {
		this.lastAccessTime = System.currentTimeMillis();
		
		return logonTime;
	}

	public void setLogonTime(long logonTime) {
		this.logonTime = logonTime;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setSubject(LogonSubject subject){
		this.lastAccessTime = System.currentTimeMillis();
		this.subject = subject;
	}
	
	public LogonSubject getSubject() {
		this.lastAccessTime = System.currentTimeMillis();
		return this.subject;
	}

	public User getUser() {
		this.lastAccessTime = System.currentTimeMillis();
		return this.user;
	}

	public String getUserSessionid() {
		this.lastAccessTime = System.currentTimeMillis();
		return this.userSessionid;
	}

	public void setUserSessionid(String userSessionid) {
		this.lastAccessTime = System.currentTimeMillis();
		this.userSessionid = userSessionid;
	}

	public LogonEnvironment getLogonEnv() {
		this.lastAccessTime = System.currentTimeMillis();
		return logonEnv;
	}

	public void setLogonEnv(LogonEnvironment logonEnv) {
		this.lastAccessTime = System.currentTimeMillis();
		this.logonEnv = logonEnv;
	}

}
