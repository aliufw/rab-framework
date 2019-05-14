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
	 * ��־��¼����
	 */
	private final static LogWritter logger = LogFactory.getLogger(PersistenceManager.class);

	/**
	 * �־û��������Ʊ��
	 */
	private String SessionFactoryName;
	
	/**
	 * session factory ������Ϣ����
	 */
	private AnnotationConfiguration configuration ;
	
	/**
	 * ��configuration��Ӧ��SessionFactory����ʵ��
	 */
	private SessionFactory sessionFactory ;

	/**
	 * �־ò�ͨ��������Ϣ
	 */
	private Properties commonConfig = null;
	
	/**
	 * �Ѿ����ص� ��ǰ Configuration �¶���� O-R mapping ��Ϣ�б�
	 */
	private List<String> loadedORmappings = new ArrayList<String>();
	
	/**
	 * ������
	 * 
	 * @param persistenceCommonConfig �־ò�ͨ��������Ϣ
	 */
	public PersistenceManager(PersistenceCommonConfig persistenceCommonConfig){
		this.SessionFactoryName = persistenceCommonConfig.getSessionFactoryName();
		this.commonConfig = persistenceCommonConfig.getSessionFactoryconfig();
		
		this.configuration = new AnnotationConfiguration();
		configuration.addProperties(commonConfig);
		
	}
	
	/**
	 * ��־û��������ע��Hibernateӳ���б�,����ע��ģʽ��ӳ���б�ʹ��������
	 * 
	 * @param mappingClasses Hbernateӳ�����б�
	 */
	public void registerMappingByClass(List<String> mappingClasses) throws BaseCheckedException{
    	Iterator<String> iterMapping = mappingClasses.iterator();
    	while(iterMapping.hasNext()){
    		String className = iterMapping.next();
    		
    		boolean flag = true;
    		for(String ormapping : loadedORmappings){
    			if(className.equals(ormapping)){
    				logger.info("�־ò�ӳ���ļ��ظ����壡or-mapping class=" + className);
    				flag = false;
    				break;
    			}
    		}
    		
    		if(flag){
    			
    			Class<?> claz = this.getClass(className);
    			
    			this.loadedORmappings.add(className);
    			this.configuration.addAnnotatedClass(claz);
    			logger.info("���س־ò�ӳ����Ϣ: class = " + className);
    		}
    	}
	}
	
	/**
	 * ��־û��������ע��Hibernateӳ���б�,����ӳ���ļ�ģʽ
	 * 
	 * @param mappingResources Hbernateӳ���ļ��б�
	 */
	public void registerMappingByResource(List<String> mappingResources) throws BaseCheckedException{
    	Iterator<String> iterMapping = mappingResources.iterator();
    	while(iterMapping.hasNext()){
    		String resourceName = iterMapping.next();
    		
    		boolean flag = true;
    		for(String ormapping : loadedORmappings){
    			if(resourceName.equals(ormapping)){
    				logger.info("�־ò�ӳ���ļ��ظ����壡or-mapping resource=" + resourceName);
    				flag = false;
    				break;
    			}
    		}
    		
    		if(flag){
    			this.loadedORmappings.add(resourceName);
    			this.configuration.addResource(resourceName);
    			logger.info("���س־ò�ӳ����Ϣ: resource = " + resourceName);
    		}
    	}
	}
	
	
	
	/**
	 * ��ʼ��
	 * 
	 * @param configuration ���ò���ʵ��!
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
			logger.error("00000400: �־ò��ʼ��ʧ�ܴ���SessionFactory����ʵ��ʱ�����쳣",e);
			throw new BaseCheckedException("00000400",e);
		}
	}

	/**
	 * ��鵱ǰ�ƶ���ӳ���ļ��Ƿ��Ѿ����ع���
	 * @param resource   ӳ���ļ�����·��
	 * @return   ���ع���true�� û�м��ع���false
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
	 * ����ָ����hibernateӳ���ļ�,ע��ģʽ������ΪBO��
	 * 
	 * @param className  BO��
	 * @throws CSSBaseCheckedException
	 */
	public boolean registerORmappingByClass(String className)  throws BaseCheckedException {
    	//2. �������ص�ӳ���ļ��Ƿ��Ѿ�����
		if(checkLoadedORmapping(className)){ //�Ѿ����أ����أ�
			logger.info("[" + className + "] �Ѿ�����!");
			return false;
		}
		
		Class<?> claz = this.getClass(className);
		
		//3. ����ָ����ӳ���ļ�
		this.configuration.addAnnotatedClass(claz);
		
		this.initSessionFactory();
		
		logger.info("�ɹ�����: className = " + className);
		
		//4. �����غۼ�����
		this.loadedORmappings.add(className);
			
		return true;
	}
	
	/**
	 * ����ָ����hibernateӳ���ļ�,ע��ģʽ������ΪBO��
	 * 
	 * @param className  BO��
	 * @throws CSSBaseCheckedException
	 */
	public boolean registerORmappingByResource(String resourceName)  throws BaseCheckedException {
    	//2. �������ص�ӳ���ļ��Ƿ��Ѿ�����
		if(checkLoadedORmapping(resourceName)){ //�Ѿ����أ����أ�
			logger.info("[" + resourceName + "] �Ѿ�����!");
			return false;
		}
		
		//3. ����ָ����ӳ���ļ�
		this.configuration.addResource(resourceName);
		
		this.initSessionFactory();
		
		logger.info("�ɹ�����: resource = " + resourceName);
		
		//4. �����غۼ�����
		this.loadedORmappings.add(resourceName);
			
		return true;
	}
	
	
	
	private Class<?> getClass(String className) throws BaseCheckedException {
		try {
			Class<?> claz = Class.forName(className);
			return claz;
		} 
		catch (Exception e) {
			logger.error("00000434: ���س־ò�ӳ���ļ�" + className + "ʱ�����쳣", e);
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
