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
 * <P>����˵����ҵ���session������</P>
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
     * ȡϵͳĬ�ϵ����ݷ��ʶ���
     * 
     * @return
     */
	public PersistenceDAO getPersistenceDAO() ;

	/**
	 * ����ָ�������֣��������ݷ��ʶ���
	 * 
	 * @param daoName ����Դ����
	 * 
	 * @return
	 */
	public PersistenceDAO getPersistenceDAO(String sessionFactoryName) ;

	
	/**
	 * 
	 * <p>ȡϵͳͨ�õ�Ĭ�ϳ־ò���ʽӿڣ����ӵ�������sys-without-tx�������Ӳ�����ȫ���������</p>
	 *
	 * @return
	 */
	public PersistenceDAO getSystemDaoWithoutTx() ;
	
	/**
	 * �ر�session���ͷ���Դ
	 * 
	 * @throws BaseCheckedException
	 */
	public void close()  throws BaseCheckedException ;
	
	/**
	 * <p>���ص�ǰ�������ڵ�Contextʵ������</p>
	 *
	 * @return
	 */
	public CoreAppContext getContext() ;

	/**
	 * 
	 * <p>���ص�ǰ�������ڵ�Serverʵ������</p>
	 *
	 * @return
	 */
	public CoreAppServer getServer() ;
	
	/**
	 * 
	 * <p>���ص�ǰ�û�����VHSessionId</p>
	 *
	 * @return
	 */
	public String getVHSessionId();
	
	/**
	 * 
	 * <p>���ص�ǰ�û���Ϣ</p>
	 *
	 * @return
	 */
	public User getUser();

	/**
	 * 
	 * <p>���ص�ǰ�û���Ȩ��Ϣ</p>
	 *
	 * @return
	 */
	public LogonSubject getSubject();
	
	/**
	 * 
	 * <p>���ص�ǰ�û��Ļ�����Ϣ</p>
	 *
	 * @return
	 */
	public LogonEnvironment getLogonEnvironment();
	
}
