package com.rab.framework.web.action.vo.data;

import java.util.Map;

import com.rab.framework.comm.dto.vo.BaseValueObject;

public class ResTDSVO extends BaseValueObject{
	private static final long serialVersionUID = 8315915515310303543L;
	private String status;
	private Map<String, Map<String, Object>> tds;

	public Map<String, Map<String, Object>> getTds() {
		return tds;
	}

	public void setTds(Map<String, Map<String, Object>> tds) {
		this.tds = tds;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
