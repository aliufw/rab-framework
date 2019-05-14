package com.rab.framework.component.dictcache;

import java.util.Map;

import com.rab.framework.comm.dto.event.BaseResponseEvent;

/**
 * 
 * <P>Title: CacheResEvent</P>
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
public class CacheResEvent extends BaseResponseEvent {
    /**
	 * 序列号标记
	 */
	private static final long serialVersionUID = 2528388185611768721L;
	
	/**
     * 存放的内容是一系列的代码表对象：CacheTable
     */
    private Map<String,CacheTable> cacheTables;

	public Map<String, CacheTable> getCacheTables() {
		return cacheTables;
	}

	public void setCacheTables(Map<String, CacheTable> cacheTables) {
		this.cacheTables = cacheTables;
	}


}
