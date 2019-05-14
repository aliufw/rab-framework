package com.rab.framework.comm.cachesession.dbreplication;

import java.sql.Connection;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceDAOFactory;
import com.rab.framework.dao.PersistenceManager;
import com.rab.framework.domain.server.CoreAppServer;


/**
 * 
 * <P>Title: DBConnectionManager</P>
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
public class DBConnectionManager {
	/**
	 * ��־��¼����
	 */
	private final LogWritter logger = LogFactory.getLogger(this.getClass());
	
	/**
	 * ����ʵ������
	 */
	private static DBConnectionManager instance = new DBConnectionManager();
	
	private String SYS_DAO_WITHOUT_TX_FACTORY_NAME = "sys-without-tx";

	/**
	 * ˽�й�����
	 *
	 */
	private DBConnectionManager(){
	}
	
	/**
	 * �����ӿ�
	 * @return
	 */
	public static DBConnectionManager singleton(){
		return instance;
	}
	
	/**
	 * ȡ���ݿ�����
	 * 
	 * @return
	 * @throws Exception
	 */
    public Connection getConnection() throws Exception {
		PersistenceManager pm = CoreAppServer.getInstance().getPersistenceManager(SYS_DAO_WITHOUT_TX_FACTORY_NAME);
		
		if(pm == null){
			logger.error("00000428: û����������Դsys-without-tx�����������ļ�persistence.xml");
			return null;
		}

		PersistenceDAO sysDao = PersistenceDAOFactory.createPersistenceDAO(pm);
			

    	return sysDao.getConnection();
    }
	
}
