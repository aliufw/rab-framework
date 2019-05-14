package com.rab.framework.comm.appcontext;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.FileUtils;

/**
 * 
 * <P>Title: BootstrapLoader</P>
 * <P>Description: </P>
 * <P>����˵������ʼ�������ļ�������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-30</P>
 *
 */
public class BootstrapLoader {
	/**
	 * ��־��¼��
	 */
	private final static LogWritter logger = LogFactory.getLogger(BootstrapLoader.class);

	/**
	 * ��ʼ�������ļ�����
	 */
	private final String bootstrap = "bootstrap.xml";

	private Map<String, Object> pool = new HashMap<String, Object>();
	
	
	public Map<String, Object> load() throws AppContextInitException {
		// �õ������ļ���������
		FileUtils fileUtils = new FileUtils(this.bootstrap);
		InputStream is = fileUtils.getInputStream();
		if (is == null) {
			throw new AppContextInitException("00000101: ϵͳ�����ļ� bootstrap.xml ��ȡ���󣡳�����ֹ���������л�����");
		}

		Document doc = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(is);
		} catch (Exception ex) {
			throw new AppContextInitException("00000108: �����ļ� bootstrap.xml����ʧ�ܣ�", ex);
		}
		
		Element root = doc.getRootElement();

		// 1. ���� property ����
		List<Element> properties = root.getChildren("property");
		for (int i = 0; i < properties.size(); i++) {
			Element property = (Element) properties.get(i);
			String name = property.getAttributeValue("name").trim();
			String value = property.getAttributeValue("value").trim();
			addKey(name, value);
		}

		// 2. ������Ⱥ����
		Element cluster = root.getChild("cluster");
		if (cluster != null) {
			List<Attribute> attributes = cluster.getAttributes();
			Properties props = new Properties();
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = (Attribute) attributes.get(i);
				String name = attribute.getName();
				String value = attribute.getValue();
				props.setProperty(name, value);
			}
			addKey("cluster", props);
		}

		// 3. �㲥����
		Element multicast = root.getChild("multicast");
		if (multicast != null) {
			List<Attribute> attributes = multicast.getAttributes();
			Properties props = new Properties();
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = (Attribute) attributes.get(i);
				String name = attribute.getName();
				String value = attribute.getValue();
				props.setProperty(name, value);
			}
			addKey("multicast", props);
		}

		// 4. ���������������
		Element codecache = root.getChild("codecache");
		if (codecache != null) {
			List<Attribute> attributes = codecache.getAttributes();
			Properties props = new Properties();
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = (Attribute) attributes.get(i);
				String name = attribute.getName();
				String value = attribute.getValue();
				props.setProperty(name, value);
			}
			addKey("codecache", props);
		}

		// 5. ����������
		List<Element> startapps = root.getChildren("startapp");
		if(startapps.size() > 0){
			Properties props = new Properties();
			for (int i = 0; i < startapps.size(); i++) {
				Element startapp = (Element) startapps.get(i);
				List<Attribute> attributes = startapp.getAttributes();
				for (int k = 0; k < attributes.size(); k++) {
					Attribute attribute = (Attribute) attributes.get(k);
					String name = attribute.getName().trim();
					if(name.startsWith("startup.")){
						String value = attribute.getValue().trim();
						props.setProperty(name, value);
						break;
					}
				}
				addKey("startapp", props);
			}
		}

		// 6. ������ʱ����
		Element scheduler = root.getChild("scheduler");
		if (scheduler != null) {
			List<Attribute> attributes = scheduler.getAttributes();
			Properties props = new Properties();
			for (int i = 0; i < attributes.size(); i++) {
				Attribute attribute = (Attribute) attributes.get(i);
				String name = attribute.getName();
				String value = attribute.getValue();
				props.setProperty(name, value);
			}
			addKey("scheduler", props);
		}

		// 7. EJB����
		List<Element> ejbs = root.getChildren("ejb");
		Map<String, Properties> map = new HashMap<String, Properties>();
		for (int i = 0; i < ejbs.size(); i++) {
			Element ejb = (Element) ejbs.get(i);
			List<Attribute> attributes = ejb.getAttributes();

			Properties props = new Properties();
			for (int k = 0; k < attributes.size(); k++) {
				Attribute attribute = (Attribute) attributes.get(k);
				String name = attribute.getName();
				String value = attribute.getValue();
				props.setProperty(name, value);
			}
			String name = props.getProperty("jndi");
			map.put(name, props);
		}
		addKey("ejb", map);

		// 8. data-source����
		List<Element> dataSources = root.getChildren("data-source");
		Map<String, Properties> mapDS = new HashMap<String, Properties>();
		for (int i = 0; i < dataSources.size(); i++) {
			Element dataSource = (Element) dataSources.get(i);
			List<Attribute> attributes = dataSource.getAttributes();

			Properties props = new Properties();
			for (int k = 0; k < attributes.size(); k++) {
				Attribute attribute = (Attribute) attributes.get(k);
				String name = attribute.getName();
				String value = attribute.getValue();
				props.setProperty(name, value);
			}
			String name = props.getProperty("jndi");
			mapDS.put(name, props);
		}
		addKey("data-source", mapDS);

		
		return this.pool;
	}

	/**
	 * ���ö������صķ���, ��key��value���뵽����
	 * 
	 * @param key
	 * @param value
	 */
	private void addKey(String key, Object value) {
		pool.put(key, value);
		logger.info("����" + key + "[" + value + "]");
	}

}
