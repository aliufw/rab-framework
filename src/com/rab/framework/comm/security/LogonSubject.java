package com.rab.framework.comm.security;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * <P>Title: LogonSubject</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>�û���½ϵͳ���Ȩ�ޱ������������</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class LogonSubject implements Serializable {
    /**
	 * ���л����
	 */
	private static final long serialVersionUID = -781329701471835168L;

	/**
     * �û�Id
     */
    private String usercode;

    /**
     * ������Դ�б�
     * ��λ 
     *    - ���� 
     *          -ģ��
     *               -����ƾ֤�б�
     * Map<String,
     *     Map<String,
     *         Map<String,
     *             Map<String,DefaultFuncRightPrincipal>>>>
     */
    private Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> funcPrincipals;

    
    public LogonSubject(String usercode){
    	this.usercode  = usercode;
    }

	public Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> getFuncPrincipals() {
		return funcPrincipals;
	}

	public void setFuncPrincipals(Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> funcPrincipals) {
		this.funcPrincipals = funcPrincipals;
	}

    public String getUsercode() {
        return usercode;
    }


    
}
