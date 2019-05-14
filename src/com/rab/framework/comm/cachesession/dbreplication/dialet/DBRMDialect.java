package com.rab.framework.comm.cachesession.dbreplication.dialet;

import java.io.Serializable;
import java.sql.Connection;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

public abstract class DBRMDialect {
    /**
     * 日志记录对象
     */
	protected final static LogWritter logger = LogFactory.getLogger(DBRMDialect.class);
	
	protected String tableName = "t_sys_dbreplication";

	protected Connection con = null;
	
	
	public void setCon(Connection con) {
		this.con = con;
	}

	public abstract void saveDataToDB(String sessionid, String key, Serializable data, long lastModified) throws Exception;
	
	public abstract Object readDataFromDB(String sessionid, String key) throws Exception;
	
	public abstract void deleteDataFromDB(String sessionid, String key) throws Exception;
	
	public abstract void deleteAllDataFromDB(long currentTime) throws Exception;
	
	
}
