package com.rab.framework.domain.domainconfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <P>Title: PersistenceDomainConfig</P>
 * <P>Description: </P>
 * <P>����˵�����ƶ�ģ��ĳ־ò�������Ϣ</P>
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
	 * ����Դ����
	 */
	private String sessionfactoryName;

	/**
	 * ����ע��ģʽ��ӳ���ϵ�б�
	 */
	List<String> mappingClasses = new ArrayList<String>();

	/**
	 * �����ļ�ģʽ��ӳ���ϵ�б�
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
