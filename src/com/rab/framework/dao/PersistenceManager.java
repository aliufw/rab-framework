package com.rab.framework.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.domain.domainconfig.PersistenceCommonConfig;


public class PersistenceManager {
	/**
	 * 日志记录对象
	 */
	private final static LogWritter logger = LogFactory.getLogger(PersistenceManager.class);

	/**
	 * 持久化对象名称标记
	 */
	private String SessionFactoryName;
	
	/**
	 * session factory 配置信息对象
	 */
	private AnnotationConfiguration configuration ;
	
	/**
	 * 和configuration对应的SessionFactory对象实例
	 */
	private SessionFactory sessionFactory ;

	/**
	 * 持久层通用配置信息
	 */
	private Properties commonConfig = null;
	
	/**
	 * 已经加载的 当前 Configuration 下定义的 O-R mapping 信息列表
	 */
	private List<String> loadedORmappings = new ArrayList<String>();
	
	/**
	 * 构造器
	 * 
	 * @param persistenceCommonConfig 持久层通用配置信息
	 */
	public PersistenceManager(PersistenceCommonConfig persistenceCommonConfig){
		this.SessionFactoryName = persistenceCommonConfig.getSessionFactoryName();
		this.commonConfig = persistenceCommonConfig.getSessionFactoryconfig();
		
		this.configuration = new AnnotationConfiguration();
		configuration.addProperties(commonConfig);
		
	}
	
	/**
	 * 向持久化管理对象注册Hibernate映射列表,基于注解模式的映射列表，使用类名称
	 * 
	 * @param mappingClasses Hbernate映射类列表
	 */
	public void registerMappingByClass(List<String> mappingClasses) throws BaseCheckedException{
    	Iterator<String> iterMapping = mappingClasses.iterator();
    	while(iterMapping.hasNext()){
    		String className = iterMapping.next();
    		
    		boolean flag = true;
    		for(String ormapping : loadedORmappings){
    			if(className.equals(ormapping)){
    				logger.info("持久层映射文件重复定义！or-mapping class=" + className);
    				flag = false;
    				break;
    			}
    		}
    		
    		if(flag){
    			
    			Class<?> claz = this.getClass(className);
    			
    			this.loadedORmappings.add(className);
    			this.configuration.addAnnotatedClass(claz);
    			logger.info("加载持久层映射信息: class = " + className);
    		}
    	}
	}
	
	/**
	 * 向持久化管理对象注册Hibernate映射列表,基于映射文件模式
	 * 
	 * @param mappingResources Hbernate映射文件列表
	 */
	public void registerMappingByResource(List<String> mappingResources) throws BaseCheckedException{
    	Iterator<String> iterMapping = mappingResources.iterator();
    	while(iterMapping.hasNext()){
    		String resourceName = iterMapping.next();
    		
    		boolean flag = true;
    		for(String ormapping : loadedORmappings){
    			if(resourceName.equals(ormapping)){
    				logger.info("持久层映射文件重复定义！or-mapping resource=" + resourceName);
    				flag = false;
    				break;
    			}
    		}
    		
    		if(flag){
    			this.loadedORmappings.add(resourceName);
    			this.configuration.addResource(resourceName);
    			logger.info("加载持久层映射信息: resource = " + resourceName);
    		}
    	}
	}
	
	
	
	/**
	 * 初始化
	 * 
	 * @param configuration 配置参数实例!
	 * @throws CSSBaseCheckedException
	 */
	public void initSessionFactory() throws BaseCheckedException {
		try {
			
			if(this.sessionFactory != null){
				synchronized(this.sessionFactory){
					this.sessionFactory = this.configuration.buildSessionFactory();
				}
			}
			else{
				this.sessionFactory = this.configuration.buildSessionFactory();
			}
		} catch (HibernateException e) {
			logger.error("00000400: 持久层初始化失败创建SessionFactory对象实例时发生异常",e);
			throw new BaseCheckedException("00000400",e);
		}
	}

	/**
	 * 检查当前制定的映射文件是否已经加载过。
	 * @param resource   映射文件名称路径
	 * @return   加载过－true， 没有加载过－false
	 */
	private boolean checkLoadedORmapping(String resource){
		boolean flag = false;
		for(String mapping : this.loadedORmappings){
			if(resource.equals(mapping)){
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	/**
	 * 加载指定的hibernate映射文件,注解模式，参数为BO类
	 * 
	 * @param className  BO类
	 * @throws CSSBaseCheckedException
	 */
	public boolean registerORmappingByClass(String className)  throws BaseCheckedException {
    	//2. 检查待加载的映射文件是否已经加载
		if(checkLoadedORmapping(className)){ //已经加载，返回！
			logger.info("[" + className + "] 已经加载!");
			return false;
		}
		
		Class<?> claz = this.getClass(className);
		
		//3. 加载指定的映射文件
		this.configuration.addAnnotatedClass(claz);
		
		this.initSessionFactory();
		
		logger.info("成功加载: className = " + className);
		
		//4. 作加载痕迹保留
		this.loadedORmappings.add(className);
			
		return true;
	}
	
	/**
	 * 加载指定的hibernate映射文件,注解模式，参数为BO类
	 * 
	 * @param className  BO类
	 * @throws CSSBaseCheckedException
	 */
	public boolean registerORmappingByResource(String resourceName)  throws BaseCheckedException {
    	//2. 检查待加载的映射文件是否已经加载
		if(checkLoadedORmapping(resourceName)){ //已经加载，返回！
			logger.info("[" + resourceName + "] 已经加载!");
			return false;
		}
		
		//3. 加载指定的映射文件
		this.configuration.addResource(resourceName);
		
		this.initSessionFactory();
		
		logger.info("成功加载: resource = " + resourceName);
		
		//4. 作加载痕迹保留
		this.loadedORmappings.add(resourceName);
			
		return true;
	}
	
	
	
	private Class<?> getClass(String className) throws BaseCheckedException {
		try {
			Class<?> claz = Class.forName(className);
			return claz;
		} 
		catch (Exception e) {
			logger.error("00000434: 加载持久层映射文件" + className + "时出现异常", e);
			List<String> params = new ArrayList<String>();
			params.add(className);
			throw new BaseCheckedException("00000434", params, e);
		} 
	}
	
	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public String getSessionFactoryName() {
		return SessionFactoryName;
	}

	public List<String> getLoadedORmappings() {
		return loadedORmappings;
	}

}
