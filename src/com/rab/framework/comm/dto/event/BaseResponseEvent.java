package com.rab.framework.comm.dto.event;

import java.io.Serializable;

import com.rab.framework.comm.exception.ExceptionInfo;

/**
 * 
 * <P>Title: BaseResponseEvent</P>
 * <P>Description: </P>
 * <P>����˵����DTO�����󷵻ض���</P>
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
	 * ���л�����
	 */
	private static final long serialVersionUID = -7337360464371355862L;
	
	/**
	 * ���׳ɹ���ǣ��ɹ�Ϊtrue����֮Ϊfalse
	 */
	private boolean success = true;

	/**
	 * ���ڵ�success=falseʱ������쳣������Ϣ
	 */
	private ExceptionInfo exceptionInfo;
	
	/**
	 * �����ݽ������ʱ���ɿ�ܾ����Ƿ�ˢ���ֵ�������ݡ������ֵ�����ݸ���ʱ
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
