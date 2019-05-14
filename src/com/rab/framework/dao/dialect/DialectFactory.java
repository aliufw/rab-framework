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
 * <P>����˵�������Թ���������</P>
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
	 * ��־��¼��
	 */
	private final static LogWritter logger = LogFactory.getLogger(DialectFactory.class);
	
	/**
	 * 
	 * <p>�������Թ�����</p>
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
			logger.error("00000432���ڴ������ݿⷽ�Թ�����Dialectʵ��(" + classname + ")ʱ�����쳣��");
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
				logger.info("�޷��Զ���ȡ���ݿ�����, �Ĵ������ļ�bootstrap.xml�в��ң�");
				dbType = (String) ApplicationContext.singleton().getValueByKey("sys-db-type");
				if(dbType == null){
					logger.error("00000430�� ������ѯʱ���޷������Ч�����ݿ����ͣ����������ļ�bootstrap.xml�е�������sys-db-type��");
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
			logger.error("00000431���ڻ�ȡ���ݿ�����ʱ�����쳣��");
			throw new BaseCheckedException("00000431");
		}
		
		return dialectClassName;
	}
}
