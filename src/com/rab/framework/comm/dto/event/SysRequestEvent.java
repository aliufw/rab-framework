package com.rab.framework.comm.dto.event;

public class SysRequestEvent extends BaseRequestEvent{

	/**
	 * ���л����
	 */
	private static final long serialVersionUID = -2198908861992832474L;

	/**
	 * ϵͳ����ı����Ϣ
	 */
	private boolean sysfunction = true;
	
	/**
	 * ������
	 * 
	 * @param transactionID �����������ʶ��Դ�ڷ�������ҵ������߼����
	 */
	public SysRequestEvent(String transactionID) {
		super(transactionID, "sys-sessionid");
	}
	
//	/**
//	 * ������
//	 * 
//	 * @param transactionID �����������ʶ��Դ�ڷ�������ҵ������߼���� 
//	 * @param sessionID �û���¼״̬����Դ�ڵ�¼��ϵͳ���ص�Ψһ��ʶ�ַ���
//	 */
//	public SysRequestEvent(String transactionID, String sessionID) {
//		super(transactionID, sessionID);
//	}

	public boolean isSysfunction() {
		return sysfunction;
	}

}
