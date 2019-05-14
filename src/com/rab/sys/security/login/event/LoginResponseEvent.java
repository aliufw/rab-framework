package com.rab.sys.security.login.event;

import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.security.TicketImpl;

public class LoginResponseEvent extends BaseResponseEvent {

	/**
	 * –Ú¡–ªØ±‡∫≈
	 */
	private static final long serialVersionUID = 6498700488905073353L;

	private TicketImpl ticket;

	public TicketImpl getTicket() {
		return ticket;
	}

	public void setTicket(TicketImpl ticket) {
		this.ticket = ticket;
	}
	
}
