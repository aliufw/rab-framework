package com.rab.framework.domain.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.dao.PersistenceManager;
import com.rab.framework.domain.domainconfig.DomainConfig;
import com.rab.framework.domain.domainconfig.ModelConfig;
import com.rab.framework.domain.domainconfig.PersistenceDomainConfig;
import com.rab.framework.domain.domainconfig.TransactionCfg;

/**
 * 
 * <P>Title: VHAppContext</P>
 * <P>Description: </P>
 * <P>����˵��������ҵ���߼������Context����������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class CoreAppContextImp implements CoreAppContext{
	/**
	 * ��־��¼����
	 */
	private final static LogWritter log = LogFactory.getLogger(CoreAppContextImp.class);

    /**
     * context ���Ʊ�ʶ
     */
    private String name;

    /**
     * ģ��������Ϣ
     */
    private ModelConfig modelConfig;
    
    /**
     * Server�ص�
     */
    private CoreAppServer server;
    
    /**
     * <p>��ű�ģ����ص�PersistenceManager��������á�</p>
     * <p>�������ڴ�ŵ�PersistenceManager������Server�����иö��󼯺ϵ��Ӽ���</p>
     */
    private Map<String,PersistenceManager> ctxPersistenceManagers = new HashMap<String,PersistenceManager>();
   
    /**
     * ������
     * 
     * @param contextName context����
     * @param server Server��������
     */
    public CoreAppContextImp(ModelConfig modelConfig, CoreAppServer server) throws BaseCheckedException {
        this.server = server;
        this.modelConfig = modelConfig;
        this.name = modelConfig.getModelName();
        
        //��ʼ��
        initContext(modelConfig);
    }
   
    public CoreAppContextImp(){
    	
    }

    /**
     * ����ͻ��˷��͵�ҵ������
     * 
     * @param reqEvent �������
     * @return         ���ش���Ľ����װ����
     * @throws BaseCheckedException
     */
    public BaseResponseEvent execute(BaseRequestEvent reqEvent) throws BaseCheckedException {

        //1. ȡ��transactionid
        String transactionid = reqEvent.getTransactionID();
        
        //2. ����transactionid�� ������Ӧ�Ľ��״���ʵ��
        TransactionManager transcationManager = creatorTranscationManager(transactionid);
        
        //3. ִ��
        BaseResponseEvent resp = transcationManager.doTranscation(reqEvent);
        
        //Context
        return resp;
    }

    /**
     * ����������Ϣ�Ĵ��������
     * 
     * @param transactionid
     * @return
     * @throws BaseCheckedException
     */
    private TransactionManager creatorTranscationManager (String transactionid){
    	DomainConfig domainConfig = this.modelConfig.getDomainConfig();
    	Map<String,TransactionCfg> transations = domainConfig.getTransactions();
    	TransactionCfg tCfg = transations.get(transactionid);
    	
    	TransactionManager tm = new TransactionManager();
    	tm.setTransactionCfg(tCfg);
    	tm.setContext(this);
	
		//��ʼ��
    	tm.initialize();
    	
    	return tm;
    }
   
	/**
	 * ��ʼ��Contextʵ������Ҫ��Hibernate�־ò�Ĳ�����ʼ��
	 * SessionFactory�����ݲ����������������Serverͳһ����
	 * 
	 * @param modelConfig
	 * @throws BaseCheckedException
	 */
    private void initContext(ModelConfig modelConfig){

    	List<PersistenceDomainConfig> persistenceConfigs = modelConfig.getPersistenceConfigs();
    	
    	for(int i=0; i<persistenceConfigs.size(); i++){
    		PersistenceDomainConfig pdc = persistenceConfigs.get(i);
    		String sessionFactoryName = pdc.getSessionfactoryName();
    		
    		//��Server������ȡPersistenceManagerʵ����ԭ���ϣ���ʵ����Ϊnull
            PersistenceManager pm = server.getPersistenceManager(sessionFactoryName);
        	
            if(CoreAppServer.RUNNING_MODEL_DEVELOP.equalsIgnoreCase(server.getRunningmodel())){
        		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        		log.info("!! ��ǰ���ڿ���ģʽ���У�����ʼ��hibernateӳ���ļ���");
        		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        	}
        	else{
        		List<String> mappingClasses   = pdc.getMappingClasses();
        		List<String> mappingResources = pdc.getMappingResources();
        		
        		try {
					pm.registerMappingByClass(mappingClasses);     //ע��ģʽ
					pm.registerMappingByResource(mappingResources);//ӳ���ļ�ģʽ
					
				} catch (BaseCheckedException e) {
					this.server.exit(e);
					return;
				}
        	}

        	//���ù�ϵ����ʼ�����Զ����µ�server��
        	//��������ʵ�����뱾ģ����������У��������ñ�ʶ
        	ctxPersistenceManagers.put(sessionFactoryName, pm);
    	}
    }

    public String getName() {
        return name;
    }

    public void setServer(CoreAppServer server) {
        this.server = server;
    }

	public CoreAppServer getServer() {
		return server;
	}

	public Map<String,PersistenceManager> getCtxPersistenceManagers() {
		return this.ctxPersistenceManagers;
	}

	public ModelConfig getModelConfig() {
		return modelConfig;
	}
	
	public Object clone() throws CloneNotSupportedException{
		CoreAppContextImp ctx = new CoreAppContextImp();
    	ctx.name = this.name;
    	ctx.modelConfig = this.modelConfig;
    	ctx.server = this.server;
    	ctx.ctxPersistenceManagers = this.ctxPersistenceManagers;
		
    	return ctx;
    }

}

