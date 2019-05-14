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
 * <P>程序说明：核心业务逻辑处理的Context环境管理器</P>
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
	 * 日志记录对象
	 */
	private final static LogWritter log = LogFactory.getLogger(CoreAppContextImp.class);

    /**
     * context 名称标识
     */
    private String name;

    /**
     * 模块配置信息
     */
    private ModelConfig modelConfig;
    
    /**
     * Server回调
     */
    private CoreAppServer server;
    
    /**
     * <p>存放本模块相关的PersistenceManager对象的引用。</p>
     * <p>本容器内存放的PersistenceManager对象是Server容器中该对象集合的子集。</p>
     */
    private Map<String,PersistenceManager> ctxPersistenceManagers = new HashMap<String,PersistenceManager>();
   
    /**
     * 构造器
     * 
     * @param contextName context名称
     * @param server Server对象引用
     */
    public CoreAppContextImp(ModelConfig modelConfig, CoreAppServer server) throws BaseCheckedException {
        this.server = server;
        this.modelConfig = modelConfig;
        this.name = modelConfig.getModelName();
        
        //初始化
        initContext(modelConfig);
    }
   
    public CoreAppContextImp(){
    	
    }

    /**
     * 处理客户端发送的业务请求
     * 
     * @param reqEvent 请求对象
     * @return         返回处理的结果封装对象
     * @throws BaseCheckedException
     */
    public BaseResponseEvent execute(BaseRequestEvent reqEvent) throws BaseCheckedException {

        //1. 取出transactionid
        String transactionid = reqEvent.getTransactionID();
        
        //2. 根据transactionid， 创建对应的交易处理实例
        TransactionManager transcationManager = creatorTranscationManager(transactionid);
        
        //3. 执行
        BaseResponseEvent resp = transcationManager.doTranscation(reqEvent);
        
        //Context
        return resp;
    }

    /**
     * 生成请求信息的处理控制器
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
	
		//初始化
    	tm.initialize();
    	
    	return tm;
    }
   
	/**
	 * 初始化Context实例，主要是Hibernate持久层的参数初始化
	 * SessionFactory对象暂不创建，将在最后由Server统一创建
	 * 
	 * @param modelConfig
	 * @throws BaseCheckedException
	 */
    private void initContext(ModelConfig modelConfig){

    	List<PersistenceDomainConfig> persistenceConfigs = modelConfig.getPersistenceConfigs();
    	
    	for(int i=0; i<persistenceConfigs.size(); i++){
    		PersistenceDomainConfig pdc = persistenceConfigs.get(i);
    		String sessionFactoryName = pdc.getSessionfactoryName();
    		
    		//从Server容器中取PersistenceManager实例，原则上，该实例不为null
            PersistenceManager pm = server.getPersistenceManager(sessionFactoryName);
        	
            if(CoreAppServer.RUNNING_MODEL_DEVELOP.equalsIgnoreCase(server.getRunningmodel())){
        		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        		log.info("!! 当前处于开发模式运行，不初始化hibernate映射文件！");
        		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        	}
        	else{
        		List<String> mappingClasses   = pdc.getMappingClasses();
        		List<String> mappingResources = pdc.getMappingResources();
        		
        		try {
					pm.registerMappingByClass(mappingClasses);     //注解模式
					pm.registerMappingByResource(mappingResources);//映射文件模式
					
				} catch (BaseCheckedException e) {
					this.server.exit(e);
					return;
				}
        	}

        	//引用关系，初始化会自动更新到server中
        	//将创建的实例放入本模块的上下文中，建立引用标识
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

