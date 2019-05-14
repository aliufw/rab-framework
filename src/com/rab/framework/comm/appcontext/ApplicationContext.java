package com.rab.framework.comm.appcontext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.threadlocal.ThreadLocalManager;
import com.rab.framework.comm.util.FileUtils;
import com.rab.framework.domain.domainconfig.DomainConfigLoader;
import com.rab.framework.domain.domainconfig.ModelConfig;
import com.rab.framework.domain.domainconfig.PersistenceCommonConfig;
import com.rab.framework.domain.server.ModelContext;

/**
 * 
 * <P>Title: ApplicationContext</P>
 * <P>Description: </P>
 * <P>����˵����ƽ̨�Ļ������������Ļ���</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-30</P>
 *
 */
public class ApplicationContext {
	/**
	 * ��־��¼��
	 */
	private final static LogWritter logger = LogFactory.getLogger(ApplicationContext.class);

	/**
	 * ��������
	 */
	private static ApplicationContext applicationContext = null;
	
	/**
	 * ��������
	 */
	private Map<String,Object> contextPool = null;

	/**
	 * �쳣������Ϣ����
	 */
	private Properties exceptionInfos = new Properties();

	/**
	 * �洢sql-*.xml�е�ӳ����Ϣ
	 */
	private Properties sqlConfigs = new Properties();

	/**
	 * ˽�й�����
	 */
	private ApplicationContext(){
		init();
	}
	
    private void init() {
        try {
        	//1. �����������
            BootstrapLoader bl = new BootstrapLoader();
            contextPool = bl.load();
            
			//2. ��web�ͻ�������ʱ����Ҫ�����쳣��Ϣ�����ļ�webexception.propterties
			String filename = "webexception.propterties";
			Properties exceptionProps = this.loadExtExceptionConfig(filename);
			this.exceptionInfos.putAll(exceptionProps);

            //3. ����ҵ���������Ϣ
            String isWebappServer = "" + ThreadLocalManager.getThreadLocalMap().get("isWebappServer");
            String ejbModel = "" + contextPool.get("ejb-model");

            logger.debug("ϵͳ��ʼ����isWebappServer = " + isWebappServer);
            logger.debug("ϵͳ��ʼ����ejbModel = " + ejbModel);
            
            //ejb-model=true && isWebappServer=true,������ҵ��������ļ�
            if(isWebappServer.equalsIgnoreCase("true") 
            		&& ejbModel.equalsIgnoreCase("true")){
            		return;
            }
            
    		DomainConfigLoader dcLoader = new DomainConfigLoader();
    		if(contextPool.get("db-type") != null){
    			dcLoader.setDbType("" + contextPool.get("db-type"));
    		}
    		//3.1 ���س־ò�ͨ������
    		Map<String,PersistenceCommonConfig> persistenceCommonConfig = dcLoader.loadPersistenceCommonConfig();
    		ModelContext.singleton().setPersistenceCommonConfig(persistenceCommonConfig);
    		
        	//3.2 ����ҵ��ģ�����ã�����domain��persistence��exception��sql�Ĵ���
    		Map<String,ModelConfig>  modelConfigs = dcLoader.loadModel();
    		ModelContext.singleton().setModelConfigs(modelConfigs);
			this.exceptionInfos = ModelContext.singleton().getExceptionInfos();
			this.sqlConfigs = ModelContext.singleton().getSqlConfigs();
			
			
		} catch (AppContextInitException e) {
			logger.error("��ʼ������ʱ�����쳣��",e);
			System.exit(-1);
		}
    }
    
    private Properties loadExtExceptionConfig(String filename) throws AppContextInitException{
		Properties props = new Properties();
		InputStream is = null;
		try {
			FileUtils fileUtils = new FileUtils(filename);
			is = fileUtils.getInputStream();
			if(is == null){
				logger.info("====================================================================");
				logger.info("û�з�����Ҫ���صĿͻ����쳣�����ļ���ϵͳ�������ù��̼������У�");
				logger.info("====================================================================");

				return props;
			}
			props.load(is);
			logger.info("���ؿͻ����쳣�����ļ�" + filename + " �ɹ�!");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppContextInitException("00000105: ���ص���չ�쳣�����ļ�" + filename + " ʱ�����쳣!", e);
		}
		finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					;
				}
			}
		}
		return props;
	}

    /**
     * ����ģʽ�����ض�ApplicationContext�ķ�������
     * 
     * @return ApplicationContext����ʵ������
     */
    public static ApplicationContext singleton() {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
         return applicationContext;
    }
    
	public Object getValueByKey(String key){
		return contextPool.get(key);
	}

	public String getExceptionMsg(String code){
		return exceptionInfos.getProperty(code);
	}

	public String getSqlByKey(String key){
		return sqlConfigs.getProperty(key);
	}

	public boolean checkRuntimeSecurityManager(){
		String conf = (String) contextPool.get("runtime-security-manager");

		if(conf != null && conf.trim().equalsIgnoreCase("false")){
			return false;
		}
		else{
			return true;
		}
	}
}
