package com.rab.framework.comm.cachesession.dbreplication.dialet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBRMSqlServerDialetImpl extends DBRMDialect {
	public void saveDataToDB(String sessionid, String key, Serializable data, long lastModified) throws Exception{
		logger.info("保存session变量(session="+sessionid+",  key="+key+")到数据库... ");
		String sql = "insert into " + tableName + " (sessionid,valuekey,data, lastModified) ";
		sql += "values(?,?,?,?)";
		
		logger.debug("sql = " + sql);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(data);
		byte[] buff = baos.toByteArray();
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, sessionid);
		stmt.setString(2, key);
		stmt.setBytes(3, buff);
		stmt.setLong(4, System.currentTimeMillis());
		stmt.execute();
		
	}
	
	public Object readDataFromDB(String sessionid, String key) throws Exception{
		logger.info("从数据库中读取session变量(session="+sessionid+",  key="+key+")... ");
		String sql = "select data from " + tableName + " where sessionid='" + sessionid + "' and valuekey='" + key + "'";
		logger.debug("sql = " + sql);
		Object ret = null;
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		if(rs.next()){
			InputStream in = rs.getBinaryStream("data");
			ObjectInputStream ois = new ObjectInputStream(in);
			ret = ois.readObject();
			
		}

		return ret;

	}
	
	public void deleteDataFromDB(String sessionid, String key) throws Exception{
		logger.info("从数据库中删除session变量(session="+sessionid+",  key="+key+")... ");
		String sql = "delete from " + tableName + " where sessionid='" + sessionid + "' and valuekey='" + key + "'";
		logger.debug("sql = " + sql);
		
		Statement stmt = con.createStatement();
		stmt.execute(sql);
	}

	public void deleteAllDataFromDB(long currentTime) throws Exception{
		String sql = "delete from " + tableName + " where lastModified<" + currentTime ;
		logger.debug("sql = " + sql);

		Statement stmt = con.createStatement();
		stmt.execute(sql);
	}
}
