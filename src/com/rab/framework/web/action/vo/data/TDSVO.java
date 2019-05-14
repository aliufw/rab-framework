package com.rab.framework.web.action.vo.data;

import java.util.Map;

import com.rab.framework.comm.dto.vo.BaseValueObject;

public class TDSVO extends BaseValueObject{
	private static final long serialVersionUID = -9006096172516422544L;
	private String status;
	private String opt;
	private Map<String, Map<String, Object>> tds;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public Map<String, Map<String, Object>> getTds() {
		return tds;
	}

	public void setTds(Map<String, Map<String, Object>> tds) {
		this.tds = tds;
	}

}
