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
 * <P>程序说明：平台的环境参数上下文环境</P>
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
	 * 日志记录器
	 */
	private final static LogWritter logger = LogFactory.getLogger(ApplicationContext.class);

	/**
	 * 单例对象
	 */
	private static ApplicationContext applicationContext = null;
	
	/**
	 * 环境参数
	 */
	private Map<String,Object> contextPool = null;

	/**
	 * 异常描述信息集合
	 */
	private Properties exceptionInfos = new Properties();

	/**
	 * 存储sql-*.xml中的映射信息
	 */
	private Properties sqlConfigs = new Properties();

	/**
	 * 私有构造器
	 */
	private ApplicationContext(){
		init();
	}
	
    private void init() {
        try {
        	//1. 加载入口配置
            BootstrapLoader bl = new BootstrapLoader();
            contextPool = bl.load();
            
			//2. 以web客户端运行时，需要加载异常信息配置文件webexception.propterties
			String filename = "webexception.propterties";
			Properties exceptionProps = this.loadExtExceptionConfig(filename);
			this.exceptionInfos.putAll(exceptionProps);

            //3. 加载业务层配置信息
            String isWebappServer = "" + ThreadLocalManager.getThreadLocalMap().get("isWebappServer");
            String ejbModel = "" + contextPool.get("ejb-model");

            logger.debug("系统初始化：isWebappServer = " + isWebappServer);
            logger.debug("系统初始化：ejbModel = " + ejbModel);
            
            //ejb-model=true && isWebappServer=true,不加载业务层配置文件
            if(isWebappServer.equalsIgnoreCase("true") 
            		&& ejbModel.equalsIgnoreCase("true")){
            		return;
            }
            
    		DomainConfigLoader dcLoader = new DomainConfigLoader();
    		if(contextPool.get("db-type") != null){
    			dcLoader.setDbType("" + contextPool.get("db-type"));
    		}
    		//3.1 加载持久层通用配置
    		Map<String,PersistenceCommonConfig> persistenceCommonConfig = dcLoader.loadPersistenceCommonConfig();
    		ModelContext.singleton().setPersistenceCommonConfig(persistenceCommonConfig);
    		
        	//3.2 加载业务模块配置，包括domain、persistence、exception、sql四大项
    		Map<String,ModelConfig>  modelConfigs = dcLoader.loadModel();
    		ModelContext.singleton().setModelConfigs(modelConfigs);
			this.exceptionInfos = ModelContext.singleton().getExceptionInfos();
			this.sqlConfigs = ModelContext.singleton().getSqlConfigs();
			
			
		} catch (AppContextInitException e) {
			logger.error("初始化环境时发生异常！",e);
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
				logger.info("没有发现需要加载的客户端异常配置文件，系统将跳过该过程继续运行！");
				logger.info("====================================================================");

				return props;
			}
			props.load(is);
			logger.info("加载客户端异常配置文件" + filename + " 成功!");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppContextInitException("00000105: 加载的扩展异常配置文件" + filename + " 时出现异常!", e);
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
     * 单例模式，返回对ApplicationContext的访问引用
     * 
     * @return ApplicationContext对象实例引用
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
