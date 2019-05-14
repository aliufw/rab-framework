package com.rab.framework.domain.domainconfig;

import java.util.List;
import java.util.Properties;
/**
 * 
 * <P>Title: DomainConfig</P>
 * <P>Description: </P>
 * <P>����˵����ҵ��ģ���������Ϣ</P>
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
	 * ģ������
	 */
	private String modelName;
	
	/**
	 * ģ����������
	 */
	private String description;
	
	/**
	 * �洢 domain-*.xml�ļ�������Ϣ
	 */
	private DomainConfig domainConfig = null;
	
	/**
	 * �洢 persistence-*.xml�ļ�������Ϣ
	 */
	private List<PersistenceDomainConfig> persistenceConfigs = null;
	
	/**
	 * �洢exception-*.xml�ļ�
	 */
	private Properties exceptionConfigs = null;
	
	/**
	 * �洢sql-*.xml�ļ�
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

