package com.rab.framework.component.dictcache;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * <P>Title: CacheElement</P>
 * <P>Description: </P>
 * <P>����˵�����������Ϣ��������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class CacheElement implements Serializable{
    /**
	 * ���кű��
	 */
	private static final long serialVersionUID = 6138319878981451566L;
	
	/**
     * ��������
     */
    private String tableName = "";
    /**
     * �����汾��
     */
    private int version;
	
    /**
     * ��������, ��װ��CacheFilter����ʵ����
     */
	private List<CacheFilter> filters;
	
    /**
     * ������
     * @param tableName ��������
     */
    public CacheElement(String tableName){
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

	public List<CacheFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<CacheFilter> filters) {
		this.filters = filters;
	}
    
    
}