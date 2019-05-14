package com.rab.framework.domain.domainconfig;

import java.util.List;
import java.util.Properties;
/**
 * 
 * <P>Title: DomainConfig</P>
 * <P>Description: </P>
 * <P>程序说明：业务模块的配置信息</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class ModelConfig {
	
	/**
	 * 模块名称
	 */
	private String modelName;
	
	/**
	 * 模块描述文字
	 */
	private String description;
	
	/**
	 * 存储 domain-*.xml文件配置信息
	 */
	private DomainConfig domainConfig = null;
	
	/**
	 * 存储 persistence-*.xml文件配置信息
	 */
	private List<PersistenceDomainConfig> persistenceConfigs = null;
	
	/**
	 * 存储exception-*.xml文件
	 */
	private Properties exceptionConfigs = null;
	
	/**
	 * 存储sql-*.xml文件
	 */
	private Properties sqlConfigs = new Properties();

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DomainConfig getDomainConfig() {
		return domainConfig;
	}

	public void setDomainConfig(DomainConfig domainConfig) {
		this.domainConfig = domainConfig;
	}

	public List<PersistenceDomainConfig> getPersistenceConfigs() {
		return persistenceConfigs;
	}

	public void setPersistenceConfigs(List<PersistenceDomainConfig> persistenceConfigs) {
		this.persistenceConfigs = persistenceConfigs;
	}

	public Properties getExceptionConfigs() {
		return exceptionConfigs;
	}

	public void setExceptionConfigs(Properties exceptionConfigs) {
		this.exceptionConfigs = exceptionConfigs;
	}

	public Properties getSqlConfigs() {
		return sqlConfigs;
	}

	public void setSqlConfigs(Properties sqlConfigs) {
		this.sqlConfigs = sqlConfigs;
	}


}

