package com.rab.framework.web.action.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.rab.framework.comm.security.LogonEnvironment;
import com.rab.framework.comm.security.LogonSubject;
import com.rab.framework.comm.security.Ticket;
import com.rab.framework.comm.security.User;
import com.rab.framework.comm.util.Constants;

/**
 * 
 * <P>Title: BaseAction</P>
 * <P>Description: </P>
 * <P>³ÌÐòËµÃ÷£º</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-11</P>
 *
 */
public class BaseAction{
	protected String getVHSessionId(){
		Ticket ticket = getTicket();
		String vhSessionId = null;
		if(ticket != null){
			vhSessionId = ticket.getUserSessionid();
		}
		
		return vhSessionId;
	}
	
	protected User getUser(){
		Ticket ticket = getTicket();
		User user = null;
		if(ticket != null){
			user = ticket.getUser();
		}
		return user;
	}

	protected LogonSubject getSubject(){
		Ticket ticket = getTicket();
		LogonSubject subject = null;
		if(ticket != null){
			subject = ticket.getSubject();
		}
		return subject;
	}
	
	protected LogonEnvironment getLogonEnvironment(){
		Ticket ticket = getTicket();
		LogonEnvironment env = null;
		if(ticket != null){
			env = ticket.getLogonEnv();
		}
		if(env == null){
			env = new LogonEnvironment();
			ticket.setLogonEnv(env);
		}
		return env;
	}
	
	private Ticket getTicket(){
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession httpsession = request.getSession();
		Ticket ticket = (Ticket)httpsession.getAttribute(Constants.SESSION_FLAG);
		return ticket;
	}
}
