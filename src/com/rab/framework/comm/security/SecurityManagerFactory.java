package com.rab.framework.comm.security;

import com.rab.framework.comm.appcontext.ApplicationContext;

/**
 * 
 * <P>Title: SecurityManagerFactory</P>
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
public class SecurityManagerFactory {
	
	private static SecurityManager securityManager = null;
	
	public static SecurityManager getSecurityManager(){
		if(securityManager == null){
			securityManager = new SecurityManagerImpl();
		}
		return securityManager;
	}
	
	public static void main(String[] argvs){
		//����ܿ����Ƿ�򿪣����Ϊtrue����������ȫ���˼���߼�����֮��������
		boolean flag = ApplicationContext.singleton().checkRuntimeSecurityManager();
		if(flag == true){
			SecurityManagerFactory.getSecurityManager();
		}

	}
}
