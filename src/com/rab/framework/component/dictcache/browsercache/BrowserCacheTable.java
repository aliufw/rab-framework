package com.rab.framework.component.dictcache.browsercache;


/**
 * 
 * <P>Title: BrowserCacheTable</P>
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
public class BrowserCacheTable{
	
	/**
	 * 版本号
	 */
	private int version;

	/**
	 * 数据表名
	 */
	private String tableName;
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	
}
