package com.rab.framework.comm.cachesession.dbreplication.dialet;

import java.sql.Connection;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

public class DBRMDialectFactory {
	/**
	 * ��־��¼��
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
			logger.error("���ݼ�Ⱥ���ƣ��ڴ������ݿⷽ�Թ�����Dialectʵ��(" + classname + ")ʱ�����쳣��",e);
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
				logger.info("�޷��Զ���ȡ���ݿ�����, �Ĵ������ļ�bootstrap.xml�в��ң�");
				dbType = (String) ApplicationContext.singleton().getValueByKey("sys-db-type");
				if(dbType == null){
					logger.error("00000430�� ������ѯʱ���޷������Ч�����ݿ����ͣ����������ļ�bootstrap.xml�е�������sys-db-type��");
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
