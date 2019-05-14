package com.rab.framework.component.dictcache;

import java.util.List;
import java.util.Map;

import com.rab.framework.component.dictcache.manager.DictMetaInfo;

/**
 * 
 * <P>Title: CacheManager</P>
 * <P>Description: </P>
 * <P>程序说明：缓存管理接口</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public interface DictCacheManager {
	/**
	 * 缓存加载命令标记: 加载全部缓存数据
	 */
	public final static String SERVER_METHOD_LOADALL = "loadall";
	
	/**
	 * 缓存加载命令标记: 加载指定名称的缓存数据
	 */
	public final static String SERVER_METHOD_LOADONE = "loadone";
	
	/**
	 * 缓存加载命令标记: 根据设定的sql语句,加载指定名称的缓存数据
	 */
	public final static String SERVER_METHOD_LOADWITHSQL = "loadwithsql";
	
	/**
	 * 缓存加载命令标记: 更新当前缓存的数据
	 */
	public final static String SERVER_METHOD_UPDATE = "update";
	
	/**
	 * 加载全部缓存数据
	 * 
	 */
	public void load();

	/**
	 * 更新当前缓存的数据
	 *
	 */
	public void update();
	
	/**
	 * 跳过当前缓存数据,直接从服务器端的数据库中提取数据
	 * @param tableName 表名
	 * @param filters   过滤条件, 封装在CacheFilter对象实例中
	 * @return 返回数据列表,封装方式为: List[map], 相当于表[row]
	 */
	public List<Map<String,Object>> getDataFromServer(String tableName, List<CacheFilter> filters);
	
	/**
	 * 启动监控同步线程,用于定时从服务器完成更新
	 *
	 */
	public void startMonitor();
	
	
	/**
	 * 
	 * <p>取字典表的原始定义数据，主要用于字典表的持久化数据更新</p>
	 *
	 * @return
	 */
	public Map<String, DictMetaInfo> getDictMetaInfos();
}
