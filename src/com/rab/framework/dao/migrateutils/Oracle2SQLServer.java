package com.rab.framework.dao.migrateutils;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class Oracle2SQLServer {

	private Connection conSQLServer;
	
	private Connection conOracle;
	
	public Oracle2SQLServer(){
		init();
	}
	
	public void init(){
		//SQL Server 
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		String url = "jdbc:jtds:sqlserver://127.0.0.1:1433;DatabaseName=test1";
		String user = "sa";
		String pwd = "sa";
		this.conSQLServer = this.getConnection(driver, url, user, pwd);
		
		//Oracle
		driver = "oracle.jdbc.driver.OracleDriver";
		url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
		user = "test1";
		pwd = "test1";

		this.conOracle = this.getConnection(driver, url, user, pwd);

	}
	
	private Connection getConnection(String driver, String url, String user,String pwd){
		Connection con = null;

		try {
			Class.forName(driver); 
			con = java.sql.DriverManager.getConnection(url, user,pwd);
			System.out.println("取数据库连接 con = " + con);
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		
		return con;
	}

	public void migrate(String tablefrom, String tableto){
		
		String fields = "";
		String ss = "";
		
		try {
			
			if(fields.length() ==0){
				String ret[] = this.getFields(tableto);
				fields = ret[0];
				ss = ret[1];
			}

			String insql = "insert into " + tableto + "(";
			insql += fields;
			insql += ") values (" + ss + ")";
			System.out.println("insql = " + insql);
			String sql = "select ";
			sql += fields;
			sql += " from " + tablefrom;
			
			System.out.println(sql);
			
			Statement stmt = this.conOracle.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			String sqlmeta = "select " + fields + " from " + tableto + " where 1=2";
			Statement stmt0 = this.conOracle.createStatement();
			ResultSet rs0 = stmt0.executeQuery(sqlmeta);
			ResultSetMetaData meta = rs0.getMetaData();
			
			PreparedStatement pstmt = this.conSQLServer.prepareStatement(insql);
			while(rs.next()){
				for(int i=1; i<=meta.getColumnCount(); i++){
					String columName = meta.getColumnName(i);
					int type = meta.getColumnType(i);
					if(type == Types.VARCHAR){
						pstmt.setString(i, rs.getString(i));
					}
					else if(type == Types.DATE){
						pstmt.setDate(i, rs.getDate(i));
					}
					else if(type == Types.NUMERIC){
						long data = rs.getLong(i);
						String s = rs.getString(i);
						if(s == null){
							pstmt.setString(i, s);
						}
						else{
							pstmt.setLong(i, data);
						}
					}
					else{
						System.out.println("类型异常！columName = " + columName + "\ttype = " + type);
					}
//					System.out.println(rs.getString(i));
				}

				pstmt.execute();
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[] getFields(String tableto){
		String sql = "select * from " + tableto + " where 1=2";
		System.out.println(sql);
		String fields = "";
		String retstr[] = new String[2];
		
		try {
			Statement stmt0 = this.conOracle.createStatement();
			ResultSet rs0 = stmt0.executeQuery(sql);
			ResultSetMetaData meta = rs0.getMetaData();
			
			String ss = "";
			for(int i=1; i<=meta.getColumnCount(); i++){
				String columName = meta.getColumnName(i);
				fields += columName + ",";
				
				ss += "?,";
			}
			
			if(fields.endsWith(",")){
				fields = fields.substring(0, fields.lastIndexOf(","));
				ss = ss.substring(0, ss.lastIndexOf(","));
			}
			
			retstr[0] = fields;
			retstr[1] = ss;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return retstr;
	}
	
	
	public void getTableList(){
		String file = "F:/svn/doc/4.sql-script/02.init.sql";
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(file));
			
			int count = 0;
			while(lnr.ready()){
				String line = lnr.readLine().trim();
				if(line.startsWith("@")){
					int posB = line.lastIndexOf("\\");
					int posE = line.indexOf(".sql", posB);
					String tablename = line.substring(posB+1, posE);
					
					count ++;
					System.out.println(count + "\t迁移数据表：" + tablename);
					migrate(tablename,tablename);

				}
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void test(){
		String sql = "insert into t_sys_user(USER_ID,USER_CODE,USER_NAME,PASSWORD,USER_DESC,SJ_ID,IS_DBA,EMP_ID,IS_STOP,SCBJ) values (?,?,?,?,?, ?,?,?,?,?)";
//		String sql = "insert into t_sys_user(USER_ID,USER_CODE,USER_NAME,PASSWORD,USER_DESC,SJ_ID,IS_DBA,IS_STOP,SCBJ) values (?,?,?,?,?, ?,?,?,?)";
		try {
			PreparedStatement pstmt = this.conSQLServer.prepareStatement(sql);
			int i=1;
			pstmt.setInt(i++, 1);
			pstmt.setString(i++, "admin");
			pstmt.setString(i++, "系统超级管理员");
			pstmt.setString(i++, "1");
			pstmt.setString(i++, "");
			pstmt.setInt(i++, 0);
			pstmt.setInt(i++, 1);
			pstmt.setString(i++, null);
			pstmt.setInt(i++, 0);
			pstmt.setInt(i++, 0);

			pstmt.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Oracle2SQLServer t = new Oracle2SQLServer();
		t.getTableList();
	}

}
