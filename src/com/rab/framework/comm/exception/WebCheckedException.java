package com.rab.framework.comm.exception;

import java.util.List;

public class WebCheckedException extends BaseCheckedException {

	private static final long serialVersionUID = 7480469027465256470L;

	public WebCheckedException(String code, List<String> params, Throwable ex) {
		super(code, params, ex);
	}

	public WebCheckedException(String code, List<String> params) {
		super(code, params);
	}

	public WebCheckedException(String code, String param, Throwable ex) {
		super(code, param, ex);
	}

	public WebCheckedException(String code, String param) {
		super(code, param);
	}

	public WebCheckedException(String code, Throwable ex) {
		super(code, ex);
	}

	public WebCheckedException(String code) {
		super(code);
	}


}
