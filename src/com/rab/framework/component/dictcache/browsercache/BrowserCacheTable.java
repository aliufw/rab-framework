package com.rab.framework.component.dictcache.browsercache;


/**
 * 
 * <P>Title: BrowserCacheTable</P>
 * <P>Description: </P>
 * <P>����˵����������Ϣ��������, �����������ڴ��л�������ݱ�</P>
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
	 * �汾��
	 */
	private int version;

	/**
	 * ���ݱ���
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
