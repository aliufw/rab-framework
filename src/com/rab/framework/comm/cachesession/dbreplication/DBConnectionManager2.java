package com.rab.framework.comm.cachesession.dbreplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.rab.framework.comm.appcontext.ApplicationContext;


/**
 * 
 * <P>Title: DBConnectionManager</P>
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
public class DBConnectionManager2 {
	private static String CONNECTION_TYPE_JDBC = "jdbc";
	private static String CONNECTION_TYPE_JNDI = "jndi";
	

	
	//=========  基于连接池JNDI的数据源管理属性=========================
	/**
	 * 数据源名称
	 */
	private String dsname = null;
	
	/**
	 * 数据源连接描述：Context.INITIAL_CONTEXT_FACTORY
	 */
	private String factory = null;

	/**
	 * 数据源连接描述：Context.PROVIDER_URL
	 */
	private String providerurl = null;
	
	/**
	 * 数据源对象实例
	 */
	private DataSource ds = null;
	
	
	//=========  直连数据库的JDBC数据源管理属性========================
	/**
	 * JDBC驱动程序
	 */
	private String driver = null;
	
	/**
	 * 数据库连接URL
	 */
	private String url = null;
	
	/**
	 * 用户名
	 */
	private String username=null;
	
	/**
	 * 用户口令
	 */
	private String password=null;
	
	//=========  其他属性=========================================
	/**
	 * 数据源类型， 直连数据库的JDBC或基于连接池的JNDI
	 */
	private String connectionType;

	/**
	 * 单例实例对象
	 */
	private static DBConnectionManager2 instance = new DBConnectionManager2();
	
	/**
	 * 私有构造器
	 *
	 */
	private DBConnectionManager2(){
		init();
	}
	
	/**
	 * 单例接口
	 * @return
	 */
	public static DBConnectionManager2 singleton(){
		return instance;
	}
	
	/**
	 * 初始化环境变量
	 *  <cluster state="on"
	 *  	jdbc-url="jdbc:oracle:thin:@127.0.0.1:1521:orcl"
	 *		jdbc-driver="oracle.jdbc.driver.OracleDriver"
	 *		jdbc-username="test1"
	 *		jdbc-password="test1">
	 *
	 *  <cluster state="on"
	 *  	jndi-dsname="ds_oracle"
	 *		jndi-factory="weblogic.jndi.WLInitialContextFactory"
	 *		jndi-url="t3://127.0.0.1:8001">
	 *
	 *
	 */
	private void init(){
		Properties props = (Properties)ApplicationContext.singleton().getValueByKey("cluster");
		dsname      = props.getProperty("jndi-dsname");
		factory     = props.getProperty("jndi-factory");
		providerurl = props.getProperty("jndi-url");
		
		url      = props.getProperty("jdbc-url");
		driver   = props.getProperty("jdbc-driver");
		username = props.getProperty("jdbc-username");
		password = props.getProperty("jdbc-password");
		
		if(dsname != null){
			this.connectionType = CONNECTION_TYPE_JNDI;
		}
		else{
			this.connectionType = CONNECTION_TYPE_JDBC;
		}
	}
	
	/**
	 * 取数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
    public Connection getConnection() throws Exception {
    	Connection con = null;
    	
    	if(this.connectionType == CONNECTION_TYPE_JNDI){
    		con = this.getConnByJNDI();
    	}
    	else if(this.connectionType == CONNECTION_TYPE_JDBC){
    		con = this.getConnByJDBC();
    	}
    	
    	return con;
    }
	
    private Connection getConnByJDBC() throws Exception {
		Class.forName(driver);
		Connection con = DriverManager.getConnection(url, username, password);

		return con;
    }
    
    private Connection getConnByJNDI() throws Exception {
    	Connection con = null;
    	if(ds != null){
    		try {
				con = ds.getConnection();
			} catch (Exception e) {
				DataSource ds = (DataSource) lookup(this.dsname);
	    		con = ds.getConnection();
			}
    	}
    	else{
    		DataSource ds = (DataSource) lookup(this.dsname);
    		con = ds.getConnection();
    	}
    	
    	return con;
    }
    
    private Object lookup(String name) throws NamingException {
    	Properties prop = new Properties() ;
        prop.put(Context.INITIAL_CONTEXT_FACTORY, this.factory ) ;
        prop.put(Context.PROVIDER_URL, this.providerurl);
    	
        Context ctx = new InitialContext(prop) ;
        Object obj = ctx.lookup(name);
        return obj;
    }
}
