package com.rab.framework.component.dictcache;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 
 * <P>Title: CacheTable</P>
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
public class CacheTable implements Serializable {
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 6659014201529959199L;

    /**
	 * ��������: ���ݱ����ڴ��л���
	 */
	public final static int CACHE_TYPE_MEM = 0;

	/**
	 * ��������: ���ݱ�ֱ�Ӳ�ѯ���ݿ��� 
	 */
	public final static int CACHE_TYPE_DB = 1;

	/**
	 * ���ݱ���
	 */
	private String tableName;

	/**
	 * �ֵ����������
	 */
	private int size;

	/**
	 * ��������, ���ݴ洢��ʽΪ: List[map] ,��ӦΪ��[row]��ϵ
	 */
	private List<Map<String,Object>> cacheData = null;

	/**
	 * �汾��

	 */
	private int version;

	/**
	 * ������ʱ��

	 */
	private Calendar lastAccessTime;

	/**
	 * ��������
	 */
	private int cacheType;

	/**
	 * �ֵ�����б�ģʽ��ʾʱ����key���ֶ�����
	 */
	private String keyColName;
	
	/**
	 * �ֵ�����б�ģʽ��ʾʱ����value���ֶ�����
	 */
	private String valueColName;
	
	/**
	 * �������ֶ�����
	 */
	private String orderByCol;
	
	/**
	 * ����˳���ǣ�0-˳�� ��1-����
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