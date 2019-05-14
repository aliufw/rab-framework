package com.rab.framework.component.dictcache;

import java.util.HashMap;
import java.util.Map;

import com.rab.framework.comm.dto.event.SysRequestEvent;

/**
 * 
 * <P>Title: CacheReqEvent</P>
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
public class CacheReqEvent extends SysRequestEvent {
	/**
	 * ���кű��
	 */
	private static final long serialVersionUID = -638264192024780710L;

	/**
	 * ��ŵ���CacheElementԪ�ص�ʵ��������б�Ĺ����Ǹ���ʵ����Ҫ�������ġ�
	 */
	private Map<String,CacheElement> cacheElements = new HashMap<String,CacheElement>();

	private String tableName = "";
	private String sqlWhere = "";
	
//	/**
//	 * �ǲ��ú��ַ�ʽ���������ݣ�
//	 * 	CacheManager.SERVER_METHOD_LOADALL ֱ�Ӽ���ȫ������
//	 *  CacheManager.SERVER_METHOD_LOADONE ����ָ�����Ƶ�����
//	 *	CacheManager.SERVER_METHOD_UPDATE  �ж����ݵİ汾�Ƿ��Ѿ��仯���б仯�Ÿ���
//	 */
//	private String loadMethod = "";

	/**
	 * ������
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

