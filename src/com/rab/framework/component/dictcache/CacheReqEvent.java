package com.rab.framework.component.dictcache;

import java.util.HashMap;
import java.util.Map;

import com.rab.framework.comm.dto.event.SysRequestEvent;

/**
 * 
 * <P>Title: CacheReqEvent</P>
 * <P>Description: </P>
 * <P>程序说明：远程访问数据封装对象</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class CacheReqEvent extends SysRequestEvent {
	/**
	 * 序列号标记
	 */
	private static final long serialVersionUID = -638264192024780710L;

	/**
	 * 存放的是CacheElement元素的实例，这个列表的构建是根据实际需要来构建的。
	 */
	private Map<String,CacheElement> cacheElements = new HashMap<String,CacheElement>();

	private String tableName = "";
	private String sqlWhere = "";
	
//	/**
//	 * 是采用何种方式来加载数据：
//	 * 	CacheManager.SERVER_METHOD_LOADALL 直接加载全部数据
//	 *  CacheManager.SERVER_METHOD_LOADONE 加载指定名称的数据
//	 *	CacheManager.SERVER_METHOD_UPDATE  判断数据的版本是否已经变化，有变化才更新
//	 */
//	private String loadMethod = "";

	/**
	 * 构造器
	 * @param transactionID 
	 * @param sessionID
	 */
	public CacheReqEvent(String transactionID) {
		super(transactionID);
	}

	public Map<String,CacheElement> getCacheElements() {
		return cacheElements;
	}

	public void setCacheElements(Map<String,CacheElement> cacheElements) {
		this.cacheElements = cacheElements;
	}

//	public String getLoadMethod() {
//		return loadMethod;
//	}
//
//	public void setLoadMethod(String loadMethod) {
//		this.loadMethod = loadMethod;
//	}

	public String getSqlWhere() {
		return sqlWhere;
	}

	public void setSqlWhere(String sqlWhere) {
		this.sqlWhere = sqlWhere;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
	
}

