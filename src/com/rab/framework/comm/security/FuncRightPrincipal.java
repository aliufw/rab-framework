package com.rab.framework.comm.security;

import java.io.Serializable;

/**
 * 
 * <P>Title: DefaultFuncRightPrincipal</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>ȱʡ�Ĺ���Ȩ��ƾ֤</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class FuncRightPrincipal implements Serializable {
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 1137144863117517656L;
	/**
	 * ������Դ����
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
