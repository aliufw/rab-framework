package com.rab.framework.comm.dto.event;

import java.io.Serializable;

/**
 * 
 * <P>Title: BaseRequestEvent</P>
 * <P>Description: </P>
 * <P>程序说明：DTO的请求对象</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-30</P>
 *
 */
public class BaseRequestEvent implements Serializable {

	/**
	 * 序列化编码
	 */
	private static final long serialVersionUID = -1101754235032363173L;

	/**
	 * 交易ID号，来源于服务器端业务服务逻辑编号 
	 */
	protected String transactionID;
	
	/**
	 * 用户登录状态，来源于登录后系统返回的唯一标识字符串
	 */
	protected String sessionID;
	
	/**
	 * 待执行的服务器端方法标识
	 */
	private String method;

	/**
	 * 交易标识，用于做前后台交易跟踪，主要用于性能日志及分析，不用于业务控制
	 */
	private String txId;
	
	/**
	 * 构造器
	 * 
	 * @param transactionID 必须参数，标识来源于服务器端业务服务逻辑编号 
	 * @param sessionID 用户登录状态，来源于登录后系统返回的唯一标识字符串
	 */
	public BaseRequestEvent(String transactionID, String sessionID) {
		this.transactionID = transactionID;
		this.sessionID = sessionID;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}
	
}
