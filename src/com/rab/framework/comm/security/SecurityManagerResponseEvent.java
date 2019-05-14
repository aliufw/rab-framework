package com.rab.framework.comm.security;

import java.util.HashMap;
import java.util.Map;

import com.rab.framework.comm.dto.event.BaseResponseEvent;

/**
 * 
 * <P>Title: SecurityManagerResponseEvent</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-9</P>
 *
 */
public class SecurityManagerResponseEvent extends BaseResponseEvent {

	/**
	 * 序列化标识
	 */
	private static final long serialVersionUID = -3510179573327525353L;

	private Map<String,FuncRightPrincipal> funcPrincipals =  new HashMap<String,FuncRightPrincipal>();

	public Map<String, FuncRightPrincipal> getFuncPrincipals() {
		return funcPrincipals;
	}

	public void setFuncPrincipals(
			Map<String, FuncRightPrincipal> funcPrincipals) {
		this.funcPrincipals = funcPrincipals;
	}
	
}
