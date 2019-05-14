package com.rab.framework.dao.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: DialectFactory</P>
 * <P>Description: </P>
 * <P>程序说明：方言管理器工厂</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-28</P>
 *
 */
public class DialectFactory {

	/**
	 * 日志记录器
	 */
	private final static LogWritter logger = LogFactory.getLogger(DialectFactory.class);
	
	/**
	 * 
	 * <p>创建方言管理器</p>
	 *
	 * @param dao
	 * @return
	 * @throws BaseCheckedException
	 */
	public static Dialect createDialect(PersistenceDAO dao) throws BaseCheckedException{
		String classname = null;
		try {
			classname = getDialectClassName(dao.getConnection());
            Dialect dia=(Dialect) Class.forName(classname).newInstance();
            dia.setDao(dao);

            return dia;
		} 
		catch (BaseCheckedException e) {
			throw e;
		} 
		catch (Exception e) {
			logger.error("00000432：在创建数据库方言管理器Dialect实例(" + classname + ")时出现异常！");
			List<String> params = new ArrayList<String>();
			params.add(classname);
			throw new BaseCheckedException("00000432", params, e);
			
		} 
	}


	private static String getDialectClassName(Connection con) throws BaseCheckedException {
		
		String dialectFactoryClsPath = DialectFactory.class.getName();
		String dialectPackage = dialectFactoryClsPath.substring(0,dialectFactoryClsPath.lastIndexOf("."));
		String dialectClassName = "";
		
		try {
			String dbType = con.getMetaData().getDatabaseProductName();
			logger.debug("dbType = " + dbType);
			if(dbType == null){
				logger.info("无法自动获取数据库类型, 改从配置文件bootstrap.xml中查找！");
				dbType = (String) ApplicationContext.singleton().getValueByKey("sys-db-type");
				if(dbType == null){
					logger.error("00000430： 在做查询时，无法获得有效的数据库类型，请检查配置文件bootstrap.xml中的配置项sys-db-type！");
					throw new BaseCheckedException("00000430");
				}
			}
			
			if("oracle".equalsIgnoreCase(dbType)){
				dialectClassName = dialectPackage + ".OracleDialectImpl";
			}
			else if("mysql".equalsIgnoreCase(dbType)){
				dialectClassName = dialectPackage + ".MySQLDialectImpl";
			}
			else if("microsoft sql server".equalsIgnoreCase(dbType)){
				dialectClassName = dialectPackage + ".SqlServerDialectImpl";
			}
			
		} catch (SQLException e) {
			logger.error("00000431：在获取数据库类型时出现异常！");
			throw new BaseCheckedException("00000431");
		}
		
		return dialectClassName;
	}
}
