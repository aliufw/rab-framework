package com.rab.framework.comm.exception;

import java.io.Serializable;

/**
 * 
 * <P>Title: ExceptionInfo</P>
 * <P>Description: </P>
 * <P>程序说明：异常信息描述对象</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class ExceptionInfo implements Serializable {

	/**
	 * 序列化标记
	 */
	private static final long serialVersionUID = 2889559571399519634L;

	/**
	 * 异常编码
	 */
	private String exceptionCode;
	
	/**
	 * 异常描述信息
	 */
	private String exceptionMsg;
	
	/**
	 * 异常跟踪堆栈列表
	 */
	private StackTraceElement[] stacktraces;

	/**
	 * 原因异常描述信息
	 */
	private String causeExceptionMsg;
	
	/**
	 * 原因异常跟踪对战列表
	 */
	private StackTraceElement[] causeStacktraces;
	
	/**
	 * 本次交易的跟踪标识
	 */
	private String txid;
	
//	private ExceptionInfo(){}
	
	public String getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	public StackTraceElement[] getStacktraces() {
		return stacktraces;
	}

	public void setStacktraces(StackTraceElement[] stacktraces) {
		this.stacktraces = stacktraces;
	}
	
	public StackTraceElement[] getCauseStacktraces() {
		return causeStacktraces;
	}

	public void setCauseStacktraces(StackTraceElement[] causeStacktraces) {
		this.causeStacktraces = causeStacktraces;
	}

	public String getCauseExceptionMsg() {
		return causeExceptionMsg;
	}

	public void setCauseExceptionMsg(String causeExceptionMsg) {
		this.causeExceptionMsg = causeExceptionMsg;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public String toString(){
		String retStr = "";
		
		retStr += "exceptionCode: " + this.exceptionCode + "\r\n";
		retStr += "exceptionMsg: " + this.exceptionMsg + "\r\n";
		retStr += "txid: " + this.txid + "\r\n";
		retStr += "exceptionStackTrace: " + "\r\n";
		for(StackTraceElement elem : stacktraces){
			retStr += "\t " + elem + "\r\n";
		}
		
		if (this.causeStacktraces != null) {
			retStr += "causeExceptionMsg: " + this.causeExceptionMsg + "\r\n";
			retStr += "causeExceptionStackTrace: " + "\r\n";

			for (StackTraceElement elem : this.causeStacktraces) {
				retStr += "\t " + elem + "\r\n";
			}
		}

		return retStr;
	}
	
}
