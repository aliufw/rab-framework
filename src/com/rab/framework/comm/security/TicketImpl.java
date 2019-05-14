package com.rab.framework.comm.security;


/**
 * 
 * <P>Title: DefaultTicket</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>系统缺省的登陆存根</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class TicketImpl implements Ticket {

	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 8754608931736030271L;

	/**
	 * user Sessionid属性
	 */
	private String userSessionid = "";
	
	/**
	 * 用户对象
	 */
	private User user = null;
	
	/**
	 * 授权信息
	 */
	private LogonSubject subject = null;
	
	/**
	 * 登录后的环境变量信息
	 */
	private LogonEnvironment logonEnv = null;
	
	/**
	 * 用户登录时间
	 */
	private long logonTime = 0l;
	
	/**
	 * 用户最后一次访问时间
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
