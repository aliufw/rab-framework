package com.rab.framework.component.dictcache.manager;

import java.util.List;
import java.util.Properties;

public class DictMetaInfo {
	/**
	 * ����
	 */
	String name;
	
	/**
	 * ������
	 */
	String description;
	
	/**
	 * �ֶ������б�
	 */
	List<Properties> fields;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Properties> getFields() {
		return fields;
	}
	public void setFields(List<Properties> fields) {
		this.fields = fields;
	}
	
	
}
