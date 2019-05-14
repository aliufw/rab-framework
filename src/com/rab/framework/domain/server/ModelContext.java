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
 * <P>程序说明：模块配置信息</P>
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
	 * 待加载的业务模块配置,key：modelName, value:ModelConfig
	 */
	private Map<String,ModelConfig> modelConfigs = new HashMap<String,ModelConfig>();

	/**
	 * 持久层通用配置信息
	 */
	private Map<String,PersistenceCommonConfig> persistenceCommonConfig = new HashMap<String,PersistenceCommonConfig>();

	/**
	 * 异常描述信息集合
	 */
	private Properties exceptionInfos = new Properties();
	
	/**
	 * 配置文件中定义的sql语句
	 */
	private Properties sqlConfigs = new Properties();
	
	/**
	 * 单例模式实例
	 */
	private static ModelContext modelContext = new ModelContext();
	
	/**
	 * 单例模式，返回ServerContext实例引用
	 * 
	 * @return ServerContext引用
	 */
	public static ModelContext singleton(){
		return modelContext;
	}

	public Map<String, ModelConfig> getModelConfigs() {
		return modelConfigs;
	}

	public void setModelConfigs(Map<String, ModelConfig> modelConfigs) {
		this.modelConfigs = modelConfigs;
		
		//将异常配置信息统一存放到上下文中
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
