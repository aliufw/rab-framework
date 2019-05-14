package com.rab.framework.component.dictcache;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;


/**
 * 
 * <P>Title: XMLCacheManager</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>浏览器缓存web端数据文件管理器</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class XMLCacheManager {
	
	/**
	 * 日志记录对象
	 */
	private final static LogWritter logger = LogFactory.getLogger(XMLCacheManager.class);
	
	private static DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();

	private DocumentBuilder builder;
	
//	private org.w3c.dom.Document doc;
	
	/**
	 * 代码表缓存xml文件保存路径
	 */
	private String localXMLCacheRoot = "";
	
	/**
	 * 代码表和映射版本列表
	 */
	private Map<String,Integer> versionList = new HashMap<String,Integer>();
	
	
	public XMLCacheManager(){
		try {
			builder=dbFact.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("创建文档builder出现异常！", e);
		}
	}
	
	public void init(){
		File rootdir = new File(localXMLCacheRoot);
		if(!rootdir.exists()){
			rootdir.mkdirs();
		}
		File file = new File(rootdir, "versionlist.xml");
		if(!file.exists()){
			return;
		}
        try{
        	Document doc = builder.parse(file);
            Element rootElement = doc.getDocumentElement();
            NodeList fileIter = rootElement.getChildNodes();
            for(int i=0;i<fileIter.getLength();i++) {
                Node fileElement = (Node) fileIter.item(i);
                if(fileElement.getAttributes()!=null){
	                String name = fileElement.getAttributes().getNamedItem("name").getNodeValue();
	                String value = fileElement.getAttributes().getNamedItem("version").getNodeValue();
	                
	                name = name.toLowerCase();
	                Integer version = new Integer(value);
	                this.versionList.put(name, version);
	                logger.debug("加载本地文件缓存中的初始版本信息:" + name + " (version)= " + value);
                }
            }

        } catch (Exception ex) {
            logger.error("代码表缓存管理程序版本标识初始化解析出现异常!", ex);
        }
	}
	
	public String getLocalXMLCacheRoot() {
		return localXMLCacheRoot;
	}

	public void setLocalXMLCacheRoot(String localXMLCacheRoot) {
		this.localXMLCacheRoot = localXMLCacheRoot;
	}

	public void updateLocalXMLCache(Map<String,CacheTable> data) {
		// 1. 更新 versionList
		Iterator<CacheTable> iter = data.values().iterator();
		while (iter.hasNext()) {
			CacheTable table = (CacheTable) iter.next();
			String tableName = table.getTableName().toLowerCase();
			int version = table.getVersion();

			Object obj = versionList.get(tableName);
			int verOld = -1;
			if(obj == null){
				verOld = -1;
			}
			else{
				verOld = ((Integer)obj).intValue();
			}
			if(version > verOld){
				this.versionList.put(tableName, new Integer(version));
				//2. 更新代码表缓存到本地硬盘
				this.saveCacheTable(tableName, table);
			}
		}

		//3. 更新后的versionList保存到硬盘
		this.createVersionListFile();
	}
	
	/**
	 * 将代码表转换为xml后保存到本地硬盘
	 * 
	 * @param tableName
	 *            表名
	 * @param table
	 *            代码表缓存数据对象
	 */
	private void saveCacheTable(String tableName, CacheTable table){
		
		Document doc=builder.newDocument();
		Element root= doc.createElement("table");
		root.setAttribute("name",tableName);
		doc.appendChild(root);
		List<Map<String,Object>> list = table.getCacheData();

		if(table.getCacheType() == CacheTable.CACHE_TYPE_DB && 
				(table.getCacheData() == null || table.getCacheData().size()==0)){
			list = ServerCacheManager.getDictCacheService().getCacheDataFromDB(tableName,"");
//			return;
		}
		
		for(int index=0; index<list.size(); index ++){
			Map<String,Object> row = list.get(index);
			Element datarow = doc.createElement("row");
			Iterator<String> iter = row.keySet().iterator();
			while(iter.hasNext()){
				String key = "" + iter.next();
				String value = "" + row.get(key);
//				value = StringUtils.utftoGBK(value); 
				datarow.setAttribute(key, value);
			}
			root.appendChild(datarow);
		}
		
		createXMLFile(doc, tableName.toUpperCase() + ".xml");

	}

	/**
	 * 创建版本列表信息,并保存到本地硬盘
	 * 
	 * @param pool 缓存容器
	 */
	private void createVersionListFile(){
		Document doc=builder.newDocument();
		Element root= doc.createElement("files");
		doc.appendChild(root);
		
		Iterator<String> iter = this.versionList.keySet().iterator();
		while(iter.hasNext()){
			String tableName = "" + iter.next();
			Integer version = (Integer)this.versionList.get(tableName);
			
			Element file =  doc.createElement("file");
			file.setAttribute("name", tableName.toLowerCase());
			file.setAttribute("version", "" + version);
			root.appendChild(file);
		}
		
		createXMLFile(doc, "versionlist.xml");
		
	}
	
	private void createXMLFile(Document doc, String fileName){
		try {
			File rootdir = new File(localXMLCacheRoot);
			if(!rootdir.exists()){
				rootdir.mkdirs();
			}
			File file = new File(rootdir, fileName);
            Transformer trans = TransformerFactory.newInstance().newTransformer();
//            trans.setOutputProperty(OutputKeys.ENCODING,"GB2312");
            trans.setOutputProperty(OutputKeys.INDENT,"yes");
            trans.transform(new DOMSource(doc),new StreamResult(new FileOutputStream(file, false)));           
        }
        catch (Exception e) {
        	logger.error("将Document对象输出为xml时出现异常!", e);
        }

	}
}

