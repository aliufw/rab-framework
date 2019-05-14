package com.rab.framework.comm.exception;

import java.util.List;

public class ReturnExceptionFactory {
	public static BaseCheckedException createReturnCheckedException(String code){
		return new BaseCheckedException(code);
	}

	public static BaseCheckedException createReturnCheckedException(String code, Throwable ex) {
		if(ex instanceof BaseCheckedException){
			return (BaseCheckedException)ex;
		}
		else{
			return new BaseCheckedException(code, ex);
		}
	}
	
	public static BaseCheckedException createReturnCheckedException(String code, List<String> params) {
		return new BaseCheckedException(code, params);
	}

	public static BaseCheckedException createReturnCheckedException(String code, String param, Throwable ex) {
		if(ex instanceof BaseCheckedException){
			return (BaseCheckedException)ex;
		}
		else{
			return new BaseCheckedException(code, param, ex);
		}
	}

	public static BaseCheckedException createReturnCheckedException(String code, String param) {
		return new BaseCheckedException(code, param);
	}

}
