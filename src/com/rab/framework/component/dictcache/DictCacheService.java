package com.rab.framework.component.dictcache;

import java.util.List;
import java.util.Map;

/**
 * 
 * <P>Title: CacheUserInterface</P>
 * <P>Description: </P>
 * <P>程序说明：缓存应用用户接口</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public interface DictCacheService {
	/**
	 * 根据表名称,返回整表数据，数据返回形态为List，其中value为数据行
	 * 
	 * @param tableName 表名称
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row]
	 */
	public List<Map<String,Object>> getCacheData(String tableName);
	
	/**
	 * 根据表名称,返回整表数据,数据的返回形态为Map，其中key为指定字段的值，value为对应的数据行
	 * 
	 * @param tableName  表名称
	 * @param keyFieldName 主键名称列表，多个主键用 "," 分隔
	 * @return  返回数据列表,封装方式为: Map[map], 相当于表[row]
	 */
	public Map<String,Map<String,Object>> getCacheDataMap(String tableName, String keyFieldName);

	/**
	 * <P>根据表名称和过滤条件,从缓存中取缓存表数据，数据返回形态为List，其中value为数据行</P>
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
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row],其中外层map的主键为传入field参数对应的值
	 */
	public List<Map<String,Object>> getCacheData(String tableName, List<CacheFilter> filters);

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
	 * @param start     起始数据行序号
	 * @param offset    需要返回的数据行数
	 * @param filters   过滤条件, 封装在CacheFilter对象实例中
	 * @return          返回数据列表,封装方式为: List[map], 相当于表[row],其中外层map的主键为传入field参数对应的值
	 */
	public List<Map<String,Object>> getCacheData(String tableName, int start, int offset, List<CacheFilter> filters);

	
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
	public Map<String,Map<String,Object>> getCacheDataMap(String tableName, List<CacheFilter> filters, String keyFieldName);

	/**
	 * <P>根据表名称和过滤条件,从缓存中取缓存表数据，数据返回形态为List，其中value为数据行</P>
	 * <P>本方法用于非内存缓存的字典表的数据提取，一般用于数据量很大、不适合做内存缓存的字典表</P>
	 * <P></P>
	 * @param tableName 表名
	 * @param filterSQL   where条件sql字符串,包含where关键字
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row]
	 */
	public List<Map<String,Object>> getCacheDataFromDB(String tableName, String filterSQL);

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
	public Map<String,Object> getCacheDataRow(String tableName, List<CacheFilter> filters);

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
	public Object getCacheValueByColname(String tableName, List<CacheFilter> filters, String colName);

	/**
	 * <p>缓存的字典表都有两个特殊字段，用于表示该字典表数据在以下拉列表显示的key和value字段</p>
	 * <p>本函数就是根据指定的字典表名称，返回该表的这两个字段</p>
	 * <P></P>
	 * 
	 * @param tableName 字典表名称
	 * @return          返回一个字符串数组，其中包含两个字符串值：第一个是key的字段名称，第二个是value的字段名称
	 */
	public String[] getKeyAndValueColName(String tableName);
	
	/**
	 * 获取参数表信息
	 * 
	 * @param csbm 参数编码
	 * @return     参数值
	 */
	public String getCacheXtcs(String csbm);
	
}
