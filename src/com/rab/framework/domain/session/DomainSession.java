package com.rab.framework.domain.session;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.security.LogonEnvironment;
import com.rab.framework.comm.security.LogonSubject;
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.server.CoreAppContext;
import com.rab.framework.domain.server.CoreAppServer;


/**
 * 
 * <P>Title: DomainSession</P>
 * <P>Description: </P>
 * <P>程序说明：业务层session管理器</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-14</P>
 *
 */
public interface DomainSession {

    /**
     * 取系统默认的数据访问对象
     * 
     * @return
     */
	public PersistenceDAO getPersistenceDAO() ;

	/**
	 * 按照指定的名字，返回数据访问对象
	 * 
	 * @param daoName 数据源名称
	 * 
	 * @return
	 */
	public PersistenceDAO getPersistenceDAO(String sessionFactoryName) ;

	
	/**
	 * 
	 * <p>取系统通用的默认持久层访问接口，连接的名称是sys-without-tx，本连接不参与全局事务管理</p>
	 *
	 * @return
	 */
	public PersistenceDAO getSystemDaoWithoutTx() ;
	
	/**
	 * 关闭session，释放资源
	 * 
	 * @throws BaseCheckedException
	 */
	public void close()  throws BaseCheckedException ;
	
	/**
	 * <p>返回当前交易所在的Context实例引用</p>
	 *
	 * @return
	 */
	public CoreAppContext getContext() ;

	/**
	 * 
	 * <p>返回当前交易所在的Server实例引用</p>
	 *
	 * @return
	 */
	public CoreAppServer getServer() ;
	
	/**
	 * 
	 * <p>返回当前用户所的VHSessionId</p>
	 *
	 * @return
	 */
	public String getVHSessionId();
	
	/**
	 * 
	 * <p>返回当前用户信息</p>
	 *
	 * @return
	 */
	public User getUser();

	/**
	 * 
	 * <p>返回当前用户授权信息</p>
	 *
	 * @return
	 */
	public LogonSubject getSubject();
	
	/**
	 * 
	 * <p>返回当前用户的环境信息</p>
	 *
	 * @return
	 */
	public LogonEnvironment getLogonEnvironment();
	
}
