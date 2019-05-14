package com.rab.framework.domain.session;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.LogonEnvironment;
import com.rab.framework.comm.security.LogonSubject;
import com.rab.framework.comm.security.Ticket;
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceDAOFactory;
import com.rab.framework.dao.PersistenceDAOImpl;
import com.rab.framework.dao.PersistenceManager;
import com.rab.framework.domain.server.TransactionManager;
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
public class DomainSessionImpl implements DomainSession{
	/**
	 * ��־��¼����
	 */
	private final LogWritter logger = LogFactory.getLogger(this.getClass());

	private static String SYS_DAO_WITHOUT_TX_FACTORY_NAME = "sys-without-tx";
	
	/**
	 * ��ȫƱ�ݴ��
	 */
	private Ticket ticket = null;
	
	/**
	 * DomainController�ص�����
	 */
	TransactionManager transcationManager = null;
	
    /**
     * �Ѿ����������ݷ������Ӽ��ϣ����������ظ�ʹ�ã��Ա������һ�ν����д���������ݷ������Ӷ���
     */
    private Map<String, PersistenceDAOImpl> daoCache = new HashMap<String, PersistenceDAOImpl>();
    
    /**
     * ȡϵͳĬ�ϵ����ݷ��ʶ���
     * 
     * @return
     */
	public PersistenceDAO getPersistenceDAO() {
		return getPersistenceDAO(transcationManager.getDefaultDataSourceName());
	}

	/**
	 * ����ָ�������֣��������ݷ��ʶ���
	 * 
	 * @param daoName ����Դ����
	 * 
	 * @return
	 */
	public PersistenceDAO getPersistenceDAO(String sessionFactoryName) {
		if(!daoCache.containsKey(sessionFactoryName)){
			PersistenceManager pm = transcationManager.getCtxPersistenceManagers().get(sessionFactoryName);
			
			PersistenceDAO dao = PersistenceDAOFactory.createPersistenceDAO(pm);
			((PersistenceDAOImpl)dao).setDomainSession(this);

			this.daoCache.put(sessionFactoryName, (PersistenceDAOImpl)dao);
		}
		
		return daoCache.get(sessionFactoryName);
	}

	/**
	 * 
	 * <p>ȡϵͳͨ�õ�Ĭ�ϳ־ò���ʽӿڣ����ӵ�������sys-without-tx�������Ӳ�����ȫ���������</p>
	 *
	 * @return
	 * 
	 */
	public PersistenceDAO getSystemDaoWithoutTx()  {
		if(!daoCache.containsKey(SYS_DAO_WITHOUT_TX_FACTORY_NAME)){
			PersistenceManager pm = this.getServer().getPersistenceManager(SYS_DAO_WITHOUT_TX_FACTORY_NAME);
			
			if(pm == null){
//				throw new VHBaseCheckedException("00000428");
				logger.error("00000428: û����������Դsys-without-tx�����������ļ�persistence.xml");
				return null;
			}

			PersistenceDAO sysDao = PersistenceDAOFactory.createPersistenceDAO(pm);
			((PersistenceDAOImpl)sysDao).setDomainSession(this);
			
			this.daoCache.put(SYS_DAO_WITHOUT_TX_FACTORY_NAME, (PersistenceDAOImpl)sysDao);
		}
		
		return daoCache.get(SYS_DAO_WITHOUT_TX_FACTORY_NAME);
	}

	/**
	 * �ر�session���ͷ���Դ
	 * 
	 * @throws BaseCheckedException
	 */
	public void close() throws BaseCheckedException{
		if(this.daoCache == null){
			return;
		}
		
		Iterator<PersistenceDAOImpl> iter = daoCache.values().iterator();
		while(iter.hasNext()){
			try {
				iter.next().close();
			} catch (Exception e) {
				//�����׳�����Ϊ����Ҫ���ݸ��쳣�����������Դ���
				logger.error("00000304: ���ݷ��ʶ���DAO���ر��쳣",e);
			}
		}
	}
	
	
	public void setTranscationManager(TransactionManager transcationManager) {
		this.transcationManager = transcationManager;
	}

	public TransactionManager getDomainController() {
		return transcationManager;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public String getDefaultDataSourceName() {
		return transcationManager.getDefaultDataSourceName();
	}

	public Map<String, PersistenceManager> getPersistenceManagers() {
		return transcationManager.getCtxPersistenceManagers();
	}

	public CoreAppContext getContext() {
		return this.transcationManager.getContext();
	}

	public CoreAppServer getServer() {
		return this.transcationManager.getContext().getServer();
	}
	
	public String getVHSessionId(){
		Ticket ticket = getTicket();
		String vhSessionId = null;
		if(ticket != null){
			vhSessionId = ticket.getUserSessionid();
		}
		
		return vhSessionId;
	}
	
	public User getUser(){
		Ticket ticket = getTicket();
		User user = null;
		if(ticket != null){
			user = ticket.getUser();
		}
		return user;
	}

	public LogonSubject getSubject(){
		Ticket ticket = getTicket();
		LogonSubject subject = null;
		if(ticket != null){
			subject = ticket.getSubject();
		}
		return subject;
	}
	
	public LogonEnvironment getLogonEnvironment(){
		Ticket ticket = getTicket();
		LogonEnvironment env = null;
		if(ticket != null){
			env = ticket.getLogonEnv();
			if(env == null){
				env = new LogonEnvironment();
				ticket.setLogonEnv(env);
			}
		}
		
		return env;
	}
}
