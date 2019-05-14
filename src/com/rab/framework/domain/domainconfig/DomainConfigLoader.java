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
 * <P>����˵����ҵ��������ļ�������</P>
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
	 * ��־��¼����
	 */
	private final static LogWritter logger = LogFactory.getLogger(CoreAppServer.class);
	
	/**
	 * �־ò�ͨ��������ϢĬ�������ļ�����
	 */
	private String persistenceCommonConfigFile = "persistence.xml";
	
	/**
	 * ģ��������Ϣ�����ļ�����
	 */
	private String appcomponentfile = "appcomponent.xml";
	
	/**
	 * ���ݿ����ͣ�����ָʾ����������ض����ݿ��sql��Դ�ļ�
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
            
            logger.info("=============>>��ʼ���س־ò�ͨ����Ϣ: " + persistenceCommonConfigFile);
            for (int i = 0; i < list.size(); i++) {
				Element session_factory = list.get(i);
				PersistenceCommonConfig pcc = new PersistenceCommonConfig();
        		//session-factory name 
				String sessionFactoryName = session_factory.getAttributeValue("name");
				pcc.setSessionFactoryName(sessionFactoryName);
				logger.info("���س־ò�ͨ����Ϣ<session-factory>: name = " + sessionFactoryName);		
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
					logger.info("���س־ò�ͨ����Ϣ<session-factory-config>: " + key + " = " + value);					
					props.setProperty(key, value);
				}
				pcc.setSessionFactoryconfig(props);

				persistenceCommonConfig.put(sessionFactoryName, pcc);
	            logger.info("=============<<���س־ò�ͨ����Ϣ����: " + persistenceCommonConfigFile);

			}
        } catch (Exception ex) {
        	throw new AppContextInitException("00000003:�־ò�ͨ��������Ϣ��������ʱ�����쳣!",ex);
        }       
        return persistenceCommonConfig;
    }
		
	/**
	 * ����ҵ��ģ��������Ϣ
	 * 
	 * @return
	 */
	public Map<String,ModelConfig> loadModel() throws AppContextInitException {
		Map<String, ModelConfig> components = getAppcomponents(appcomponentfile);
		
		Iterator<String> iter = components.keySet().iterator();
		while(iter.hasNext()){
			String modelName = iter.next();
			ModelConfig mc = (ModelConfig)components.get(modelName);
						
			//���������ļ�domain-*.xml
			DomainConfig domainConfig = loadDomainConfig(modelName);
			mc.setDomainConfig(domainConfig);
			
			//���������ļ�persistence-*.xml
			List<PersistenceDomainConfig> persistenceConfigs = this.getPersistenceDomainConfig(modelName);
			mc.setPersistenceConfigs(persistenceConfigs);
			
			//�����쳣ģ�������ļ� exception-*.properties
			Properties exceptionConfig = this.loadExceptionConfig(modelName);
			mc.setExceptionConfigs(exceptionConfig);
			
			//����SQL������ļ� sql-*.xml
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
//    	throw new AppContextInitException("00000107: ƽ̨ģ�������ļ�["+appcomponentfile+"]��ȡ������ֹ���У�");
//    }
		if(is == null) {
			logger.info("====================================================================");
			logger.info("û�з���ƽ̨ģ�������ļ�["+appcomponentfile+"]��ϵͳ���Կͻ���ģʽ���У�");
			logger.info("====================================================================");
			return map;
		}

        Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        try {
            doc = builder.build(is);      
            Element rootElement = doc.getRootElement();

            //<app-component>
            //����ʱ�������Ϣ
            List<Element> list = rootElement.getChildren("app-component");
            for(int i=0; i<list.size(); i++){
            	Element app_component = (Element)list.get(i);
            	ModelConfig mc = new ModelConfig();
            	//<name> �������
            	String name = app_component.getChild("name").getText();
            	mc.setModelName(name);
            	
            	//<description> ���������Ϣ
            	String description = "";
            	if(app_component.getChild("description") != null)
            		description = app_component.getChild("description").getText();           	
            	mc.setDescription(description);
            	
            	map.put(name, mc);
            }          
        } catch (Exception ex) {
            throw new AppContextInitException("0000100: Ӧ���������ʧ��, ���������ļ�appcomponent.xml���������û��ʽ�Ƿ���ȷ!", ex);
        } 	
        return map;
	}
   	

	/**
	 * ԭ�е����ڽ���domain-*.xml�ļ�����
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
				logger.info("����domain������Ϣ��" + tcfg);
			}
			return dc;
			
		} catch (Exception e) {
			throw new AppContextInitException("00000102����Domain�������ļ� " + configName + " ʱ�����쳣! ", e);
		}
	}

	private List<PersistenceDomainConfig> getPersistenceDomainConfig(String modelName) throws AppContextInitException{
	    	List<PersistenceDomainConfig> persistenceConfigs = new ArrayList<PersistenceDomainConfig>();
			String configName = "persistence-" + modelName + ".xml";

	    	try {
	        	//����ģ�������������Ϣ
	        	URL url = this.getClass().getClassLoader().getResource(configName);
	        	if(url == null){
	        		throw new AppContextInitException("00000106: CSSAppContext����ʱ,û�з���ӳ�������ļ� " + configName);
	        	}
	        	InputStream is = url.openStream();

	        	logger.info(">>>>>>>>>>>>>>>>>>>>>>��ʼ����ӳ���ļ�: " + configName );

		    	SAXBuilder builder = new SAXBuilder();
		    	Document doc = builder.build(is);
				Element root = doc.getRootElement();
		        List<Element> list = root.getChildren("or-mappings");
		        for(int i=0; i<list.size(); i++){
		        	Element or_mappings = (Element)list.get(i);
		        	PersistenceDomainConfig pdc = new PersistenceDomainConfig();
		        	
		        	String session_factory =  or_mappings.getAttributeValue("session-factory");
		        	pdc.setSessionfactoryName(session_factory);
		        	logger.info("����ӳ���ļ� " + configName + " ������Ϣ: session-factory = " + session_factory);
//		        	Map map_or_mappings = new HashMap();
		        	List<Element> mappingsElement = or_mappings.getChildren("mapping");
		        	List<String> mappingClasses = new ArrayList<String>();
		        	List<String> mappingResources = new ArrayList<String>();
		        	for(int k=0; k<mappingsElement.size(); k++){
		        		Element mapping = (Element)mappingsElement.get(k);
		        		
		        		//�ж��ǲ���������ʽ��ӳ�䶨��
		        		//1. ע��ģʽ
		        		String className = mapping.getAttributeValue("class");
		        		if(className != null){
		        			mappingClasses.add(className);
		        			logger.info("����ӳ���ļ� " + configName + " ������Ϣ: class = " + className);
		        		}
		        		
		        		//2. ӳ���ļ�ģʽ
		        		String resource = mapping.getAttributeValue("resource");
		        		if(resource != null){
		        			mappingResources.add(resource);
		        			logger.info("����ӳ���ļ� " + configName + " ������Ϣ: resource = " + resource);
		        		}
		        	}
		        	
		        	pdc.setMappingClasses(mappingClasses);  //����
		        	pdc.setMappingResources(mappingResources); //����
		        	
		        	persistenceConfigs.add(pdc);
		        }
	        	logger.info("<<<<<<<<<<<<<<<<<<<<<<����ӳ���ļ�����: " + configName );

	        } catch (Exception e) {
				throw new AppContextInitException("00000103: ��ȡ�־ò�ӳ������ " + configName + " ��Ϣʱ�����쳣!", e);
			} 

	        return persistenceConfigs;
	    }
	    
    
	/**
	 * �����쳣ģ����Ϣ
	 * 
	 * @param modelName ģ������
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
				logger.info("CSSAppContext����ʱ,û�з��������ļ� " + "exception-" + modelName + ".properties!");
				return props;
			}
			props.load(is);
			logger.info("CSSAppContext���������ļ� " + "exception-" + modelName + ".properties �ɹ�!");
			
		} catch (Exception e) {
			throw new AppContextInitException("00000104: exception �����ļ� " + "exception-" + modelName + ".properties" + " ���������쳣!", e);
		}
		return props;
	}

    /**
     * ����sql��������ļ�
     * 
     * @param modelName ģ������
     * 
     * @throws Exception
     */
    private Properties loadSqlConfig(String modelName) throws AppContextInitException{
    	Properties props = new Properties();
    	
    	//1. ���ػ���sql��Դ�ļ�
    	String sqlFile = "sql-" + modelName + ".xml";
    	logger.info("���ػ���sql��Դ�ļ�: sqlfile = " + sqlFile);
    	URL url = this.getClass().getClassLoader().getResource(sqlFile);
    	Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        try {
            InputStream is = url.openStream();
			if(is == null){
				logger.info("CSSAppContext����ʱ,û�з��������ļ� " + sqlFile);
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
                logger.info("����sql��Դ������Ϣ:" + name + "=" + value);
            }
        } catch (Exception ex) {
            throw new AppContextInitException("00000105: SQL�����ļ� " + sqlFile + " ���������쳣!", ex);
        }

        //2. ������չsql��Դ�ļ�
        String  sqlExtFile = "sql-" + modelName + "-" + this.dbType + ".xml";
        logger.info("������չsql��Դ�ļ�: sqlExt = " + sqlExtFile);
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
	                logger.info("����sqlExt(" + this.dbType + ")��Դ������Ϣ:" + name + "=" + value);
	            }
			}
			else{
				logger.info("û�з���������Դ��չ�����ļ�  " + sqlExtFile);
			}
        } catch (Exception ex) {
            throw new AppContextInitException("00000109: SQL�����ļ� " + sqlExtFile + " ���������쳣!", ex);
        }
        
        return props;
    }

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

    
}
