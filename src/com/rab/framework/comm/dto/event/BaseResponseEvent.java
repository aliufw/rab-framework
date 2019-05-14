package com.rab.framework.comm.dto.event;

import java.io.Serializable;

import com.rab.framework.comm.exception.ExceptionInfo;

/**
 * 
 * <P>Title: BaseResponseEvent</P>
 * <P>Description: </P>
 * <P>程序说明：DTO的请求返回对象</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-30</P>
 *
 */
public class BaseResponseEvent implements Serializable {

	/**
	 * 序列化编码
	 */
	private static final long serialVersionUID = -7337360464371355862L;
	
	/**
	 * 交易成功标记，成功为true，反之为false
	 */
	private boolean success = true;

	/**
	 * 用于当success=false时，存放异常描述信息
	 */
	private ExceptionInfo exceptionInfo;
	
	/**
	 * 在数据结果返回时，由框架决定是否刷新字典表缓存数据。用在字典表数据更新时
	 */
	private boolean flushCachedDict = false;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public ExceptionInfo getExceptionInfo() {
		return exceptionInfo;
	}

	public void setExceptionInfo(ExceptionInfo exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}

	public boolean isFlushCachedDict() {
		return flushCachedDict;
	}

	public void setFlushCachedDict(boolean flushCachedDict) {
		this.flushCachedDict = flushCachedDict;
	}
}
