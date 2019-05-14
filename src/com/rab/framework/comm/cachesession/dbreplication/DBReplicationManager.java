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
 * <P>程序说明：</P>
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
     * 日志记录对象
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
			logger.error("取数据库连接时系统出现异常！", e);
		}
	}
	
	public void saveDataToDB(String sessionid, String key, Serializable data, long lastModified){
		try {
			con.setAutoCommit(false);
			
			DBRMDialect dialect = DBRMDialectFactory.createDialect(con);
			dialect.saveDataToDB(sessionid, key, data, lastModified);
			
			con.commit();
			
			logger.info("保存session变量(session="+sessionid+",  key="+key+")到数据库成功！！！ ");
		} 
		catch (Exception e) {
			logger.error("保存session变量(session="+sessionid+",  key="+key+")到DB时出现异常！", e);
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
			
			logger.info("从数据库中读取session变量(session="+sessionid+",  key="+key+")成功！！！ ");
		} catch (Exception e) {
			logger.error("从数据库中读取session变量(session="+sessionid+",  key="+key+")时出现异常！", e);
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
			logger.error("从DB中删除session变量数据(session="+sessionid+",  key="+key+")时出现异常！", e);
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
			logger.error("从DB中清空session变量数据时出现异常！", e);
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
