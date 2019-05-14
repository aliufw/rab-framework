package com.rab.framework.domain.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.rab.framework.domain.domainconfig.ModelConfig;
import com.rab.framework.domain.domainconfig.PersistenceCommonConfig;

/**
 * 
 * <P>Title: ModelContext</P>
 * <P>Description: </P>
 * <P>����˵����ģ��������Ϣ</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class ModelContext {
	/**
	 * �����ص�ҵ��ģ������,key��modelName, value:ModelConfig
	 */
	private Map<String,ModelConfig> modelConfigs = new HashMap<String,ModelConfig>();

	/**
	 * �־ò�ͨ��������Ϣ
	 */
	private Map<String,PersistenceCommonConfig> persistenceCommonConfig = new HashMap<String,PersistenceCommonConfig>();

	/**
	 * �쳣������Ϣ����
	 */
	private Properties exceptionInfos = new Properties();
	
	/**
	 * �����ļ��ж����sql���
	 */
	private Properties sqlConfigs = new Properties();
	
	/**
	 * ����ģʽʵ��
	 */
	private static ModelContext modelContext = new ModelContext();
	
	/**
	 * ����ģʽ������ServerContextʵ������
	 * 
	 * @return ServerContext����
	 */
	public static ModelContext singleton(){
		return modelContext;
	}

	public Map<String, ModelConfig> getModelConfigs() {
		return modelConfigs;
	}

	public void setModelConfigs(Map<String, ModelConfig> modelConfigs) {
		this.modelConfigs = modelConfigs;
		
		//���쳣������Ϣͳһ��ŵ���������
		Iterator<ModelConfig> iter = modelConfigs.values().iterator();
		while(iter.hasNext()){
			ModelConfig mc = iter.next();
			Properties props = mc.getExceptionConfigs();
			exceptionInfos.putAll(props);
			Properties props2 = mc.getSqlConfigs();
			sqlConfigs.putAll(props2);
		}
	}

	public Map<String, PersistenceCommonConfig> getPersistenceCommonConfig() {
		return persistenceCommonConfig;
	}

	public void setPersistenceCommonConfig(
			Map<String, PersistenceCommonConfig> persistenceCommonConfig) {
		this.persistenceCommonConfig = persistenceCommonConfig;
	}
	
	public Properties getExceptionInfos() {
		return exceptionInfos;
	}

	public Properties getSqlConfigs() {
		return sqlConfigs;
	}

//	public String getExceptionMsg(String code){
//		return exceptionInfos.getProperty(code);
//	}

}
