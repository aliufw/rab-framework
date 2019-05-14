package com.rab.framework.web.action.vo;

/**
 * 
 * <P>Title: ResExceptionVO</P>
 * <P>Description: </P>
 * <P>程序说明：返回给页面组件层的异常封装对象 </P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-20</P>
 *
 */
public class ResExceptionVO extends ResComponentVO{

	private static final long serialVersionUID = 8192511135446855984L;
	private final String exception = "true";
	private String exceptionMsg;
	private String exceptionName;

	public ResExceptionVO() {
		super.setState(1);//fail state
	}

	/**
	 * @return the exceptionMsg
	 */
	public String getExceptionMsg() {
		return exceptionMsg;
	}

	/**
	 * @param exceptionMsg
	 *            the exceptionMsg to set
	 */
	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	/**
	 * @return the exception
	 */
	public String getException() {
		return exception;
	}

	/**
	 * @return the exceptionName
	 */
	public String getExceptionName() {
		return exceptionName;
	}

	/**
	 * @param exceptionName the exceptionName to set
	 */
	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}		
}
