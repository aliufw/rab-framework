package com.rab.framework.comm.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * 
 * <P>Title: VHBaseCheckedException</P>
 * <P>Description: </P>
 * <P>程序说明：系统的Checked异常基类</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class BaseCheckedException extends Exception{

	private static final long serialVersionUID = -6964284094568438323L;

	private Throwable rootCause;

	/**
	 * 聚合了helper类，用于管理返回码和参数
	 */
	private ExceptionHelper helper = new ExceptionHelper();

	private String exceptionMsg = "";

	public Throwable getRootCause() {
		return rootCause;
	}

	public BaseCheckedException(String code) {
		super(code);
		helper.setCode(code);
		this.exceptionMsg = helper.getContent();
	}

	public BaseCheckedException(String code, Throwable ex) {
		super(code, ex);
		rootCause = ex;
		helper.setCode(code);
		helper.parseExceptionStackInfo(ex);
		this.exceptionMsg = helper.getContent();
	}

	public BaseCheckedException(String code, List<String> params) {
		super(code);
		helper.setCode(code);
		helper.addParam(params);
		this.exceptionMsg = helper.getContent();
	}

	public BaseCheckedException(String code, List<String> params, Throwable ex) {
		super(code, ex);
		helper.setCode(code);
		helper.addParam(params);
		helper.parseExceptionStackInfo(ex);
		this.exceptionMsg = helper.getContent();
	}
	
	public BaseCheckedException(String code, String param, Throwable ex) {
		super(code, ex);
		helper.setCode(code);
		helper.addParam(param);
		helper.parseExceptionStackInfo(ex);
		this.exceptionMsg = helper.getContent();
	}

	public BaseCheckedException(String code, String param) {
		super(code);
		helper.setCode(code);
		helper.addParam(param);
		this.exceptionMsg = helper.getContent();
	}

	public List<?> getParam() {
		return helper.getParam();
	}

	public String getCode() {
		return helper.getCode();
	}

	public String getExceptionMsg() {
		return this.exceptionMsg;
	}

	public String getExceptionStackInfo() {
		return this.helper.getExceptionStackInfo();
	}
	
	/**
	 * Returns the detail message, including the message from the nested
	 * exception if there is one.
	 */
	public String getMessage() {
		if (rootCause == null) {
			return super.getMessage();
		} else {
			return super.getMessage() + "; nested exception is: \n\t"
					+ rootCause.toString();
		}
	}

	/**
	 * Prints the composite message and the embedded stack trace to the
	 * specified stream <code>ps</code>.
	 * 
	 * @param ps
	 *            the print stream
	 */
	public void printStackTrace(PrintStream ps) {
		if (rootCause == null) {
			super.printStackTrace(ps);
		} else {
			ps.println(this);
			rootCause.printStackTrace(ps);
		} 
	}

	/**
	 * Prints the composite message and the embedded stack trace to the
	 * specified print writer <code>pw</code>
	 * 
	 * @param pw
	 *            the print writer
	 */
	public void printStackTrace(PrintWriter pw) {
		if (rootCause == null) {
			super.printStackTrace(pw);
		} else {
			pw.println(this);
			rootCause.printStackTrace(pw);
		}
	}

	/**
	 * Prints the composite message to <code>System.err</code>.
	 */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

}