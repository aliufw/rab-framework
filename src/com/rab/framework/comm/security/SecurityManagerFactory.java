package com.rab.framework.comm.security;

import com.rab.framework.comm.appcontext.ApplicationContext;

/**
 * 
 * <P>Title: SecurityManagerFactory</P>
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
public class SecurityManagerFactory {
	
	private static SecurityManager securityManager = null;
	
	public static SecurityManager getSecurityManager(){
		if(securityManager == null){
			securityManager = new SecurityManagerImpl();
		}
		return securityManager;
	}
	
	public static void main(String[] argvs){
		//检查总开关是否打开，如果为true，则启动安全过滤检查逻辑，反之，不启动
		boolean flag = ApplicationContext.singleton().checkRuntimeSecurityManager();
		if(flag == true){
			SecurityManagerFactory.getSecurityManager();
		}

	}
}
