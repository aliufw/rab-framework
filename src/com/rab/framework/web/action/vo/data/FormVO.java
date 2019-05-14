package com.rab.framework.web.action.vo.data;

import java.util.Map;

public class FormVO extends DataVO {
	private static final long serialVersionUID = 173907533939308539L;
	private String beanName;
	private Map<String, Map<String, Object>> data;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Map<String, Map<String, Object>> getData() {
		return data;
	}

	public void setData(Map<String, Map<String, Object>> data) {
		this.data = data;
	}

}
