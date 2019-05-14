package com.rab.framework.domain.domainconfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <P>Title: PersistenceDomainConfig</P>
 * <P>Description: </P>
 * <P>程序说明：制定模块的持久层配置信息</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class PersistenceDomainConfig {
	/**
	 * 数据源描述
	 */
	private String sessionfactoryName;

	/**
	 * 采用注解模式的映射关系列表
	 */
	List<String> mappingClasses = new ArrayList<String>();

	/**
	 * 采用文件模式的映射关系列表
	 */
	List<String> mappingResources = new ArrayList<String>();

	public String getSessionfactoryName() {
		return sessionfactoryName;
	}

	public void setSessionfactoryName(String sessionfactoryName) {
		this.sessionfactoryName = sessionfactoryName;
	}

	public List<String> getMappingClasses() {
		return mappingClasses;
	}

	public void setMappingClasses(List<String> mappingClasses) {
		this.mappingClasses = mappingClasses;
	}

	public List<String> getMappingResources() {
		return mappingResources;
	}

	public void setMappingResources(List<String> mappingResources) {
		this.mappingResources = mappingResources;
	}

}
