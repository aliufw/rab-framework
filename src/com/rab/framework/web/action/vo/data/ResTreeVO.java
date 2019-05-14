package com.rab.framework.web.action.vo.data;

import java.util.Map;

public class ResTreeVO extends DataVO {

	private static final long serialVersionUID = 583963994504474122L;

	private Map[] data;

	/**
	 * @return the data
	 */
	public Map[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map[] data) {
		this.data = data;
	}

}
