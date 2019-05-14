package com.rab.sys.security.login.event;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.security.LogonEnvironment;

public class ChangeModuleInfoRequestEvent extends BaseRequestEvent{
	
	
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 1136772441622259709L;
	
	/**
	 * �û���¼ϵͳ��Ļ�������
	 */
	private LogonEnvironment logonEnvironment;

	/**
	 * ������
	 * 
	 * @param transactionID �����������ʶ��Դ�ڷ�������ҵ������߼���� 
	 * @param sessionID �û���¼״̬����Դ�ڵ�¼��ϵͳ���ص�Ψһ��ʶ�ַ���
	 */
	public ChangeModuleInfoRequestEvent(String transactionID, String sessionID) {
		super(transactionID,sessionID);
	}

	public LogonEnvironment getLogonEnvironment() {
		return logonEnvironment;
	}

	public void setLogonEnvironment(LogonEnvironment logonEnvironment) {
		this.logonEnvironment = logonEnvironment;
	}

	
}
