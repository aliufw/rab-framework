package com.rab.framework.comm.appcontext;

/**
 * 
 * <P>Title: AppContextInitException</P>
 * <P>Description: </P>
 * <P>����˵����ApplicationContext��ʼ�����̵��쳣������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-30</P>
 *
 */
public class AppContextInitException extends Exception {

	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 1211152505526580944L;

	public AppContextInitException() {
	}

	public AppContextInitException(String message) {
		super(message);
	}

	public AppContextInitException(Throwable cause) {
		super(cause);
	}

	public AppContextInitException(String message, Throwable cause) {
		super(message, cause);
	}

}
