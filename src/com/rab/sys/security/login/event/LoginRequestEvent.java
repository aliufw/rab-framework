package com.rab.sys.security.login.event;

import com.rab.framework.comm.dto.event.SysRequestEvent;
import com.rab.framework.comm.security.LogonEnvironment;

public class LoginRequestEvent extends SysRequestEvent {

	/**
	 * ���л����
	 */
	private static final long serialVersionUID = -3329944204758148946L;

	/**
	 * �û�ID
	 */
	private String usercode;
	
	/**
	 * �û�����
	 */
	private String passwd;
	
	/**
	 * �û���¼ϵͳ��Ļ�������
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
