package com.rab.framework.domain.server;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.cachesession.CacheSession;
import com.rab.framework.comm.cachesession.CacheSessionManagerImpl;
import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.SysRequestEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.lc.LcManager;
import com.rab.framework.comm.lc.LcManagerContext;
import com.rab.framework.comm.lc.Logger;
import com.rab.framework.comm.lc.SecurityUtils;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.log.PerformanceLog;
import com.rab.framework.comm.security.Ticket;
import com.rab.framework.comm.util.DateUtils;
import com.rab.framework.comm.util.FileUtils;
import com.rab.framework.component.console.ServerManager;
import com.rab.framework.dao.PersistenceManager;
import com.rab.framework.domain.domainconfig.DomainConfig;
import com.rab.framework.domain.domainconfig.ModelConfig;
import com.rab.framework.domain.domainconfig.PersistenceCommonConfig;
import com.rab.framework.domain.domainconfig.TransactionCfg;
import com.rab.sys.security.login.event.LoginRequestEvent;

/**
 * 
 * <P>Title: VHAppServer</P>
 * <P>Description: </P>
 * <P>程序说明：业务层核心控制程序：Server</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-30</P>
 *
 */
public class CoreAppServer implements Cloneable{
	/**
	 * 日志记录对象
	 */
	private final static LogWritter logger = LogFactory.getLogger(CoreAppServer.class);

	public static final String RUNNING_MODEL_PRODUCT = "product";
	public static final String RUNNING_MODEL_DEVELOP = "develop";

	//--------------------------------------------------------------------------------------属性
	/**
	 * Server的原型实例
	 */
	private static CoreAppServer protoServer = null;
	
	/**
	 * 运行模式!
	 */
	private String runningmodel = null;

	/**
	 * context实例
	 */
	private Map<String,CoreAppContext> contexts = new HashMap<String,CoreAppContext>();

	/**
	 * <p>PersistenceManager是系统共享的，也就是说，系统中有一个数据源，
	 * 就有唯一的一个PersistenceManager实例。</p>
	 * <p>共享的目的是为了适应多模块间共享实体对象的情况，如果针对每一个
	 * 模块创建一个独立的PersistenceManager,模块间不共享，则会导致重复的
	 * Hibernate加载项，严重影响内存性能</p>
	 */
	private Map<String,PersistenceManager> persistenceManagers = new HashMap<String,PersistenceManager>();
	
	/**
	 * context和transactiond的映射关系
	 */
	private Map<String,String> ctx_tx_mapping = new HashMap<String,String>();

	/**
	 * 持久层通用配置信息
	 */
	private Map<String,Properties> persistenceCommonConfig = new HashMap<String,Properties>();

	/**
	 * 安全控制例外，在检验用户登录状态的情况下，下列请求类型不做登录安全校验
	 * <property name="transaction-not-checked" value="xxx,xxxx"/> 
	 */
	private List<Object> tidNotCheckeds = null;
	
	/**
	 * 系统加载的业务模块
	 */
	private Map<String,ModelConfig> modelConfigs = null;

	/**
	 * 通过原型模式，返回Server的运行时实例
	 * 
	 * @return
	 * 
	 * @throws BaseCheckedException
	 */
	public static CoreAppServer getInstance() throws BaseCheckedException {
		CoreAppServer concreteServer = null;
		
		//服务器启动，创建服务器实例原型！
		if (protoServer == null) {
			synchronized(ApplicationContext.singleton()){
				try {
					if(protoServer == null){
						protoServer = new CoreAppServer();
					}
					logger.info("服务器启动，创建原型实例成功！");
				} catch (Exception e) {
					logger.error("00000200: 服务器启动失败！",e);
					System.exit(-1);
				}
			}
			
			//启动监控管理平台
			String envPort = System.getProperty("managerport");
			int managerPort = 0;
			if (envPort != null) {
				try {
					managerPort = Integer.parseInt(envPort);
					ServerManager sm = ServerManager.getInstance();
					sm.setServer(protoServer);
					sm.setPort(managerPort);
					sm.startServerManager();
					logger.info("服务器启动，启动监控管理平台成功！");
				} catch (Exception e) {
					logger.error("00000201： 管理监控平台启动失败！", e);
				}
			}

//			//启动代码表缓存
//	    	String className = ServerCacheManager.class.getName();
//	    	try {
//	    		logger.debug("开始加载web端缓存数据 .................................");
//				Class<?> claz = Class.forName(className);
//				Class<?>[] paramTypes = {(new String[0].getClass())};
//				Method method = claz.getMethod("main", paramTypes);
//				Object[] param = {null};
//				method.invoke(null, param);
//
//	    		logger.debug("web端缓存数据加载完毕!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//				
//			} catch (Exception e) {
//				logger.error("web端缓存加载出现异常！", e);
//			} 
		}
		
		try {
			concreteServer = (CoreAppServer) protoServer.clone();
		} catch (Exception e) {
			throw new BaseCheckedException("00000202", e);
		}
		return concreteServer;
	}

	/**
	 * 由transactionid确定其对应的context对象
	 * 
	 * @param transactionid
	 * 
	 * @return
	 */
	public CoreAppContext getContext(String transactionid)
			throws BaseCheckedException {
		// 1. 由transactionid得到对应的contextid
		String contextid = (String) this.ctx_tx_mapping.get(transactionid);
		CoreAppContext protoContext = (CoreAppContext) this.contexts.get(contextid);

		if (protoContext == null) {
			return null;
		}

		CoreAppContext concreteContext = null;
		try {
			concreteContext = (CoreAppContext) protoContext.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("clone Context对象时出现异常!", e);
			throw new BaseCheckedException("00000001");
		}
		return concreteContext;
	}

    protected CoreAppServer() throws BaseCheckedException { 
    	modelConfigs = ModelContext.singleton().getModelConfigs();
    	
        init();
    }
    
    /**
     * 初始化
     * 
     * 初始化过程包括:
     * 1. 检查系统配置参数,确定是否需要自定义类加载器
     * 2. 初始化类加载器
     * 3. 加载并初始化持久层通用配置信息.  根据持久层数据资源的定义信息, 初始化Hibernate的Configuration对象,并
     *    用Configuration初始化PersistenceManager对象实例,并将PersistenceManager缓存到系统的对象缓存池中.
     * 
     * @throws CSSBaseCheckedException
     */
    private void init() throws BaseCheckedException { 
    	//0. 加载bootstrap.xml
    	ApplicationContext.singleton();
		this.runningmodel = (String)ApplicationContext.singleton().getValueByKey("running-model");
    	
    	//1. 初始化transactionid到context的映射表
    	Map<String,ModelConfig> modelConfigs = ModelContext.singleton().getModelConfigs();
    	routerRegister(modelConfigs);
    	   
    	//3. 初始化持久层通用配置信息
    	this.persistenceManagers = this.createPersistenceManager();
    	
		//4. 产品授权验证

        //5. 加载业务单元-先保证加载framework单元        
        if(modelConfigs.get("framework")!=null) {
        	ModelConfig framework = (ModelConfig)modelConfigs.get("framework");
        	
			//检查授权情况！
			licenseCheck(framework.getModelName());

        	logger.info("========================================");
			logger.debug("Model Name: [" + framework.getModelName() + "]");
			logger.info("========================================");
			CoreAppContext contextInstance = createContext(framework, this);
			this.contexts.put(framework.getModelName(),contextInstance);
        }
        
		Iterator<ModelConfig> iter = modelConfigs.values().iterator();		
		while(iter.hasNext()){
			ModelConfig modelConfig = iter.next();;
			if(!modelConfig.getModelName().equals("framework")) {
				
				//检查授权情况！
				licenseCheck(modelConfig.getModelName());
				
				logger.info("========================================");
				logger.debug("Model Name: " + modelConfig.getModelName() + "]");
				logger.info("========================================");
				
				CoreAppContext contextInstance = createContext(modelConfig, this);
				if(contextInstance == null){ 
					logger.error(modelConfig.getModelName() + "模块加载失败!!!");
					//如果加载失败，继续加载下一个实例！
					continue;
				}
				this.contexts.put(modelConfig.getModelName(),contextInstance);
			}
		}
		
		//6. 集中初始化持久层管理，创建SessionFactory对象
		Iterator<PersistenceManager> iterPM = this.persistenceManagers.values().iterator();
		while(iterPM.hasNext()){
			PersistenceManager pm = iterPM.next();
			pm.initSessionFactory();
		}
    } 
    
    private void licenseCheck(String compName){
    	//暂时屏蔽license授权检查
//    	if(true) return;
    	
    	LcManager ilm = LcManagerContext.singleton().getInstance();
		String[] licensed = ilm.getLicenseComponents();
		if (licensed == null || licensed.length == 0) {
			String msg = "\r\n";
			msg += "+----------------------------------------------------------+\r\n";
			msg += "+ 重要信息提示!!!!                                            \r\n";
			msg += "+ 系统中没有授权可以运行的模块,请和软件销售商联系            \r\n";
			msg += "+----------------------------------------------------------+\r\n";
			Logger.log(msg);
			System.exit(-1);
		}

		boolean flag = false;
		for(int i=0; i<licensed.length; i++){
			if(licensed[i].equals(compName)){
				flag = true;
				break;
			}
		}
		
		if(!flag){
			Logger.log("========================================");
			Logger.log("模块 " + compName + " 没有授权，请检查系统授权文件，程序启动初始化终止！");
			Logger.log("========================================");
			System.exit(-1);
		}
    }
    
    /**
     * 执行请求
     * 
     * @param reqEvent
     * @return
     * @throws BaseCheckedException
     */
    public BaseResponseEvent execute(BaseRequestEvent reqEvent)	throws BaseCheckedException {
    	//性能日志：服务开始时间
    	long t0 = System.currentTimeMillis();
    	
    	//1. 检查用户是否已经登录
    	checkUserLoginState(reqEvent); 
    	
    	//2. 找对应的Context对象
        String transactionid = reqEvent.getTransactionID();
        logger.debug("当前的 tid = " + transactionid);
        CoreAppContext context = getContext(transactionid);
        if(context == null){
//        	logger.error("请求 tid = " + transactionid + " 没有找到对应的服务器端逻辑映射，请核查BLH的名称客户端程序中requestEvent构造器中的transactionid属性设置是否正确! ");
        	List<String> params = new ArrayList<String>();
        	params.add(transactionid);
        	throw new BaseCheckedException("00000301",params);
        }      
        context.setServer(this);
        
        BaseResponseEvent respEvent = context.execute(reqEvent);
        
        
        //性能日志：服务结束时间
        long t1 = System.currentTimeMillis();
        
      //记录性能日志
        try {
			perflog(t1-t0, reqEvent);
		} catch (Exception e) {
			logger.error("记录性能日志时出现异常！",e);
		}
        
        return respEvent;
    }

    /**
     * <p>记录性能日志</p>
     *
     * @param t
     * @param reqEvent
     */
    private void perflog(long t, BaseRequestEvent reqEvent){
    	//检查性能日志记录开关是否打开，如果没有打开，则不记录性能日志
		String perflog = (String)ApplicationContext.singleton().getValueByKey("perf-log");
		if(perflog == null || !perflog.equalsIgnoreCase("true")){
			return;
		}
    	
    	//当前时间
    	String curDateTime = DateUtils.toDateTimeStr(Calendar.getInstance());
    	
    	//用户编号
    	String usercode = "";
    	String sessionid = reqEvent.getSessionID();
    	if(reqEvent instanceof SysRequestEvent){
    		if(reqEvent instanceof LoginRequestEvent){
    			usercode = ((LoginRequestEvent)reqEvent).getUsercode();
    		}
    	}
    	else{
    		CacheSession cs = CacheSessionManagerImpl.singleton().getCacheSession(sessionid);
    		Ticket ticket = (Ticket)cs.getValue("ticket");
    		if(ticket != null){
    			usercode = ticket.getUser().getUsercode();
    		}
    	}
    	
    	//BLH名称
    	String transactionID = reqEvent.getTransactionID();
    	
    	//BLH方法
    	String methodName = reqEvent.getMethod();
    	
    	PerformanceLog perfLogger = LogFactory.getPerfLogger();
    	perfLogger.perflog(curDateTime, usercode, transactionID, methodName, t);
    }
    
    
    /**
     * 初始化持久层通用配置信息，并创建空的PersistenceManager对象集合
     * 
     * @return
     */
    private Map<String, PersistenceManager> createPersistenceManager(){
    	 Map<String, PersistenceManager> persistenceManagers = new HashMap<String, PersistenceManager>();
    	 
    	 //从上下文中获取持久层通用配置信息
    	 Map<String,PersistenceCommonConfig> persistenceCommonConfigs = ModelContext.singleton().getPersistenceCommonConfig();
    	 Iterator<String> iter = persistenceCommonConfigs.keySet().iterator();
    	 while(iter.hasNext()){
    		 String sessionFactoryName = iter.next();
    		 PersistenceCommonConfig persistenceCommonConfig = persistenceCommonConfigs.get(sessionFactoryName);
    		 PersistenceManager persistenceManager = new PersistenceManager(persistenceCommonConfig);
    		 
    		 //将新创建的persistenceManager对象放入Server级缓存中
    		 persistenceManagers.put(sessionFactoryName, persistenceManager);
    	 }
    	 
    	 return persistenceManagers;
    }
    
	private void routerRegister(Map<String,ModelConfig> modelConfigs) {
		Iterator<String> iterModel = modelConfigs.keySet().iterator();
		while(iterModel.hasNext()){
			String modelname = iterModel.next();
			ModelConfig modelConfig = modelConfigs.get(modelname);
			DomainConfig domainConfig = modelConfig.getDomainConfig();
			Map<String,TransactionCfg> transactions = domainConfig.getTransactions();
			
			Iterator<String> iter = transactions.keySet().iterator();
			while (iter.hasNext()) {
				String transactionId = (String) iter.next();
				this.ctx_tx_mapping.put(transactionId, modelname);
			}
		}
	}

    public boolean loadContext(String modelName){
    	boolean flag = false;
    	
    	ModelConfig modelConfig = (ModelConfig)this.modelConfigs.get(modelName);
    	CoreAppContext context = createContext(modelConfig,this);
    	if(context != null){ 
			this.contexts.put(modelName,context);
			flag = true;
		}    	

    	return flag;
    }
    
    public boolean reloadContext(String modelName){
    	boolean flag = false;
    	
    	CoreAppContext oldContext = (CoreAppContext)this.getContexts().get(modelName);
    	ModelConfig modelConfig = (ModelConfig)this.modelConfigs.get(modelName);
    	synchronized(oldContext){
    		CoreAppContext newContext = createContext(modelConfig,this);
        	if(newContext != null){ 
    			this.contexts.put(modelName,newContext);
    			flag = true;
    		}    	
    	}

    	return flag;
    }
    
    public boolean unloadContext(String contextName){
    	try {
			this.contexts.remove(contextName);
			return true;
			
		} catch (Exception e) {
			logger.error("卸载context " + contextName + " 时出现异常!", e);
			return false;
		}   	
    }
    
    /**
     * 
     * <p>退出系统，在系统初始化过程中，如果出现重大异常，则终止启动，系统退出！</p>
     *
     * @param msg
     */
    public void exit(Exception e){
    	logger.error("系统初始化时出现异常，服务退出，请检查系统部署参数是否正确！");
      	logger.error("系统运行异常信息为：" + e.getMessage());
      	logger.error("系统运行异常堆栈为：", e);
    	System.exit(-1);
    }
    
    /**
     * 创建Context实例
     * 创建Context实例失败时,将返回null值,并将异常信息记录日志. Server在检测到该null值时,
     * 会跳过该Context的初始化,继续做下一个Context的初始化工作.
     * 
     * @return context实例
     */
    private CoreAppContext createContext(ModelConfig modelConfig, CoreAppServer server) {
    	CoreAppContext context = null;
        
        try {
        	
        	//----------------------------------------lfw 20100310 添加安全启动控制
        	String className = "com.rab.framework.domain.server.CoreAppContextImp";
        	String path = className.replace(".", "/") + ".class";
        	
        	URL[] urls = new URL[0];
        	SecurityLoader sl = new SecurityLoader(urls, this.getClass().getClassLoader());
        	
        	FileUtils fileUtils = new FileUtils(path);
    		InputStream in = fileUtils.getInputStream();
    		
			byte[] data = new byte[0];
			byte[] buffer = new byte[1024];
			int len = in.read(buffer);
			while(len > 0){
				byte[] tmp = new byte[data.length + len];
				System.arraycopy(data, 0, tmp, 0, data.length);
				System.arraycopy(buffer, 0, tmp, data.length, len);
				data = tmp;
				len = in.read(buffer);
			}
			
			//检查该类是否正常的class文件，如果不是，则表明该类文件是加密的，需要解密
			//用异或运算，判断结果是否为0
			if(((byte)(data[0] ^ 0xCA))!=0 
					|| ((byte)(data[1] ^ 0xFE))!=0 
					|| ((byte)(data[2] ^ 0xBA))!=0  
					|| ((byte)(data[3] ^ 0xBE))!=0){
				
				SecurityUtils su = new SecurityUtils();
				data = su.decrypt(data); //解密
			}
			
        	Class claz = sl.defineClass2(null, data, 0, data.length);
        	Constructor<CoreAppContext> constructor = claz.getConstructor(ModelConfig.class, CoreAppServer.class);
        	context = constructor.newInstance(modelConfig, server);
        	
        	//----------------------------------------
        	
//			context = new CoreAppContextImp(modelConfig, server);
		}
        catch (Exception e) {
			logger.error("创建实例 " + modelConfig.getModelName() + " 失败, 请检查系统配置！", e);
		}        
        return context;
    }
    
//    private byte[] getClassBytes(String file) throws Exception{
//		FileInputStream in = new FileInputStream(file);
//		
//		byte[] data = new byte[0];
//		byte[] buffer = new byte[1024];
//		int len = in.read(buffer);
//		while(len > 0){
//			byte[] tmp = new byte[data.length + len];
//			System.arraycopy(data, 0, tmp, 0, data.length);
//			System.arraycopy(buffer, 0, tmp, data.length, len);
//			data = tmp;
//			len = in.read(buffer);
//		}
//
//		SecurityUtils su = new SecurityUtils();
//		data = su.decrypt(data);
//		
//		return data;
//    }
    
    private void checkUserLoginState(BaseRequestEvent reqEvent) throws BaseCheckedException{
		//检查总开关是否打开，如果为false，则不启动安全过滤检查逻辑
		boolean flag = ApplicationContext.singleton().checkRuntimeSecurityManager();
		if(flag == false){
			return;
		}

		//1. 开发模式下不检查用户的登录状态
		String running_model = "" + ApplicationContext.singleton().getValueByKey("running-model");
		if("develop".equalsIgnoreCase(running_model)){
			return;
		}

    	//2. 检查是否不需要校验登录状态
    	if(reqEvent instanceof SysRequestEvent){
    		return;
    	}

    	String sessionid = reqEvent.getSessionID();
    	String transactionid = reqEvent.getTransactionID();
    	logger.debug("sessionid = " + sessionid);
    	logger.debug("transactionid = " + transactionid);

    	//3.  检查sessionid标记是否为空
    	if(sessionid == null){ //
    		logger.error("00000500: 检查到本次请求的user sessionid标记为空,当前操作没有登录,属非法访问，请核对登录状态！");
    		logger.error("reqEvent = " + reqEvent.getClass().getName());
    		logger.error("method = " + reqEvent.getMethod());
    		
    		throw new BaseCheckedException("00000500"); 
    	}

    	//4. 解析免检transactionid
    	if(tidNotCheckeds == null){
    		tidNotCheckeds = new ArrayList<Object>();
        	String transactionNotChecked = (String)ApplicationContext.singleton().getValueByKey("transaction-not-checked");
    		synchronized(this.tidNotCheckeds){
        		if(transactionNotChecked != null){
        			StringTokenizer st = new StringTokenizer(transactionNotChecked, ",");
        			while(st.hasMoreElements()){
        				this.tidNotCheckeds.add(st.nextElement());
        			}
        		}
    		}
    	}
    	//4.1 检查是否为免检transactionid
    	for(int i=0; i<this.tidNotCheckeds.size(); i++){
    		if(transactionid.equals("" + this.tidNotCheckeds.get(i))){
    			logger.debug("检测到免检 transactionid = " + transactionid);
    			return; //检查到该transactionid为免检查id,直接返回
    		}
    	}


    	//4. 检查用户是否已经登录
    	CacheSession cacheSession = CacheSessionManagerImpl.singleton().getCacheSession(sessionid);
    	Object ticket = cacheSession.getValue("ticket");
       	logger.debug("cacheSession = " + cacheSession);
       	logger.debug("ticket = " + ticket);
    	if(ticket == null){
    		//检测到用户没有登录, 抛出异常!
    		logger.error("00000300: 检测到用户没有登录！");
    		throw new BaseCheckedException("00000300");
    	}
    }	
    
	//---------------------------------------------------------------------------getter & setter	
	public Map<String, CoreAppContext> getContexts() {
		return contexts;
	}

	public Map<String, Properties> getPersistenceCommonConfig() {
		return persistenceCommonConfig;
	}

	public void setPersistenceCommonConfig(Map<String,Properties> persistenceCommonConfig) {
		this.persistenceCommonConfig = persistenceCommonConfig;
	}

	public Properties getPersistenceCommonConfigByName(String name) {
		Properties props = (Properties) this.persistenceCommonConfig.get(name);
		return props;
	}

	public PersistenceManager getPersistenceManager(String sessionFactoryName) {
		return this.persistenceManagers.get(sessionFactoryName);
	}

	public String getRunningmodel() {
		return runningmodel;
	}	
}
