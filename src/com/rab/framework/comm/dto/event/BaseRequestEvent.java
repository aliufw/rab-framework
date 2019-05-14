package com.rab.framework.comm.dto.event;

import java.io.Serializable;

/**
 * 
 * <P>Title: BaseRequestEvent</P>
 * <P>Description: </P>
 * <P>����˵����DTO���������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-30</P>
 *
 */
public class BaseRequestEvent implements Serializable {

	/**
	 * ���л�����
	 */
	private static final long serialVersionUID = -1101754235032363173L;

	/**
	 * ����ID�ţ���Դ�ڷ�������ҵ������߼���� 
	 */
	protected String transactionID;
	
	/**
	 * �û���¼״̬����Դ�ڵ�¼��ϵͳ���ص�Ψһ��ʶ�ַ���
	 */
	protected String sessionID;
	
	/**
	 * ��ִ�еķ������˷�����ʶ
	 */
	private String method;

	/**
	 * ���ױ�ʶ��������ǰ��̨���׸��٣���Ҫ����������־��������������ҵ�����
	 */
	private String txId;
	
	/**
	 * ������
	 * 
	 * @param transactionID �����������ʶ��Դ�ڷ�������ҵ������߼���� 
	 * @param sessionID �û���¼״̬����Դ�ڵ�¼��ϵͳ���ص�Ψһ��ʶ�ַ���
	 */
	public BaseRequestEvent(String transactionID, String sessionID) {
		this.transactionID = transactionID;
		this.sessionID = sessionID;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}
	
}
