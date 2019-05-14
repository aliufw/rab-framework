package com.rab.framework.comm.security;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * <P>Title: LogonSubject</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>用户登陆系统后的权限保存管理容器类</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class LogonSubject implements Serializable {
    /**
	 * 序列化编号
	 */
	private static final long serialVersionUID = -781329701471835168L;

	/**
     * 用户Id
     */
    private String usercode;

    /**
     * 功能资源列表
     * 单位 
     *    - 帐套 
     *          -模块
     *               -功能凭证列表
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
