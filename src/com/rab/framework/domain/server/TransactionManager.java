package com.rab.framework.domain.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.cachesession.CacheSession;
import com.rab.framework.comm.cachesession.CacheSessionManagerImpl;
import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.Ticket;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceDAOImpl;
import com.rab.framework.dao.PersistenceManager;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.framework.domain.domainconfig.DomainConfig;
import com.rab.framework.domain.domainconfig.TransactionCfg;
import com.rab.framework.domain.session.DomainSessionImpl;

/**
 * 
 * <P>Title: TranscationManager</P>
 * <P>Description: </P>
 * <P>����˵����ҵ���ҵ���߼������׹�����</P>
 * <P>ҵ���ҵ���߼����״����ִ���ߣ�������ҵ���߼�ʵ�������л��������ݣ�
 * �������ҵ���߼���ִ�д���</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-30</P>
 *
 */
public class TransactionManager {

	private static final LogWritter logger = LogFactory.getLogger(TransactionManager.class);

	/**
	 * ��ģ���Ĭ������Դ
	 */
	private String defaultDataSourceName;

	/**
	 * ���ν��׵Ľ�����������
	 */
	private TransactionCfg transactionCfg;
	
	/**
	 * ��ģ�������漰���ĳ־û����ʹ�����󼯺�
	 */
	private Map<String,PersistenceManager> ctxPersistenceManagers = new HashMap<String,PersistenceManager>();
	
	/**
	 * Context�ص�
	 */
	private CoreAppContext context;
	
	/**
	 * ���ν���������ʹ�õ�DomainSession����
	 */
	private DomainSessionImpl domainSession;

	public void setTransactionCfg(TransactionCfg transactionCfg) {
		this.transactionCfg = transactionCfg;
	}

	public void setContext(CoreAppContext context) {
		this.context = context;
	}

	public String getDefaultDataSourceName() {
		return defaultDataSourceName;
	}

	public Map<String, PersistenceManager> getCtxPersistenceManagers() {
		return ctxPersistenceManagers;
	}

	/**
	 * @return ���ض�Context�Ļص�����
	 */
	public CoreAppContext getContext() {
		return context;
	}

	/**
	 * ִ�н��״���
	 * 
	 * @param reqEvent ����������󣬰����ͻ��˷��͵��������������
	 * @return  ���ش���������װ��DTO���ض�����
	 * @throws BaseCheckedException �������쳣ʱ�����쳣��װ��VHBaseCheckedException���׳�
	 */
	public final BaseResponseEvent doTranscation(BaseRequestEvent reqEvent) throws BaseCheckedException {
		BaseResponseEvent resp = null;
		String blhName = this.transactionCfg.getClassName();
		try {
			String sessionid = reqEvent.getSessionID();
			if (sessionid != null) {
				CacheSession cs = CacheSessionManagerImpl.singleton().getCacheSession(sessionid);
				Ticket ticket = (Ticket) cs.getValue("ticket");
				domainSession.setTicket(ticket);
			}
			
			String ejb_model = (String)ApplicationContext.singleton().getValueByKey("ejb-model");
			if ("true".equalsIgnoreCase(ejb_model)) { 
//			if ("true".equalsIgnoreCase(ejb_model) && false) {  //������JBPMͬJTS�ļ����������⣬��ʱ�������ܸ�ΪBean����ģʽ
				//ʹ����EJB����ʹ��EJB������������
				
				resp = doBLH(reqEvent);
				
			}
			else{ 
				//û��ʹ��EJB�����ɿ���Լ���������
				PersistenceDAO dao = this.domainSession.getPersistenceDAO();
				((PersistenceDAOImpl)dao).beginTransaction();
				
				try {
					resp = doBLH(reqEvent);
					((PersistenceDAOImpl)dao).commitTransaction();

				} 
				catch (Exception e) {
					((PersistenceDAOImpl)dao).rollbackTransaction();
					if (e instanceof BaseCheckedException) {
						throw e;
					} 
					else if(e instanceof InvocationTargetException){
						Throwable rootCause = e.getCause();
						if(rootCause instanceof BaseCheckedException){
							throw (BaseCheckedException)rootCause;
						}
						else{
							logger.error("00000302=��BLH(" + blhName + ")ִ�й����з����쳣", e);
							List<String> params = new ArrayList<String>();
							params.add(blhName);
							throw new BaseCheckedException("00000302",params, e);
						}
					}
					else {
						logger.error("00000302=��BLH(" + blhName + ")ִ�й����з����쳣", e);
						List<String> params = new ArrayList<String>();
						params.add(blhName);
						throw new BaseCheckedException("00000302",params, e);
					}
				}
			}
			
			logger.info("ִ��ҵ���߼�������blhName = " + blhName+"   methodName = " + reqEvent.getMethod());

		} 
		catch (BaseCheckedException e) {
			throw e;
		}
		catch (Exception e) {
			List<String> params = new ArrayList<String>();
			params.add(blhName);
			throw new BaseCheckedException("00000303",params, e);
		} 
		finally {
			domainSession.close();
		}
		return resp;
	}

	private BaseResponseEvent doBLH(BaseRequestEvent reqEvent) throws Exception {
		String blhName = this.transactionCfg.getClassName();
		logger.info("��ʼ����ҵ���߼�ʵ����blhName = " + blhName);
		ClassLoader classLoader = this.getClass().getClassLoader();
		BaseDomainBLH blh = (BaseDomainBLH) classLoader.loadClass(blhName).newInstance();
		blh.setDomainSession(domainSession);
		
		String methodName = reqEvent.getMethod();
		logger.info("��ʼִ��ҵ���߼���blhName = " + blhName+"   methodName = "+methodName);
		
		Method method = null;
		if(reqEvent instanceof DataRequestEvent){
			try {
				method = blh.getClass().getMethod(methodName, DataRequestEvent.class);
			} catch (Exception e) {
				method = blh.getClass().getMethod(methodName, BaseRequestEvent.class);
			}
		}
		else{
			method = blh.getClass().getMethod(methodName, BaseRequestEvent.class);
		}
		
		return (BaseResponseEvent)method.invoke(blh, reqEvent);
	}
	
	/**
	 * ��ʼ�����׿�����
	 */
	public void initialize() {
		DomainConfig domainConfig = this.context.getModelConfig().getDomainConfig();
		this.defaultDataSourceName = domainConfig.getDefaultDataSourceName();
		this.ctxPersistenceManagers = this.context.getCtxPersistenceManagers();
		
		domainSession = new DomainSessionImpl();
		domainSession.setTranscationManager(this);

	}

}
