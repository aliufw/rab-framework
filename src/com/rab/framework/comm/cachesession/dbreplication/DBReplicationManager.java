package com.rab.framework.comm.cachesession.dbreplication;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import com.rab.framework.comm.cachesession.dbreplication.dialet.DBRMDialect;
import com.rab.framework.comm.cachesession.dbreplication.dialet.DBRMDialectFactory;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;



/**
 * 
 * <P>Title: DBReplicationManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class DBReplicationManager {
    /**
     * ��־��¼����
     */
	private final static LogWritter logger = LogFactory.getLogger(DBReplicationManager.class);

//	private String tableName = "t_xt_dbreplication";
	
	private Connection con = null;
	
	private DBReplicationManager(){
		init();
	}
	
	public static DBReplicationManager getInstance(){
		return new DBReplicationManager();
	}
	
	private void init(){
		try {
//			ConfManager.load();
			con = DBConnectionManager.singleton().getConnection();
		} catch (Exception e) {
			logger.error("ȡ���ݿ�����ʱϵͳ�����쳣��", e);
		}
	}
	
	public void saveDataToDB(String sessionid, String key, Serializable data, long lastModified){
		try {
			con.setAutoCommit(false);
			
			DBRMDialect dialect = DBRMDialectFactory.createDialect(con);
			dialect.saveDataToDB(sessionid, key, data, lastModified);
			
			con.commit();
			
			logger.info("����session����(session="+sessionid+",  key="+key+")�����ݿ�ɹ������� ");
		} 
		catch (Exception e) {
			logger.error("����session����(session="+sessionid+",  key="+key+")��DBʱ�����쳣��", e);
		}
		finally{
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
	}
	
	public Object readDataFromDB(String sessionid, String key){
		Object ret = null;
		try {
			DBRMDialect dialect = DBRMDialectFactory.createDialect(con);
			ret = dialect.readDataFromDB(sessionid, key);
			
			logger.info("�����ݿ��ж�ȡsession����(session="+sessionid+",  key="+key+")�ɹ������� ");
		} catch (Exception e) {
			logger.error("�����ݿ��ж�ȡsession����(session="+sessionid+",  key="+key+")ʱ�����쳣��", e);
		}
		finally{
			try {
				con.close();
			} catch (SQLException e) {
			}
		}

		return ret;
	}
	
	public void deleteDataFromDB(String sessionid, String key){
		try {
			con.setAutoCommit(false);
			DBRMDialect dialect = DBRMDialectFactory.createDialect(con);
			dialect.deleteDataFromDB(sessionid, key);
			
			con.commit();
		} catch (Exception e) {
			logger.error("��DB��ɾ��session��������(session="+sessionid+",  key="+key+")ʱ�����쳣��", e);
		}
		finally{
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
	}

	public void deleteAllDataFromDB(long currentTime){
		try {
			con.setAutoCommit(false);
			DBRMDialect dialect = DBRMDialectFactory.createDialect(con);
			dialect.deleteAllDataFromDB(currentTime);
			
			con.commit();
		} catch (Exception e) {
			logger.error("��DB�����session��������ʱ�����쳣��", e);
		}
		finally{
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
	}

	public void test(){
//		String sessionid = "11196150493200$8261";
//		String key       = "11196150493200$8261";
//		Serializable data = new String("Qwertrety wertw werqw");
//		long lastModified = System.currentTimeMillis();
		
		
//		saveDataToDB(sessionid, key, data, lastModified);
//		Object obj = this.readDataFromDB(sessionid, key);
//		System.out.println(obj);
//		this.deleteAllDataFromDB(Calendar.getInstance().getTimeInMillis());
	}
	
	public static void main(String[] args) {
		DBReplicationManager d = new DBReplicationManager();
		d.test();
	}

}
