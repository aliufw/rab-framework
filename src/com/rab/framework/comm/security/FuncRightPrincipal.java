package com.rab.framework.comm.security;

import java.io.Serializable;

/**
 * 
 * <P>Title: DefaultFuncRightPrincipal</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>缺省的功能权限凭证</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class FuncRightPrincipal implements Serializable {
	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 1137144863117517656L;
	/**
	 * 功能资源描述
	 */
	private FuncRightResource funcRightRes = null;

	public void setRightResource(FuncRightResource funcRightRes) {
		this.funcRightRes = funcRightRes;
	}

	public String getPrincipalId() {
		return this.funcRightRes.getFuncId();
	}

	public String getPrincipalName() {
		return this.funcRightRes.getPermName();
	}

	public FuncRightResource getFuncRightRes() {
		return funcRightRes;
	}
}
