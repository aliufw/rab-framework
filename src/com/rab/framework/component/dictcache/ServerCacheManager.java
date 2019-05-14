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
 * <P>程序说明：</P>
 * 缓存管理程序
 * 
 * 实现了两个接口: 
 *    CacheUserInterface - 缓存用户接口,主要用于从缓存中提取数据
 *    CacheManager       - 缓存管理接口,主要用于缓存的管理监控
 * 
 * 本程序是缓存管理的核心控制逻辑,需要用到的系统配置参数包括:
 * use-memory-cache - true or false , 定义缓存管理模式
 * update-cyc       - 整型变量,表示缓存更新周期,单位为分钟
 * catalog-table    - 字符串变量, 表示目录表名称

 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class ServerCacheManager implements DictCacheService, DictCacheManager{
    /**
     * 日志记录对象
     */
	protected static final LogWritter log = LogFactory.getLogger(ServerCacheManager.class);

    /**
     * 存放代码缓存的属性变量,其中存放的是 CacheTable对象集合
     */
    private Map<String,CacheTable> codeCachePool = new HashMap<String,CacheTable>();

    /**
     * CacheManager 的静态变量,经过类的初始化后成为全局的一个实例.
     */
    private static ServerCacheManager instance = null;

    /**
     * 是否使用内存缓存,主要对应于开发模式和运行模式
     * 
     * 开发模式: useMemoryCache=false, 内存中不缓存任何数据,任何数据访问都直接从数据库中提取,以简化启动加载过程
     * 运行模式: useMemoryCache=true,  将数据缓存在内存中, 提高系统运行时性能
     */
    private boolean useMemoryCache = true;
    
    /**
     * 是否将代码表以XML格式缓存到本地硬盘
     */
    private boolean useLocalXMLCache = false;
    
//    /**
//     * 将代码表缓存到本地硬盘时的存放路径
//     */
//    private String localXMLCacheDir = "";
    
    /**
     * 将代码表缓存到本地硬盘的管理程序
     */
    private XMLCacheManager xmlCacheManager = null;
    
    /**
     * 监控线程,用于实时从服务器端保持数据一致性更新
     */
    private Thread monitor = null; 
    
    private String dictMetaFile = "dictmeta.xml";
    /**
     * 缓存字典表的元数据描述
     */
    Map<String, DictMetaInfo> dictMetaInfos = new HashMap<String, DictMetaInfo>();
    
    /**
     * 私有构造器
     *
     */
    private ServerCacheManager(){
    	init();
    }
    
    /**
     *初始化,包括:
     *	加载缓存数据
     *	启动同步监控线程
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
     * 返回用户接口
     * @return CacheUserInterface
     */
    public static DictCacheService getDictCacheService() {
        if (instance == null) {
        	instance = new ServerCacheManager();
        }
        return instance;
    }

    /**
     * 返回管理接口
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
	 * 根据表名称,返回整表数据，数据返回形态为List，其中value为数据行
	 * 
	 * @param tableName 表名称
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row]。 如果没有找到相关数据，则返回空的list列表。
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
	 * 根据表名称,返回整表数据,数据的返回形态为Map，其中key为指定字段的值，value为对应的数据行
	 * 
	 * @param tableName  表名称
	 * @param keyFieldName 主键名称列表，多个主键用 "," 分隔
	 * @return  返回数据列表,封装方式为: Map[map], 相当于表[row]
	 */
	public Map<String,Map<String,Object>> getCacheDataMap(String tableName, String keyFieldName){
		List<Map<String,Object>> list = this.getCacheData(tableName);
		
		Map<String,Map<String,Object>> map = convertList2Map(list, keyFieldName);
		
		return map;
	}
	
	
	/**
	 * <P>根据表名称和过滤条件,从缓存中取缓存表数据</P>
	 *  <P>过滤条件包括：</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>注意：</P>
	 * <P>1. CacheFilter列表中，多个CacheFilter之间是“与”的关系</P>
	 * <p>2. 当运算符号位“=”或“<>”时，CacheFilter.fieldValue为单个值</p>
	 * <p>3. 当运算符号位“in”或“not in”时，CacheFilter.fieldValue为List<Objectd></p>
	 * 
	 * @param tableName 表名
	 * @param filters   过滤条件, 封装在CacheFilter对象实例中
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row]， 如果没有找到相关数据，则返回空的list列表。
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
				 * 增加可通过汉字的拼音首字母进行匹配
				 * start
				 */
//				else if(filterOperator.equals(CacheFilter.FILTER_OPERATOR_LIKE)){
//					Object data = map.get(fieldName.toUpperCase());
//					String sValue = "" + data;       //字典表字段值
//					String sModel = "" + fieldValue; //模式匹配字串
//					if(sValue.indexOf(sModel) >= 0){
//						filterList.add(map);
//					}
//				}
				else if(filterOperator.equals(CacheFilter.FILTER_OPERATOR_LIKE)){
					Object data = map.get(fieldName.toUpperCase());
					String sValue = "" + data;       //字典表字段值
					String sModel = "" + fieldValue; //模式匹配字串
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
	 * <P>根据表名称、起始数据行序号、数据行数和过滤条件,从缓存中取缓存表数据，数据返回形态为List，其中value为数据行</P>
	 *  <P>过滤条件包括：</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>注意：</P>
	 * <P>1. CacheFilter列表中，多个CacheFilter之间是“与”的关系</P>
	 * <p>2. 当运算符号位“=”或“<>”时，CacheFilter.fieldValue为单个值</p>
	 * <p>3. 当运算符号位“in”或“not in”时，CacheFilter.fieldValue为List<Objectd></p>
	 * 
	 * @param tableName 表名
	 * @param start     起始数据行序号，从“0”开始计数
	 * @param offset    需要返回的数据行数
	 * @param filters   过滤条件, 封装在CacheFilter对象实例中
	 * @return          返回数据列表,封装方式为: List[map], 相当于表[row],其中外层map的主键为传入field参数对应的值
	 */
	public List<Map<String,Object>> getCacheData(String tableName, int start, int offset, List<CacheFilter> filters){
		List<Map<String,Object>> retList = new ArrayList<Map<String,Object>>();
		
		//如果输入参数不合逻辑，则返回空的数据集
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
	 * <P>根据表名称和过滤条件,从缓存中取缓存表数据.数据的返回形态为Map，其中key为指定字段的值，value为对应的数据行</P>
	 *  <P>过滤条件包括：</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>注意：</P>
	 * <P>1. CacheFilter列表中，多个CacheFilter之间是“与”的关系</P>
	 * <p>2. 当运算符号位“=”或“<>”时，CacheFilter.fieldValue为单个值</p>
	 * <p>3. 当运算符号位“in”或“not in”时，CacheFilter.fieldValue为List<Objectd></p>
	 * 
	 * @param tableName 表名
	 * @param filters  过滤条件, 封装在CacheFilter对象实例中
	 * @param keyFieldName 主键名称列表，多个主键用 "," 分隔
	 * @return  返回数据列表,封装方式为: Map[map], 相当于表[row],其中外层map的主键为传入field参数对应的值
	 */
	public Map<String,Map<String,Object>> getCacheDataMap(String tableName, List<CacheFilter> filters, String keyFieldName){
		List<Map<String,Object>> list = this.getCacheData(tableName,filters);
		
		Map<String,Map<String,Object>> map = convertList2Map(list, keyFieldName);
		
		return map;
	}
	
	/**
	 * <P>根据表名称和过滤条件,从缓存中取缓存表数据，数据返回形态为List，其中value为数据行</P>
	 * <P>本方法用于非内存缓存的字典表的数据提取，一般用于数据量很大、不适合做内存缓存的字典表</P>
	 * <P></P>
	 * 
	 * @param tableName 表名
	 * @param filterSQL   where条件sql字符串,包含where关键字
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row]
	 */
	public List<Map<String,Object>> getCacheDataFromDB(String tableName, String filterSQL){
		List<Map<String,Object>> ret = null;
		log.debug("从服务器端加载单表数据： tableName=" + tableName + " filterSQL=" + filterSQL);
		
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
	 * <P>根据表名称和过滤条件,从缓存中提取一行数据。如果有多条匹配行结果，则只返回第一个！</P>
	 * <P>过滤条件包括：</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>注意：</P>
	 * <P>1. CacheFilter列表中，多个CacheFilter之间是“与”的关系</P>
	 * <p>2. 当运算符号位“=”或“<>”时，CacheFilter.fieldValue为单个值</p>
	 * <p>3. 当运算符号位“in”或“not in”时，CacheFilter.fieldValue为List<Objectd></p>
	 * 
	 * @param tableName 表名
	 * @param filters   过滤条件, 封装在CacheFilter对象实例中
	 * @return 返回数据行,封装方式为: map,相当于表[row]。 
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
	 * <P>根据表名称和过滤条件,从缓存中取缓存表数据。</P>
	 * <P>过滤条件包括：</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>注意：</P>
	 * <P>1. CacheFilter列表中，多个CacheFilter之间是“与”的关系</P>
	 * <p>2. 当运算符号位“=”或“<>”时，CacheFilter.fieldValue为单个值</p>
	 * <p>3. 当运算符号位“in”或“not in”时，CacheFilter.fieldValue为List<Objectd></p>
	 * 
	 * <P>返回数据规则如下：</P>
	 * <P>1. 只返回指定字段的一个值</P>
	 * <P>2. 如果结果集行数大于1，则只返回首行数据中指定字段的一个值</P>
	 * <P></P>
	 * 
	 * @param tableName 表名
	 * @param filters   过滤条件, 封装在CacheFilter对象实例中
	 * @param colName   字段名
	 * @return 返回字段名key对应的字段值, 如果有多条匹配行结果，则只返回第一个匹配行的相关值！
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
	 * <p>缓存的字典表都有两个特殊字段，用于表示该字典表数据在以下拉列表显示的key和value字段</p>
	 * <p>本函数就是根据指定的字典表名称，返回该表的这两个字段</p>
	 * <P></P>
	 * 
	 * @param tableName 字典表名称
	 * @return          返回一个字符串数组，其中包含两个字符串值：第一个是key的字段名称，第二个是value的字段名称
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
	 * 获取参数表信息
	 * 
	 * @param csbm 参数编码
	 * @return     参数值
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
	 * 加载全部缓存数据
	 * 
	 */
	public void load() {
		if(!this.useMemoryCache){ //开发模式,不加载
			return;
		}
		
		CacheReqEvent req = new CacheReqEvent("CacheUpdateManagerBLH");
		req.setMethod(DictCacheManager.SERVER_METHOD_LOADALL);
		
		Map<String,CacheTable> data = delegate(req);
		
		this.updateLocalCache(data);
		
		startMonitor();
		
		//加载字典表元数据
		loadDictMataInfo(dictMetaFile);
	}

	/**
	 * 更新当前缓存的数据
	 *
	 */
	public void update() {
		if(!this.useMemoryCache){ //开发模式,不加载
			return;
		}

		log.debug("============= 开始更新缓存代码表 ================");
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
	 * 跳过当前缓存数据,直接从服务器端的数据库中提取数据
	 * @param tableName 表名
	 * @param filters   过滤条件, 封装在CacheFilter对象实例中
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row]
	 */
	public List<Map<String,Object>> getDataFromServer(String tableName, List<CacheFilter> filters){
		List<Map<String,Object>> ret = null;
		tableName = tableName.toUpperCase();
		
		log.debug("从服务器端加载单表数据： tableName=" + tableName + " filters=" + filters);
		
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
        
        //在开发模式下(不使用内存缓存)，当取到完整的代码表数据后，将其缓存到内存中！
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
			    //异常处理
				ExceptionInfo ei = resp.getExceptionInfo();
				log.error("加载缓存代码表出现异常，信息如下：\r\n" + ei.toString());
			}
		} catch (Exception e) {
			log.error("字典表更新时发生异常！",e);
		}
        
        return data;
	}
	
	/**
	 * 启动监控同步线程,用于定时从服务器完成更新
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
		//暂时屏蔽掉本地缓存更新
//		if(true) return;
		
        synchronized(this.codeCachePool){
        	this.codeCachePool.putAll(data);
        }
        
        if(useLocalXMLCache){ //保存为xml文件
            this.xmlCacheManager.updateLocalXMLCache(data);
        }
         
        Iterator<CacheTable> iter = data.values().iterator();
         while(iter.hasNext()){
        	 CacheTable table = (CacheTable)iter.next();
        	 String msg = "更新本地缓存： tableName = " + table.getTableName();
        	 msg += " version = " + table.getVersion();
        	 msg += " cacheType= " + (table.getCacheType()==CacheTable.CACHE_TYPE_MEM?"MEM":"DB");
        	 if(table.getCacheType() == CacheTable.CACHE_TYPE_MEM){
        		 msg += " rowCount= " + table.getCacheData().size(); 
        	 }
        	 
        	 log.info(msg);
         }
        
	}
	

	/**
	 * 跳过当前缓存数据,直接从服务器端的数据库中提取数据,并且将此数据更新到缓存中
	 * @param tableName 表名
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row]
	 */
	public List<Map<String,Object>> reload(String tableName){
		List<Map<String,Object>> ret = null;
		tableName = tableName.toUpperCase();
		
		log.debug("从服务器端加载单表数据： tableName=" + tableName);
		
		CacheReqEvent req = new CacheReqEvent("CacheUpdateManagerBLH");
		req.setMethod(DictCacheManager.SERVER_METHOD_LOADONE);
		
		Map<String,CacheElement> cacheElements = new HashMap<String,CacheElement>();
		CacheElement ce = new CacheElement(tableName);
		cacheElements.put(tableName, ce);

		req.setCacheElements(cacheElements);
		
		Map<String,CacheTable> data = delegate(req);
		
		CacheTable ct = (CacheTable)data.get(tableName);
        ret = ct.getCacheData();
        
        //当取到完整的代码表数据后，将其缓存到内存中！
        if(this.useMemoryCache){
            Map<String,CacheTable> map = new HashMap<String,CacheTable>();
            map.put(tableName, ct);
            this.updateLocalCache(map);
        }
        
		return ret;
	}

	/**
	 * 取字典表的原始定义数据，主要用于字典表的持久化数据更新<
	 */
	public Map<String, DictMetaInfo> getDictMetaInfos(){
		return this.dictMetaInfos;
	}
	
	/**
	 * 
	 * <p>从配置文件中加载字典表的定义信息</p>
	 *
	 * @param file 字典表元数据定义文件
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
			log.error("字典表管理元数据配置信息加载时出现异常", e);
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
	 * <P>程序说明：缓存更新同步线程</P>
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
                strUpdateCyc = "5";  //默认5分钟
           }

			int updateCyc = -1;
			try {
				updateCyc = Integer.parseInt(strUpdateCyc); //单位： 分钟
			} catch (Exception e1) {
				log.error("代码表缓存管理同步线程出现异常！", e1);
				return;
			}
			
			while(true){
				try {
					sleep(updateCyc * 60 * 1000);
				} catch (InterruptedException e) {
					log.error("代码表缓存管理同步线程出现异常！", e);
				}
				
				update();
			}
		}
	}
		
}
