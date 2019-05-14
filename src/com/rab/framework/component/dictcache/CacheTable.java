package com.rab.framework.component.dictcache;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 
 * <P>Title: CacheTable</P>
 * <P>Description: </P>
 * <P>程序说明：缓存信息描述对象, 用于描述在内存中缓存的数据表</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 * 
 */
public class CacheTable implements Serializable {
	/**
	 * 序列化标记
	 */
	private static final long serialVersionUID = 6659014201529959199L;

    /**
	 * 缓存类型: 数据表在内存中缓存
	 */
	public final static int CACHE_TYPE_MEM = 0;

	/**
	 * 缓存类型: 数据表直接查询数据库获得 
	 */
	public final static int CACHE_TYPE_DB = 1;

	/**
	 * 数据表名
	 */
	private String tableName;

	/**
	 * 字典表数据行数
	 */
	private int size;

	/**
	 * 缓存数据, 数据存储方式为: List[map] ,对应为表[row]关系
	 */
	private List<Map<String,Object>> cacheData = null;

	/**
	 * 版本号

	 */
	private int version;

	/**
	 * 最后访问时间

	 */
	private Calendar lastAccessTime;

	/**
	 * 缓存类型
	 */
	private int cacheType;

	/**
	 * 字典表以列表模式显示时用作key的字段名称
	 */
	private String keyColName;
	
	/**
	 * 字典表以列表模式显示时用作value的字段名称
	 */
	private String valueColName;
	
	/**
	 * 排序用字段名称
	 */
	private String orderByCol;
	
	/**
	 * 排序顺序标记，0-顺序 ，1-倒序
	 */
	private String descFlag;
		
	public List<Map<String,Object>> getCacheData() {
		return cacheData;
	}

	public void setCacheData(List<Map<String,Object>> cacheData) {
		this.cacheData = cacheData;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public java.util.Calendar getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(java.util.Calendar lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getCacheType() {
		return cacheType;
	}

	public void setCacheType(int cacheType) {
		this.cacheType = cacheType;
	}
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

	public String getKeyColName() {
		return keyColName;
	}

	public void setKeyColName(String keyColName) {
        if(keyColName != null){
        	keyColName = keyColName.toUpperCase();
        }

		this.keyColName = keyColName;
	}

	public String getValueColName() {
		return valueColName;
	}

	public void setValueColName(String valueColName) {
        if(valueColName != null){
        	valueColName = valueColName.toUpperCase();
        }

		this.valueColName = valueColName;
	}

	public String getOrderByCol() {
		return orderByCol;
	}

	public void setOrderByCol(String orderByCol) {
        if(orderByCol != null){
        	orderByCol = orderByCol.toUpperCase();
        }
        this.orderByCol = orderByCol;
	}

	public String getDescFlag() {
		return descFlag;
	}

	public void setDescFlag(String descFlag) {
		this.descFlag = descFlag;
	}


}