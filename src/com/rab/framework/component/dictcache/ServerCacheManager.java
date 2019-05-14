package com.rab.framework.component.dictcache;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.ExceptionInfo;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.FileUtils;
import com.rab.framework.component.dictcache.manager.DictMetaInfo;
import com.rab.framework.delegate.BizDelegate;
import com.rab.framework.web.action.util.ConvertLetterUtil;

/**
 * 
 * <P>Title: ServerCacheManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * ����������
 * 
 * ʵ���������ӿ�: 
 *    CacheUserInterface - �����û��ӿ�,��Ҫ���ڴӻ�������ȡ����
 *    CacheManager       - �������ӿ�,��Ҫ���ڻ���Ĺ�����
 * 
 * �������ǻ������ĺ��Ŀ����߼�,��Ҫ�õ���ϵͳ���ò�������:
 * use-memory-cache - true or false , ���建�����ģʽ
 * update-cyc       - ���ͱ���,��ʾ�����������,��λΪ����
 * catalog-table    - �ַ�������, ��ʾĿ¼������

 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class ServerCacheManager implements DictCacheService, DictCacheManager{
    /**
     * ��־��¼����
     */
	protected static final LogWritter log = LogFactory.getLogger(ServerCacheManager.class);

    /**
     * ��Ŵ��뻺������Ա���,���д�ŵ��� CacheTable���󼯺�
     */
    private Map<String,CacheTable> codeCachePool = new HashMap<String,CacheTable>();

    /**
     * CacheManager �ľ�̬����,������ĳ�ʼ�����Ϊȫ�ֵ�һ��ʵ��.
     */
    private static ServerCacheManager instance = null;

    /**
     * �Ƿ�ʹ���ڴ滺��,��Ҫ��Ӧ�ڿ���ģʽ������ģʽ
     * 
     * ����ģʽ: useMemoryCache=false, �ڴ��в������κ�����,�κ����ݷ��ʶ�ֱ�Ӵ����ݿ�����ȡ,�Լ��������ع���
     * ����ģʽ: useMemoryCache=true,  �����ݻ������ڴ���, ���ϵͳ����ʱ����
     */
    private boolean useMemoryCache = true;
    
    /**
     * �Ƿ񽫴������XML��ʽ���浽����Ӳ��
     */
    private boolean useLocalXMLCache = false;
    
//    /**
//     * ��������浽����Ӳ��ʱ�Ĵ��·��
//     */
//    private String localXMLCacheDir = "";
    
    /**
     * ��������浽����Ӳ�̵Ĺ������
     */
    private XMLCacheManager xmlCacheManager = null;
    
    /**
     * ����߳�,����ʵʱ�ӷ������˱�������һ���Ը���
     */
    private Thread monitor = null; 
    
    private String dictMetaFile = "dictmeta.xml";
    /**
     * �����ֵ���Ԫ��������
     */
    Map<String, DictMetaInfo> dictMetaInfos = new HashMap<String, DictMetaInfo>();
    
    /**
     * ˽�й�����
     *
     */
    private ServerCacheManager(){
    	init();
    }
    
    /**
     *��ʼ��,����:
     *	���ػ�������
     *	����ͬ������߳�
     *
     */
    private void init(){
    	Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
    	String strUseMemoryCache = props.getProperty("use-memory-cache");
    	if(strUseMemoryCache != null 
    			&& strUseMemoryCache.equalsIgnoreCase("true")){
    		this.useMemoryCache = true;
    	}
    	else{
    		this.useMemoryCache = false;
    	}
    	
    	String strLocalXMLCacheDir = props.getProperty("local-xml-cache-dir");
    	if(strLocalXMLCacheDir != null 
    			&& strLocalXMLCacheDir.trim().length()>0){
    		this.useLocalXMLCache = true;
    		this.xmlCacheManager = new XMLCacheManager();
    		xmlCacheManager.setLocalXMLCacheRoot(strLocalXMLCacheDir.trim());
    		xmlCacheManager.init();
    	}
    	else{
    		this.useLocalXMLCache = false;
    		this.xmlCacheManager = null;
    	}
    	
//    	load();
//    	startMonitor();
    }
    
    /**
     * �����û��ӿ�
     * @return CacheUserInterface
     */
    public static DictCacheService getDictCacheService() {
        if (instance == null) {
        	instance = new ServerCacheManager();
        }
        return instance;
    }

    /**
     * ���ع���ӿ�
     * 
     * @return CacheManager
     */
    public static DictCacheManager getDictCacheManager() {
        if (instance == null) {
        	instance = new ServerCacheManager();
        }

    	return (ServerCacheManager)instance;
    }

	public Thread getMonitor() {
		return monitor;
	}

	public Map<String,CacheTable> getCodeCachePool() {
		return codeCachePool;
	}

	public boolean isUseMemoryCache() { 
		return useMemoryCache;
	}
	
	//----------------------------------------------------------------------



	/**
	 * ���ݱ�����,�����������ݣ����ݷ�����̬ΪList������valueΪ������
	 * 
	 * @param tableName ������
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row]�� ���û���ҵ�������ݣ��򷵻ؿյ�list�б�
	 */
	public List<Map<String,Object>> getCacheData(String tableName){
		if(!this.useMemoryCache){
			return this.getDataFromServer(tableName, null);
		}
		
		tableName = tableName.toUpperCase();		
		
		CacheTable table = (CacheTable)this.codeCachePool.get(tableName);
		if(table == null){
			return null;
		}
		
		List<Map<String,Object>> list = null;
		if(table.getCacheType() == CacheTable.CACHE_TYPE_DB){
			list = this.getDataFromServer(tableName, null);
		}
		else{
			list = table.getCacheData();
		}
		
		if(list  == null){
			list = new ArrayList<Map<String,Object>>();
		}
		
		return list;
	}
	
	/**
	 * ���ݱ�����,������������,���ݵķ�����̬ΪMap������keyΪָ���ֶε�ֵ��valueΪ��Ӧ��������
	 * 
	 * @param tableName  ������
	 * @param keyFieldName ���������б���������� "," �ָ�
	 * @return  ���������б�,��װ��ʽΪ: Map[map], �൱�ڱ�[row]
	 */
	public Map<String,Map<String,Object>> getCacheDataMap(String tableName, String keyFieldName){
		List<Map<String,Object>> list = this.getCacheData(tableName);
		
		Map<String,Map<String,Object>> map = convertList2Map(list, keyFieldName);
		
		return map;
	}
	
	
	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�����ȡ���������</P>
	 *  <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * @param tableName ����
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row]�� ���û���ҵ�������ݣ��򷵻ؿյ�list�б�
	 */
	public List<Map<String,Object>> getCacheData(String tableName, List<CacheFilter> filters) {
		if(!this.useMemoryCache){
			return this.getDataFromServer(tableName, filters);
		}
		tableName = tableName.toUpperCase();
		
		CacheTable table = (CacheTable)this.codeCachePool.get(tableName);
		if(table == null){
			return null;
		}
		
		List<Map<String,Object>> list = null;
		if(table.getCacheType() == CacheTable.CACHE_TYPE_DB){
			list = this.getDataFromServer(tableName, filters);
			return list;
		}
		
		list = table.getCacheData();
		if(list == null){
			return null;
		}
		
		List<Map<String,Object>> filterList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> mirror = list;
		for(int i=0; i<filters.size(); i++){
			CacheFilter cf = (CacheFilter)filters.get(i);
			String fieldName = cf.getFieldName();
			Object fieldValue = cf.getFieldValue();
			String filterOperator = cf.getFilterOperator();
			
			if(fieldValue == null){
				continue;
			}
			
			for(int k=0; k<mirror.size(); k++){
				Map<String,Object> map = mirror.get(k);
				if(filterOperator.equals(CacheFilter.FILTER_OPERATOR_EQUAL)){
					Object data = map.get(fieldName.toUpperCase());
					if(fieldValue instanceof String){
						data = "" + data;
					}
					if(fieldValue.equals(data)){
						filterList.add(map);
					}
				}
				/**
				 * add by LISHIX 20111223
				 * ���ӿ�ͨ�����ֵ�ƴ������ĸ����ƥ��
				 * start
				 */
//				else if(filterOperator.equals(CacheFilter.FILTER_OPERATOR_LIKE)){
//					Object data = map.get(fieldName.toUpperCase());
//					String sValue = "" + data;       //�ֵ���ֶ�ֵ
//					String sModel = "" + fieldValue; //ģʽƥ���ִ�
//					if(sValue.indexOf(sModel) >= 0){
//						filterList.add(map);
//					}
//				}
				else if(filterOperator.equals(CacheFilter.FILTER_OPERATOR_LIKE)){
					Object data = map.get(fieldName.toUpperCase());
					String sValue = "" + data;       //�ֵ���ֶ�ֵ
					String sModel = "" + fieldValue; //ģʽƥ���ִ�
					if(sValue.indexOf(sModel) >= 0){
						filterList.add(map);
					}else{
						String sValueLetter=ConvertLetterUtil.getFirstLetter(sValue);
						sModel=sModel.toLowerCase();
						if(sValueLetter.indexOf(sModel) >= 0){
							filterList.add(map);
						}
					}
				}
				/**
				 * end
				 */
				else if(filterOperator.equals(CacheFilter.FILTER_OPERATOR_NOT_EQUAL)){
					Object data = map.get(fieldName.toUpperCase());
					if(fieldValue instanceof String){
						data = "" + data;
					}
					if(!fieldValue.equals(data)){
						filterList.add(map);
					}
				}
				else if(filterOperator.equals(CacheFilter.FILTER_OPERATOR_IN)){
					List<String> fieldValueList = (List)fieldValue;
					for(int index = 0; index <fieldValueList.size(); index++ ){
						String s1 = "" + fieldValueList.get(index);
						String s2 = "" + map.get(fieldName.toUpperCase());
						if(s1.equals(s2)){
							filterList.add(map);
							break;
						}
					}
				}
				else if(filterOperator.equals(CacheFilter.FILTER_OPERATOR_NOT_IN)){
					List<String> fieldValueList = (List)fieldValue;
					boolean flag = true;
					for(int index = 0; index <fieldValueList.size(); index++ ){
						String s1 = "" + fieldValueList.get(index);
						String s2 = "" + map.get(fieldName.toUpperCase());
						if(s1.equals(s2)){
							flag = false;
							break;
						}
					}
					
					if(flag){
						filterList.add(map);
					}
				}
			}

			mirror = filterList;
			filterList = new ArrayList<Map<String,Object>>();
		}
		
		filterList = mirror;
		
		if(filterList  == null){
			filterList = new ArrayList<Map<String,Object>>();
		}

		return filterList;
	}
	
	/**
	 * <P>���ݱ����ơ���ʼ��������š����������͹�������,�ӻ�����ȡ��������ݣ����ݷ�����̬ΪList������valueΪ������</P>
	 *  <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * @param tableName ����
	 * @param start     ��ʼ��������ţ��ӡ�0����ʼ����
	 * @param offset    ��Ҫ���ص���������
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @return          ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row],�������map������Ϊ����field������Ӧ��ֵ
	 */
	public List<Map<String,Object>> getCacheData(String tableName, int start, int offset, List<CacheFilter> filters){
		List<Map<String,Object>> retList = new ArrayList<Map<String,Object>>();
		
		//���������������߼����򷵻ؿյ����ݼ�
		if(start < 0 || offset < 0){
			return retList;
		}
		
		List<Map<String,Object>> list = getCacheData(tableName, filters);
		if(list.size() >= (start + offset)){
			//1. list.size() >= start + offset
			for(int i=start; i<start + offset; i++){
				retList.add(list.get(i));
			}
		}
		else if(list.size() >= start && list.size() < (start + offset)){
			//2. list.size() >= start && list.size() < (start + offset)
			for(int i=start; i<list.size(); i++){
				retList.add(list.get(i));
			}
		}
		else{
			//3. list.size() < start
			return retList;
		}
		
		return retList;
	}
	
	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�����ȡ���������.���ݵķ�����̬ΪMap������keyΪָ���ֶε�ֵ��valueΪ��Ӧ��������</P>
	 *  <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * @param tableName ����
	 * @param filters  ��������, ��װ��CacheFilter����ʵ����
	 * @param keyFieldName ���������б���������� "," �ָ�
	 * @return  ���������б�,��װ��ʽΪ: Map[map], �൱�ڱ�[row],�������map������Ϊ����field������Ӧ��ֵ
	 */
	public Map<String,Map<String,Object>> getCacheDataMap(String tableName, List<CacheFilter> filters, String keyFieldName){
		List<Map<String,Object>> list = this.getCacheData(tableName,filters);
		
		Map<String,Map<String,Object>> map = convertList2Map(list, keyFieldName);
		
		return map;
	}
	
	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�����ȡ��������ݣ����ݷ�����̬ΪList������valueΪ������</P>
	 * <P>���������ڷ��ڴ滺����ֵ���������ȡ��һ�������������ܴ󡢲��ʺ����ڴ滺����ֵ��</P>
	 * <P></P>
	 * 
	 * @param tableName ����
	 * @param filterSQL   where����sql�ַ���,����where�ؼ���
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row]
	 */
	public List<Map<String,Object>> getCacheDataFromDB(String tableName, String filterSQL){
		List<Map<String,Object>> ret = null;
		log.debug("�ӷ������˼��ص������ݣ� tableName=" + tableName + " filterSQL=" + filterSQL);
		
		tableName = tableName.toUpperCase();
		
		CacheReqEvent req = new CacheReqEvent("CacheUpdateManagerBLH");
		req.setMethod(DictCacheManager.SERVER_METHOD_LOADWITHSQL);
		
		req.setTableName(tableName);
		req.setSqlWhere(filterSQL);
		
		Map<String,CacheTable> data = delegate(req);
		
        CacheTable ct = (CacheTable)data.get(tableName);
        ret = ct.getCacheData();


		return ret;
	}
	
	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�������ȡһ�����ݡ�����ж���ƥ���н������ֻ���ص�һ����</P>
	 * <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * @param tableName ����
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @return ����������,��װ��ʽΪ: map,�൱�ڱ�[row]�� 
	 */
	public Map<String,Object> getCacheDataRow(String tableName, List<CacheFilter> filters){
		List<Map<String,Object>> list = this.getCacheData(tableName, filters);
		if(list.size() > 0){
			return list.get(0);
		}
		else{
			return new HashMap<String,Object>();
		}
	}
	
	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�����ȡ��������ݡ�</P>
	 * <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * <P>�������ݹ������£�</P>
	 * <P>1. ֻ����ָ���ֶε�һ��ֵ</P>
	 * <P>2. ����������������1����ֻ��������������ָ���ֶε�һ��ֵ</P>
	 * <P></P>
	 * 
	 * @param tableName ����
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @param colName   �ֶ���
	 * @return �����ֶ���key��Ӧ���ֶ�ֵ, ����ж���ƥ���н������ֻ���ص�һ��ƥ���е����ֵ��
	 */
	public Object getCacheValueByColname(String tableName, List<CacheFilter> filters, String colName){
		Map<String,Object> map = this.getCacheDataRow(tableName, filters);
		if(map.isEmpty()){
			return null;
		}
		else{
			return map.get(colName.toUpperCase());
		}
	}

	/**
	 * <p>������ֵ�������������ֶΣ����ڱ�ʾ���ֵ���������������б���ʾ��key��value�ֶ�</p>
	 * <p>���������Ǹ���ָ�����ֵ�����ƣ����ظñ���������ֶ�</p>
	 * <P></P>
	 * 
	 * @param tableName �ֵ������
	 * @return          ����һ���ַ������飬���а��������ַ���ֵ����һ����key���ֶ����ƣ��ڶ�����value���ֶ�����
	 */
	public String[] getKeyAndValueColName(String tableName){
		String[] ret = new String[2];

		tableName = tableName.toUpperCase();
		
		CacheTable table = (CacheTable)this.codeCachePool.get(tableName);
		if(table == null){
			return ret;
		}

		ret[0] = table.getKeyColName();
		ret[1] = table.getValueColName();
		
		return ret;
	}

	/**
	 * ��ȡ��������Ϣ
	 * 
	 * @param csbm ��������
	 * @return     ����ֵ
	 */
	public String getCacheXtcs(String csbm){
		String csbName = "t_xt_xtcs".toUpperCase();
		List<Map<String,Object>> list = this.getCacheData(csbName);

		String value = "";
		
		for(int i=0; i<list.size(); i++){
			Map<String,Object> row = list.get(i);
			Iterator<String> iter = row.keySet().iterator();
			while(iter.hasNext()){
				String key = (String)iter.next();
				if(csbm.equalsIgnoreCase(key)){
					value = (String)row.get(key);
					return value;
				}
			}
		}
		
		return value;
	}

	//----------------------------------------------------------------------
	
	/**
	 * ����ȫ����������
	 * 
	 */
	public void load() {
		if(!this.useMemoryCache){ //����ģʽ,������
			return;
		}
		
		CacheReqEvent req = new CacheReqEvent("CacheUpdateManagerBLH");
		req.setMethod(DictCacheManager.SERVER_METHOD_LOADALL);
		
		Map<String,CacheTable> data = delegate(req);
		
		this.updateLocalCache(data);
		
		startMonitor();
		
		//�����ֵ��Ԫ����
		loadDictMataInfo(dictMetaFile);
	}

	/**
	 * ���µ�ǰ���������
	 *
	 */
	public void update() {
		if(!this.useMemoryCache){ //����ģʽ,������
			return;
		}

		log.debug("============= ��ʼ���»������� ================");
		CacheReqEvent req = new CacheReqEvent("CacheUpdateManagerBLH");
		req.setMethod(DictCacheManager.SERVER_METHOD_UPDATE);
		
		Iterator<CacheTable> iter = this.codeCachePool.values().iterator();
		Map<String,CacheElement> cacheElements = new HashMap<String,CacheElement>();
		while(iter.hasNext()){
			CacheTable table = (CacheTable)iter.next();
			CacheElement ce = new CacheElement(table.getTableName());
			ce.setVersion(table.getVersion());
			
			cacheElements.put(table.getTableName(), ce);
		}
		req.setCacheElements(cacheElements);
		
		Map<String,CacheTable> data = delegate(req);
		
		this.updateLocalCache(data);
	}
	
	/**
	 * ������ǰ��������,ֱ�Ӵӷ������˵����ݿ�����ȡ����
	 * @param tableName ����
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row]
	 */
	public List<Map<String,Object>> getDataFromServer(String tableName, List<CacheFilter> filters){
		List<Map<String,Object>> ret = null;
		tableName = tableName.toUpperCase();
		
		log.debug("�ӷ������˼��ص������ݣ� tableName=" + tableName + " filters=" + filters);
		
		CacheReqEvent req = new CacheReqEvent("CacheUpdateManagerBLH");
		req.setMethod(DictCacheManager.SERVER_METHOD_LOADONE);
		
		Map<String,CacheElement> cacheElements = new HashMap<String,CacheElement>();
		CacheElement ce = new CacheElement(tableName);
		ce.setFilters(filters);
		cacheElements.put(tableName, ce);

		req.setCacheElements(cacheElements);
		
		Map<String,CacheTable> data = delegate(req);
		
		CacheTable ct = (CacheTable)data.get(tableName);
        ret = ct.getCacheData();
        
        //�ڿ���ģʽ��(��ʹ���ڴ滺��)����ȡ�������Ĵ�������ݺ󣬽��仺�浽�ڴ��У�
        if(!this.useMemoryCache && 
        		(filters == null || filters.size() == 0)){
            Map<String,CacheTable> map = new HashMap<String,CacheTable>();
            map.put(tableName, ct);
            this.updateLocalCache(map);
        }
        
		return ret;
	}
	
	private Map<String,CacheTable> delegate(CacheReqEvent req){
		Map<String,CacheTable> data = new HashMap<String,CacheTable>();
		
		try {
			BaseResponseEvent resp = (BaseResponseEvent) BizDelegate.delegate(req);
			if (resp.isSuccess()) {
				CacheResEvent res = (CacheResEvent) resp;
			    data = res.getCacheTables();
			} 
			else {
			    //�쳣����
				ExceptionInfo ei = resp.getExceptionInfo();
				log.error("���ػ�����������쳣����Ϣ���£�\r\n" + ei.toString());
			}
		} catch (Exception e) {
			log.error("�ֵ�����ʱ�����쳣��",e);
		}
        
        return data;
	}
	
	/**
	 * �������ͬ���߳�,���ڶ�ʱ�ӷ�������ɸ���
	 *
	 */
	public void startMonitor(){
    	Monitor thread = new Monitor();
    	this.monitor = thread;
    	thread.setDaemon(true);
    	thread.start();
	}

	private Map<String,Map<String,Object>> convertList2Map(List<Map<String,Object>> table, String keyFieldName){
		Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
		List<Object> keys = new ArrayList<Object>();
		keyFieldName = keyFieldName.toUpperCase();
		if(keyFieldName.indexOf(",") < 0){
			keys.add(keyFieldName);
		}
		else{
			StringTokenizer st = new StringTokenizer(keyFieldName, ",");
			while(st.hasMoreElements()){
				keys.add(st.nextElement());
			}
		}
		
		for(int i=0; i<table.size(); i++){
			Map<String,Object> row = table.get(i);
			String key = "";
			for(int keyIndex=0; keyIndex<keys.size(); keyIndex++){
				key += row.get(keys.get(keyIndex)) + ",";
			}
			if(key.endsWith(",")){
				key = key.substring(0, key.length()-1);
			}
			map.put(key, row);
		}
		
		return map;

	}

	private void updateLocalCache(Map<String,CacheTable> data){
		//��ʱ���ε����ػ������
//		if(true) return;
		
        synchronized(this.codeCachePool){
        	this.codeCachePool.putAll(data);
        }
        
        if(useLocalXMLCache){ //����Ϊxml�ļ�
            this.xmlCacheManager.updateLocalXMLCache(data);
        }
         
        Iterator<CacheTable> iter = data.values().iterator();
         while(iter.hasNext()){
        	 CacheTable table = (CacheTable)iter.next();
        	 String msg = "���±��ػ��棺 tableName = " + table.getTableName();
        	 msg += " version = " + table.getVersion();
        	 msg += " cacheType= " + (table.getCacheType()==CacheTable.CACHE_TYPE_MEM?"MEM":"DB");
        	 if(table.getCacheType() == CacheTable.CACHE_TYPE_MEM){
        		 msg += " rowCount= " + table.getCacheData().size(); 
        	 }
        	 
        	 log.info(msg);
         }
        
	}
	

	/**
	 * ������ǰ��������,ֱ�Ӵӷ������˵����ݿ�����ȡ����,���ҽ������ݸ��µ�������
	 * @param tableName ����
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row]
	 */
	public List<Map<String,Object>> reload(String tableName){
		List<Map<String,Object>> ret = null;
		tableName = tableName.toUpperCase();
		
		log.debug("�ӷ������˼��ص������ݣ� tableName=" + tableName);
		
		CacheReqEvent req = new CacheReqEvent("CacheUpdateManagerBLH");
		req.setMethod(DictCacheManager.SERVER_METHOD_LOADONE);
		
		Map<String,CacheElement> cacheElements = new HashMap<String,CacheElement>();
		CacheElement ce = new CacheElement(tableName);
		cacheElements.put(tableName, ce);

		req.setCacheElements(cacheElements);
		
		Map<String,CacheTable> data = delegate(req);
		
		CacheTable ct = (CacheTable)data.get(tableName);
        ret = ct.getCacheData();
        
        //��ȡ�������Ĵ�������ݺ󣬽��仺�浽�ڴ��У�
        if(this.useMemoryCache){
            Map<String,CacheTable> map = new HashMap<String,CacheTable>();
            map.put(tableName, ct);
            this.updateLocalCache(map);
        }
        
		return ret;
	}

	/**
	 * ȡ�ֵ���ԭʼ�������ݣ���Ҫ�����ֵ��ĳ־û����ݸ���<
	 */
	public Map<String, DictMetaInfo> getDictMetaInfos(){
		return this.dictMetaInfos;
	}
	
	/**
	 * 
	 * <p>�������ļ��м����ֵ��Ķ�����Ϣ</p>
	 *
	 * @param file �ֵ��Ԫ���ݶ����ļ�
	 */
	private void loadDictMataInfo(String file){
		FileUtils fileUtils = new FileUtils(file);
		InputStream is = fileUtils.getInputStream();

		try {
			if (is == null) {
				throw new BaseCheckedException("00000600");
			}

			Document doc = null;
			SAXBuilder builder = new SAXBuilder();
			try {
				doc = builder.build(is);
			} catch (Exception ex) {
				throw new BaseCheckedException("00000601", ex);
			}
			
			Element root = doc.getRootElement();
			List<Element> elm_tables = root.getChildren("table");
			
			for(Element elm : elm_tables){
				DictMetaInfo table = new DictMetaInfo();
				
				table.setName(elm.getChildText("table-name").trim().toLowerCase());
				table.setDescription(elm.getChildText("table-description"));
				
				List<Properties> fields = new ArrayList<Properties>();
				List<Element> elm_fields = elm.getChild("fields").getChildren("field");
				for(Element elmfield : elm_fields){
					Properties prop = new Properties();
					prop.setProperty("column-name", elmfield.getAttributeValue("column-name"));
					prop.setProperty("display-name", elmfield.getAttributeValue("display-name"));
					prop.setProperty("data-type", elmfield.getAttributeValue("data-type"));
					prop.setProperty("data-length", elmfield.getAttributeValue("data-length"));
					prop.setProperty("is-null", elmfield.getAttributeValue("is-null"));
					prop.setProperty("is-pk", elmfield.getAttributeValue("is-pk"));
					fields.add(prop);
				}
				
				table.setFields(fields);
				
				this.dictMetaInfos.put(table.getName(),table);
			}
		} 
		catch (BaseCheckedException e) {
			log.error("�ֵ�����Ԫ����������Ϣ����ʱ�����쳣", e);
		}
		
	}
	
	public static void main(String[] s){
		ApplicationContext.singleton();
    	ServerCacheManager scm = (ServerCacheManager)ServerCacheManager.getDictCacheService();
    	if(scm.isUseMemoryCache()){
    		ServerCacheManager.getDictCacheManager().load();
		}
	}
	
	/**
	 * 
	 * <P>Title: Monitor</P>
	 * <P>Description: </P>
	 * <P>����˵�����������ͬ���߳�</P>
	 * <P></P>
	 *
	 * <P>Copyright: Copyright (c) 2011 </P>
	 * <P>@author lfw</P>
	 * <P>version 1.0</P>
	 * <P>2010-6-16</P>
	 *
	 */
	class Monitor extends Thread{
		public void run(){
	    	Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
	    	String strUpdateCyc = props.getProperty("update-cyc");

			if(strUpdateCyc==null||strUpdateCyc.equals("")){
                strUpdateCyc = "5";  //Ĭ��5����
           }

			int updateCyc = -1;
			try {
				updateCyc = Integer.parseInt(strUpdateCyc); //��λ�� ����
			} catch (Exception e1) {
				log.error("����������ͬ���̳߳����쳣��", e1);
				return;
			}
			
			while(true){
				try {
					sleep(updateCyc * 60 * 1000);
				} catch (InterruptedException e) {
					log.error("����������ͬ���̳߳����쳣��", e);
				}
				
				update();
			}
		}
	}
		
}
