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
 * <P>程序说明：</P>
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
	 * 日志记录对象
	 */
	private final LogWritter logger = LogFactory.getLogger(this.getClass());
	
	/**
	 * 单例实例对象
	 */
	private static DBConnectionManager instance = new DBConnectionManager();
	
	private String SYS_DAO_WITHOUT_TX_FACTORY_NAME = "sys-without-tx";

	/**
	 * 私有构造器
	 *
	 */
	private DBConnectionManager(){
	}
	
	/**
	 * 单例接口
	 * @return
	 */
	public static DBConnectionManager singleton(){
		return instance;
	}
	
	/**
	 * 取数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
    public Connection getConnection() throws Exception {
		PersistenceManager pm = CoreAppServer.getInstance().getPersistenceManager(SYS_DAO_WITHOUT_TX_FACTORY_NAME);
		
		if(pm == null){
			logger.error("00000428: 没有配置数据源sys-without-tx，请检查配置文件persistence.xml");
			return null;
		}

		PersistenceDAO sysDao = PersistenceDAOFactory.createPersistenceDAO(pm);
			

    	return sysDao.getConnection();
    }
	
}
