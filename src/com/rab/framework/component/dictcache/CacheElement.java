package com.rab.framework.component.dictcache;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * <P>Title: CacheElement</P>
 * <P>Description: </P>
 * <P>程序说明：代码表信息表述对象</P>
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
	 * 序列号标记
	 */
	private static final long serialVersionUID = 6138319878981451566L;
	
	/**
     * 代码表表名
     */
    private String tableName = "";
    /**
     * 代码表版本号
     */
    private int version;
	
    /**
     * 过滤条件, 封装在CacheFilter对象实例中
     */
	private List<CacheFilter> filters;
	
    /**
     * 构造器
     * @param tableName 代码表表名
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