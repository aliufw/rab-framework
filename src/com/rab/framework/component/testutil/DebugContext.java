package com.rab.framework.component.testutil;

import java.util.Map;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.domain.domainconfig.DomainConfig;
import com.rab.framework.domain.domainconfig.TransactionCfg;
import com.rab.framework.domain.server.TransactionManager;
import com.rab.framework.domain.server.CoreAppContext;
import com.rab.framework.domain.server.CoreAppServer;
import com.rab.framework.domain.session.DomainSession;
import com.rab.framework.domain.session.DomainSessionImpl;

/**
 * 
 * <P>Title: DebugContext</P>
 * <P>Description: </P>
 * <P>程序说明：测试用运行时上下文环境</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class DebugContext {

	public DomainSession createDebugDomainSession(String transactionid) throws Exception{
		
		ApplicationContext.singleton();
		CoreAppServer server = CoreAppServer.getInstance();
		CoreAppContext context = server.getContext(transactionid);

		TransactionManager transcationManager = creatorTranscationManager(transactionid,context);
		
		DomainSessionImpl domainSession = new DomainSessionImpl();
		domainSession.setTranscationManager(transcationManager);

		return domainSession;
	}
	
    private TransactionManager creatorTranscationManager (String transactionid, CoreAppContext context){
    	DomainConfig domainConfig = context.getModelConfig().getDomainConfig();
    	Map<String,TransactionCfg> transations = domainConfig.getTransactions();
    	TransactionCfg tCfg = transations.get(transactionid);
    	
    	TransactionManager tm = new TransactionManager();
    	tm.setTransactionCfg(tCfg);
    	tm.setContext(context);
	
		//初始化
    	tm.initialize();
    	
    	return tm;
    }

}
