package com.rab.framework.comm.cachesession.dbreplication;

import java.io.Serializable;

/**
 * 
 * <P>Title: DBReplicationInfo</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class DBReplicationInfo {

    /**
     * 数据描述关键词
     */
    private String key;
    
    /**
     * 数据
     */
    private Serializable data;
    
    /**
     * 最后一次数据修改时间
     */
    private long lastModified;

    
    
	public Serializable getData() {
		return data;
	}

	public void setData(Serializable data) {
		this.data = data;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

}
