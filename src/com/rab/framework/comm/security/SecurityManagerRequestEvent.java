package com.rab.framework.comm.security;

import com.rab.framework.comm.dto.event.SysRequestEvent;

/**
 * 
 * <P>Title: SecurityManagerRequestEvent</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-9</P>
 *
 */
public class SecurityManagerRequestEvent extends SysRequestEvent {
	/**
	 * ���л���ʶ
	 */
	private static final long serialVersionUID = 3602198164288649609L;

	/**
	 * ������
	 * @param transactionID
	 * @param sessionID
	 */
	public SecurityManagerRequestEvent(String transactionID) {
		super(transactionID);
	}

}
