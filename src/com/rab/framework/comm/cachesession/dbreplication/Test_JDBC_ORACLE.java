package com.rab.framework.comm.cachesession.dbreplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 
 * <P>Title: Test_JDBC_ORACLE</P>
 * <P>Description: </P>
 * <P>³ÌÐòËµÃ÷£º</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class Test_JDBC_ORACLE {

	public Connection getConnection() {
		Connection con = null;
		try {
			String url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
			String driver = "oracle.jdbc.OracleDriver";
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, "lfw", "lfw");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}
	
	public void close(Connection con){
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void test(){
		for(int i=0; i<10; i++){
			Connection con = this.getConnection();
			System.out.println(con);
			String sql ="select * from dual";
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
				while(rs.next()){
					String s = rs.getString(1);
					System.out.println(s);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally{
				
				if(rs != null){
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(stmt != null){
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				this.close(con);
			}
		}
	}
	
	public static void main(String[] args) {
//		Test_JDBC_ORACLE tmj = new Test_JDBC_ORACLE();
//		tmj.test();
		
	}

}
