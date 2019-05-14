package com.rab.framework.comm.cachesession.dbreplication.dialet;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBRMOracleDialetImpl extends DBRMDialect{
	public void saveDataToDB(String sessionid, String key, Serializable data, long lastModified) throws Exception{
		logger.info("����session����(session="+sessionid+",  valuekey="+key+")�����ݿ�... ");
		String sql = "insert into " + tableName + " (sessionid,valuekey,data,lastModified) ";
		sql += "values('"+ sessionid +"', '"+ key +"',EMPTY_BLOB(),'"+ lastModified +"')";
		
		logger.debug("sql = " + sql);
		
		Statement stmt = con.createStatement();
		stmt.executeUpdate(sql);
		
		sql = "select data from " + tableName + " where sessionid='"+sessionid+"' and valuekey='"+key+"' for update";
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()){
			Blob myBlob = rs.getBlob("data");
			OutputStream out = myBlob.setBinaryStream(0);
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(data);
			oos.flush();
			oos.close();
		}
	}
	
	public Object readDataFromDB(String sessionid, String key) throws Exception{
		logger.info("�����ݿ��ж�ȡsession����(session="+sessionid+",  valuekey="+key+")... ");
		String sql = "select data from " + tableName + " where sessionid='" + sessionid + "' and valuekey='" + key + "'";
		logger.debug("sql = " + sql);
		Object ret = null;
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		if(rs.next()){
			Blob myBlob = rs.getBlob("data");
			InputStream in = myBlob.getBinaryStream();
			ObjectInputStream ois = new ObjectInputStream(in);
			ret = ois.readObject();
		}

		return ret;
	}
	
	public void deleteDataFromDB(String sessionid, String key) throws Exception{
		logger.info("�����ݿ���ɾ��session����(session="+sessionid+",  key="+key+")... ");
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
