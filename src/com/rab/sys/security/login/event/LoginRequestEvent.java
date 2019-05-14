package com.rab.sys.security.login.event;

import com.rab.framework.comm.dto.event.SysRequestEvent;
import com.rab.framework.comm.security.LogonEnvironment;

public class LoginRequestEvent extends SysRequestEvent {

	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = -3329944204758148946L;

	/**
	 * 用户ID
	 */
	private String usercode;
	
	/**
	 * 用户口令
	 */
	private String passwd;
	
	/**
	 * 用户登录系统后的环境变量
	 */
	private LogonEnvironment logonEnvironment;
	
	public LoginRequestEvent(String transactionID) {
		super(transactionID);
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}



	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	/**
	 * @return the logonEnvironment
	 */
	public LogonEnvironment getLogonEnvironment() {
		return logonEnvironment;
	}

	/**
	 * @param logonEnvironment the logonEnvironment to set
	 */
	public void setLogonEnvironment(LogonEnvironment logonEnvironment) {
		this.logonEnvironment = logonEnvironment;
	}

}
