package com.rab.framework.component.dictcache;

import java.util.Map;

import com.rab.framework.comm.dto.event.BaseResponseEvent;

/**
 * 
 * <P>Title: CacheResEvent</P>
 * <P>Description: </P>
 * <P>����˵����Զ�̷������ݷ�װ����</P>
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
	 * ���кű��
	 */
	private static final long serialVersionUID = 2528388185611768721L;
	
	/**
     * ��ŵ�������һϵ�еĴ�������CacheTable
     */
    private Map<String,CacheTable> cacheTables;

	public Map<String, CacheTable> getCacheTables() {
		return cacheTables;
	}

	public void setCacheTables(Map<String, CacheTable> cacheTables) {
		this.cacheTables = cacheTables;
	}


}
