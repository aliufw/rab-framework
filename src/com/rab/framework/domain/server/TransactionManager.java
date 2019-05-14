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
 * <P>程序说明：业务层业务逻辑处理交易管理器</P>
 * <P>业务层业务逻辑交易处理的执行者，包含了业务逻辑实例、运行环境等内容，
 * 负责完成业务逻辑的执行处理</P>
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
	 * 本模块的默认数据源
	 */
	private String defaultDataSourceName;

	/**
	 * 本次交易的交易描述对象
	 */
	private TransactionCfg transactionCfg;
	
	/**
	 * 本模块中所涉及到的持久化访问管理对象集合
	 */
	private Map<String,PersistenceManager> ctxPersistenceManagers = new HashMap<String,PersistenceManager>();
	
	/**
	 * Context回调
	 */
	private CoreAppContext context;
	
	/**
	 * 本次交互过程所使用的DomainSession对象
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
	 * @return 返回对Context的回调引用
	 */
	public CoreAppContext getContext() {
		return context;
	}

	/**
	 * 执行交易处理
	 * 
	 * @param reqEvent 访问请求对象，包含客户端发送的请求操作及数据
	 * @return  返回处理结果，封装在DTO返回对象中
	 * @throws BaseCheckedException 当出现异常时，将异常封装在VHBaseCheckedException中抛出
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
//			if ("true".equalsIgnoreCase(ejb_model) && false) {  //因工作流JBPM同JTS的兼容性有问题，暂时将事务框架改为Bean管理模式
				//使用了EJB，将使用EJB容器管理事务
				
				resp = doBLH(reqEvent);
				
			}
			else{ 
				//没有使用EJB，将由框架自己管理事务
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
							logger.error("00000302=在BLH(" + blhName + ")执行过程中发生异常", e);
							List<String> params = new ArrayList<String>();
							params.add(blhName);
							throw new BaseCheckedException("00000302",params, e);
						}
					}
					else {
						logger.error("00000302=在BLH(" + blhName + ")执行过程中发生异常", e);
						List<String> params = new ArrayList<String>();
						params.add(blhName);
						throw new BaseCheckedException("00000302",params, e);
					}
				}
			}
			
			logger.info("执行业务逻辑结束：blhName = " + blhName+"   methodName = " + reqEvent.getMethod());

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
		logger.info("开始创建业务逻辑实例：blhName = " + blhName);
		ClassLoader classLoader = this.getClass().getClassLoader();
		BaseDomainBLH blh = (BaseDomainBLH) classLoader.loadClass(blhName).newInstance();
		blh.setDomainSession(domainSession);
		
		String methodName = reqEvent.getMethod();
		logger.info("开始执行业务逻辑：blhName = " + blhName+"   methodName = "+methodName);
		
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
	 * 初始化交易控制器
	 */
	public void initialize() {
		DomainConfig domainConfig = this.context.getModelConfig().getDomainConfig();
		this.defaultDataSourceName = domainConfig.getDefaultDataSourceName();
		this.ctxPersistenceManagers = this.context.getCtxPersistenceManagers();
		
		domainSession = new DomainSessionImpl();
		domainSession.setTranscationManager(this);

	}

}
