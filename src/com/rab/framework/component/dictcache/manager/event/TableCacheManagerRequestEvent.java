package com.rab.framework.component.dictcache.manager.event;

import java.util.List;
import java.util.Map;

import com.rab.framework.comm.dto.event.SysRequestEvent;

public class TableCacheManagerRequestEvent extends SysRequestEvent {

	private static final long serialVersionUID = 974056201632406743L;

	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 更新的数据行，字段和值信息
	 */
	private List<Map<String, String>> datarows;
	
	public TableCacheManagerRequestEvent(String transactionID) {
		super(transactionID);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Map<String, String>> getDatarows() {
		return datarows;
	}

	public void setDatarows(List<Map<String, String>> datarows) {
		this.datarows = datarows;
	}



}
