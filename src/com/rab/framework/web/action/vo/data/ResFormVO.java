package com.rab.framework.web.action.vo.data;

import java.util.Map;

public class ResFormVO extends DataVO {
	private static final long serialVersionUID = -530168382292427082L;
	private Map<String, Map<String, Object>> data;

	public Map<String, Map<String, Object>> getData() {
		return data;
	}

	public void setData(Map<String, Map<String, Object>> data) {
		this.data = data;
	}

}
