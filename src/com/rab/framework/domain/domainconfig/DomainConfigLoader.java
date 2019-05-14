package com.rab.framework.domain.domainconfig;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.rab.framework.comm.appcontext.AppContextInitException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.FileUtils;
import com.rab.framework.domain.server.CoreAppServer;

/**
 * 
 * <P>Title: DomainConfigLoader</P>
 * <P>Description: </P>
 * <P>程序说明：业务层配置文件加载器</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class DomainConfigLoader {
	/**
	 * 日志记录对象
	 */
	private final static LogWritter logger = LogFactory.getLogger(CoreAppServer.class);
	
	/**
	 * 持久层通用配置信息默认配置文件名称
	 */
	private String persistenceCommonConfigFile = "persistence.xml";
	
	/**
	 * 模块配置信息配置文件名称
	 */
	private String appcomponentfile = "appcomponent.xml";
	
	/**
	 * 数据库类型，用以指示并加载针对特定数据库的sql资源文件
	 */
	private String dbType = null;
	
	public Map<String,PersistenceCommonConfig> loadPersistenceCommonConfig() throws AppContextInitException {
    	Document doc = null;
    	Map<String,PersistenceCommonConfig> persistenceCommonConfig = new HashMap<String,PersistenceCommonConfig>();
    	
    	try {
            SAXBuilder builder = new SAXBuilder();
        	URL url = this.getClass().getClassLoader().getResource(persistenceCommonConfigFile);
            InputStream is = url.openStream();
            doc = builder.build(is);
            
            Element root = doc.getRootElement();
            List<Element> list = root.getChildren("session-factory");
            
            logger.info("=============>>开始加载持久层通用信息: " + persistenceCommonConfigFile);
            for (int i = 0; i < list.size(); i++) {
				Element session_factory = list.get(i);
				PersistenceCommonConfig pcc = new PersistenceCommonConfig();
        		//session-factory name 
				String sessionFactoryName = session_factory.getAttributeValue("name");
				pcc.setSessionFactoryName(sessionFactoryName);
				logger.info("加载持久层通用信息<session-factory>: name = " + sessionFactoryName);		
				//session-factory-config
				Element session_factory_config = session_factory.getChild("session-factory-config");
				List<Element> sessionProps = session_factory_config.getChildren("property");
				Properties props = new Properties();
				for (int k = 0; k < sessionProps.size(); k++) {
					Element config = (Element) sessionProps.get(k);
					String key = config.getAttributeValue("name");
					String value = config.getText();
					if (!key.startsWith("hibernate")) {
						key = "hibernate." + key;
					}
					logger.info("加载持久层通用信息<session-factory-config>: " + key + " = " + value);					
					props.setProperty(key, value);
				}
				pcc.setSessionFactoryconfig(props);

				persistenceCommonConfig.put(sessionFactoryName, pcc);
	            logger.info("=============<<加载持久层通用信息结束: " + persistenceCommonConfigFile);

			}
        } catch (Exception ex) {
        	throw new AppContextInitException("00000003:持久层通用配置信息解析加载时出现异常!",ex);
        }       
        return persistenceCommonConfig;
    }
		
	/**
	 * 加载业务模块配置信息
	 * 
	 * @return
	 */
	public Map<String,ModelConfig> loadModel() throws AppContextInitException {
		Map<String, ModelConfig> components = getAppcomponents(appcomponentfile);
		
		Iterator<String> iter = components.keySet().iterator();
		while(iter.hasNext()){
			String modelName = iter.next();
			ModelConfig mc = (ModelConfig)components.get(modelName);
						
			//解析配置文件domain-*.xml
			DomainConfig domainConfig = loadDomainConfig(modelName);
			mc.setDomainConfig(domainConfig);
			
			//解析配置文件persistence-*.xml
			List<PersistenceDomainConfig> persistenceConfigs = this.getPersistenceDomainConfig(modelName);
			mc.setPersistenceConfigs(persistenceConfigs);
			
			//解析异常模板配置文件 exception-*.properties
			Properties exceptionConfig = this.loadExceptionConfig(modelName);
			mc.setExceptionConfigs(exceptionConfig);
			
			//解析SQL语句置文件 sql-*.xml
			Properties sqlConfig = this.loadSqlConfig(modelName);
			mc.setSqlConfigs(sqlConfig);
		}
		
		return components;
	}
	
	private Map<String, ModelConfig> getAppcomponents(String appcomponentfile) throws AppContextInitException{
        FileUtils fileUtils = new FileUtils(appcomponentfile);
        InputStream is = fileUtils.getInputStream();
        Map<String, ModelConfig> map = new HashMap<String, ModelConfig>();

//      if(is == null) {
//    	throw new AppContextInitException("00000107: 平台模块配置文件["+appcomponentfile+"]读取错误！终止运行！");
//    }
		if(is == null) {
			logger.info("====================================================================");
			logger.info("没有发现平台模块配置文件["+appcomponentfile+"]，系统将以客户端模式运行！");
			logger.info("====================================================================");
			return map;
		}

        Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        try {
            doc = builder.build(is);      
            Element rootElement = doc.getRootElement();

            //<app-component>
            //运行时的组件信息
            List<Element> list = rootElement.getChildren("app-component");
            for(int i=0; i<list.size(); i++){
            	Element app_component = (Element)list.get(i);
            	ModelConfig mc = new ModelConfig();
            	//<name> 组件名称
            	String name = app_component.getChild("name").getText();
            	mc.setModelName(name);
            	
            	//<description> 组件描述信息
            	String description = "";
            	if(app_component.getChild("description") != null)
            		description = app_component.getChild("description").getText();           	
            	mc.setDescription(description);
            	
            	map.put(name, mc);
            }          
        } catch (Exception ex) {
            throw new AppContextInitException("0000100: 应用组件加载失败, 请检查配置文件appcomponent.xml的内容设置或格式是否正确!", ex);
        } 	
        return map;
	}
   	

	/**
	 * 原有的用于解析domain-*.xml文件方法
	 * @param ctxName
	 * @param loader
	 * @return
	 * @throws Exception
	 */
	private DomainConfig loadDomainConfig(String modelName) throws AppContextInitException {
		String configName = "domain-" + modelName + ".xml";
		DomainConfig dc = new DomainConfig();
		dc.setModelName(modelName);

		try {
			URL url = this.getClass().getClassLoader().getResource(configName);
			InputStream is = url.openStream();
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(is);

			Element root = doc.getRootElement();

			// <bizLogicHandler-config/>
			String defaultDSName = root.getChildText("default-dataSource-name");
			if (defaultDSName != null) {
				dc.setDefaultDataSourceName(defaultDSName);
			}

			// <bizLogicHandler/>
			List<Element> list = root.getChildren("bizLogicHandler");
			for (int i = 0; i < list.size(); i++) {
				Element elm = (Element) list.get(i);
				String name = elm.getChildText("bizLogicHandler-name");
				String clsname = elm.getChildText("bizLogicHandler-class");

				TransactionCfg tcfg = new TransactionCfg();
				tcfg.setName(name);
				tcfg.setClassName(clsname);

				List<Element> dataSources = elm.getChildren("dataSource-name");
				if (dataSources != null && dataSources.size() > 0) {
					for (int k = 0; k < dataSources.size(); k++) {
						Element dsElm = (Element) dataSources.get(k);
						String dsName = dsElm.getChildText("dataSources");
						tcfg.getDsNames().add(dsName);
					}
				}
				dc.getTransactions().put(name, tcfg);
				logger.info("加载domain配置信息：" + tcfg);
			}
			return dc;
			
		} catch (Exception e) {
			throw new AppContextInitException("00000102解析Domain层配置文件 " + configName + " 时出现异常! ", e);
		}
	}

	private List<PersistenceDomainConfig> getPersistenceDomainConfig(String modelName) throws AppContextInitException{
	    	List<PersistenceDomainConfig> persistenceConfigs = new ArrayList<PersistenceDomainConfig>();
			String configName = "persistence-" + modelName + ".xml";

	    	try {
	        	//解析模块自身的配置信息
	        	URL url = this.getClass().getClassLoader().getResource(configName);
	        	if(url == null){
	        		throw new AppContextInitException("00000106: CSSAppContext加载时,没有发现映射配置文件 " + configName);
	        	}
	        	InputStream is = url.openStream();

	        	logger.info(">>>>>>>>>>>>>>>>>>>>>>开始加载映射文件: " + configName );

		    	SAXBuilder builder = new SAXBuilder();
		    	Document doc = builder.build(is);
				Element root = doc.getRootElement();
		        List<Element> list = root.getChildren("or-mappings");
		        for(int i=0; i<list.size(); i++){
		        	Element or_mappings = (Element)list.get(i);
		        	PersistenceDomainConfig pdc = new PersistenceDomainConfig();
		        	
		        	String session_factory =  or_mappings.getAttributeValue("session-factory");
		        	pdc.setSessionfactoryName(session_factory);
		        	logger.info("加载映射文件 " + configName + " 配置信息: session-factory = " + session_factory);
//		        	Map map_or_mappings = new HashMap();
		        	List<Element> mappingsElement = or_mappings.getChildren("mapping");
		        	List<String> mappingClasses = new ArrayList<String>();
		        	List<String> mappingResources = new ArrayList<String>();
		        	for(int k=0; k<mappingsElement.size(); k++){
		        		Element mapping = (Element)mappingsElement.get(k);
		        		
		        		//判断是采用哪种形式的映射定义
		        		//1. 注解模式
		        		String className = mapping.getAttributeValue("class");
		        		if(className != null){
		        			mappingClasses.add(className);
		        			logger.info("加载映射文件 " + configName + " 配置信息: class = " + className);
		        		}
		        		
		        		//2. 映射文件模式
		        		String resource = mapping.getAttributeValue("resource");
		        		if(resource != null){
		        			mappingResources.add(resource);
		        			logger.info("加载映射文件 " + configName + " 配置信息: resource = " + resource);
		        		}
		        	}
		        	
		        	pdc.setMappingClasses(mappingClasses);  //保存
		        	pdc.setMappingResources(mappingResources); //保存
		        	
		        	persistenceConfigs.add(pdc);
		        }
	        	logger.info("<<<<<<<<<<<<<<<<<<<<<<加载映射文件结束: " + configName );

	        } catch (Exception e) {
				throw new AppContextInitException("00000103: 读取持久层映射配置 " + configName + " 信息时出现异常!", e);
			} 

	        return persistenceConfigs;
	    }
	    
    
	/**
	 * 解析异常模板信息
	 * 
	 * @param modelName 模块名称
	 * 
	 * @return
	 * @throws Exception
	 */
    private Properties loadExceptionConfig(String modelName) throws AppContextInitException{
		InputStream is = null;
		Properties props = new Properties();
		try {
	    	URL url = this.getClass().getClassLoader().getResource("exception-" + modelName + ".properties");
			is = url.openStream();
			if(is == null){
				logger.info("CSSAppContext加载时,没有发现配置文件 " + "exception-" + modelName + ".properties!");
				return props;
			}
			props.load(is);
			logger.info("CSSAppContext加载配置文件 " + "exception-" + modelName + ".properties 成功!");
			
		} catch (Exception e) {
			throw new AppContextInitException("00000104: exception 配置文件 " + "exception-" + modelName + ".properties" + " 解析出现异常!", e);
		}
		return props;
	}

    /**
     * 解析sql语句配置文件
     * 
     * @param modelName 模块名称
     * 
     * @throws Exception
     */
    private Properties loadSqlConfig(String modelName) throws AppContextInitException{
    	Properties props = new Properties();
    	
    	//1. 加载基础sql资源文件
    	String sqlFile = "sql-" + modelName + ".xml";
    	logger.info("加载基础sql资源文件: sqlfile = " + sqlFile);
    	URL url = this.getClass().getClassLoader().getResource(sqlFile);
    	Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        try {
            InputStream is = url.openStream();
			if(is == null){
				logger.info("CSSAppContext加载时,没有发现配置文件 " + sqlFile);
				return props;
			}

            doc = builder.build(is);
            Element rootElement = doc.getRootElement();
            Iterator<?> sqlList = rootElement.getChildren("sql").iterator();
            while (sqlList.hasNext()) {
                Element sqlElement = (Element) sqlList.next();
                String name = sqlElement.getAttributeValue("name");
                String value = sqlElement.getValue();
                props.setProperty(name, value);
                logger.info("加载sql资源配置信息:" + name + "=" + value);
            }
        } catch (Exception ex) {
            throw new AppContextInitException("00000105: SQL配置文件 " + sqlFile + " 解析出现异常!", ex);
        }

        //2. 加载扩展sql资源文件
        String  sqlExtFile = "sql-" + modelName + "-" + this.dbType + ".xml";
        logger.info("加载扩展sql资源文件: sqlExt = " + sqlExtFile);
        URL extUrl = this.getClass().getClassLoader().getResource(sqlExtFile);
        Document extDoc = null;
        SAXBuilder extBuilder = new SAXBuilder();
        try {
        	if(extUrl == null){
        		return props;
        	}
        	
            InputStream is = extUrl.openStream();
			if(is != null){
				extDoc = extBuilder.build(is);
	            Element rootElement = extDoc.getRootElement();
	            Iterator<?> sqlList = rootElement.getChildren("sql").iterator();
	            while (sqlList.hasNext()) {
	                Element sqlElement = (Element) sqlList.next();
	                String name = sqlElement.getAttributeValue("name");
	                String value = sqlElement.getValue();
	                props.setProperty(name, value);
	                logger.info("加载sqlExt(" + this.dbType + ")资源配置信息:" + name + "=" + value);
	            }
			}
			else{
				logger.info("没有发现数据资源扩展配置文件  " + sqlExtFile);
			}
        } catch (Exception ex) {
            throw new AppContextInitException("00000109: SQL配置文件 " + sqlExtFile + " 解析出现异常!", ex);
        }
        
        return props;
    }

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

    
}
