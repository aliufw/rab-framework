package com.rab.framework.comm.cachesession.dbreplication.dialet;

import java.sql.Connection;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

public class DBRMDialectFactory {
	/**
	 * 日志记录器
	 */
	private final static LogWritter logger = LogFactory.getLogger(DBRMDialectFactory.class);
	
	public static DBRMDialect createDialect(Connection con) throws Exception{
		String classname = null;
		
		try {
			classname = getDialectClassName(con);
			DBRMDialect dia=(DBRMDialect) Class.forName(classname).newInstance();
			dia.setCon(con);
			
            return dia;
		} 
		catch (Exception e) {
			logger.error("数据集群复制：在创建数据库方言管理器Dialect实例(" + classname + ")时出现异常！",e);
			throw e;
		} 
	}
	
	
	private static String getDialectClassName(Connection con) throws Exception {
		
		String dialectFactoryClsPath = DBRMDialectFactory.class.getName();
		String dialectPackage = dialectFactoryClsPath.substring(0,dialectFactoryClsPath.lastIndexOf("."));
		String dialectClassName = "";
		
		try {
			String dbType = con.getMetaData().getDatabaseProductName();
			
			if(dbType == null){
				logger.info("无法自动获取数据库类型, 改从配置文件bootstrap.xml中查找！");
				dbType = (String) ApplicationContext.singleton().getValueByKey("sys-db-type");
				if(dbType == null){
					logger.error("00000430： 在做查询时，无法获得有效的数据库类型，请检查配置文件bootstrap.xml中的配置项sys-db-type！");
					throw new BaseCheckedException("00000430");
				}
			}
			
			if("oracle".equalsIgnoreCase(dbType)){
				dialectClassName = dialectPackage + ".DBRMOracleDialetImpl";
			}
			else if("microsoft sql server".equalsIgnoreCase(dbType)){
				dialectClassName = dialectPackage + ".DBRMSqlServerDialetImpl";
			}
			
		} catch (Exception e) {
			throw e;
		}
		
		return dialectClassName;
	}	
}
