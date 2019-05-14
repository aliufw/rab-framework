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
 * <P>����˵����ҵ�����Ŀ��Ƴ���Server</P>
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
	 * ��־��¼����
	 */
	private final static LogWritter logger = LogFactory.getLogger(CoreAppServer.class);

	public static final String RUNNING_MODEL_PRODUCT = "product";
	public static final String RUNNING_MODEL_DEVELOP = "develop";

	//--------------------------------------------------------------------------------------����
	/**
	 * Server��ԭ��ʵ��
	 */
	private static CoreAppServer protoServer = null;
	
	/**
	 * ����ģʽ!
	 */
	private String runningmodel = null;

	/**
	 * contextʵ��
	 */
	private Map<String,CoreAppContext> contexts = new HashMap<String,CoreAppContext>();

	/**
	 * <p>PersistenceManager��ϵͳ����ģ�Ҳ����˵��ϵͳ����һ������Դ��
	 * ����Ψһ��һ��PersistenceManagerʵ����</p>
	 * <p>�����Ŀ����Ϊ����Ӧ��ģ��乲��ʵ�����������������ÿһ��
	 * ģ�鴴��һ��������PersistenceManager,ģ��䲻������ᵼ���ظ���
	 * Hibernate���������Ӱ���ڴ�����</p>
	 */
	private Map<String,PersistenceManager> persistenceManagers = new HashMap<String,PersistenceManager>();
	
	/**
	 * context��transactiond��ӳ���ϵ
	 */
	private Map<String,String> ctx_tx_mapping = new HashMap<String,String>();

	/**
	 * �־ò�ͨ��������Ϣ
	 */
	private Map<String,Properties> persistenceCommonConfig = new HashMap<String,Properties>();

	/**
	 * ��ȫ�������⣬�ڼ����û���¼״̬������£������������Ͳ�����¼��ȫУ��
	 * <property name="transaction-not-checked" value="xxx,xxxx"/> 
	 */
	private List<Object> tidNotCheckeds = null;
	
	/**
	 * ϵͳ���ص�ҵ��ģ��
	 */
	private Map<String,ModelConfig> modelConfigs = null;

	/**
	 * ͨ��ԭ��ģʽ������Server������ʱʵ��
	 * 
	 * @return
	 * 
	 * @throws BaseCheckedException
	 */
	public static CoreAppServer getInstance() throws BaseCheckedException {
		CoreAppServer concreteServer = null;
		
		//����������������������ʵ��ԭ�ͣ�
		if (protoServer == null) {
			synchronized(ApplicationContext.singleton()){
				try {
					if(protoServer == null){
						protoServer = new CoreAppServer();
					}
					logger.info("����������������ԭ��ʵ���ɹ���");
				} catch (Exception e) {
					logger.error("00000200: ����������ʧ�ܣ�",e);
					System.exit(-1);
				}
			}
			
			//������ع���ƽ̨
			String envPort = System.getProperty("managerport");
			int managerPort = 0;
			if (envPort != null) {
				try {
					managerPort = Integer.parseInt(envPort);
					ServerManager sm = ServerManager.getInstance();
					sm.setServer(protoServer);
					sm.setPort(managerPort);
					sm.startServerManager();
					logger.info("������������������ع���ƽ̨�ɹ���");
				} catch (Exception e) {
					logger.error("00000201�� ������ƽ̨����ʧ�ܣ�", e);
				}
			}

//			//�����������
//	    	String className = ServerCacheManager.class.getName();
//	    	try {
//	    		logger.debug("��ʼ����web�˻������� .................................");
//				Class<?> claz = Class.forName(className);
//				Class<?>[] paramTypes = {(new String[0].getClass())};
//				Method method = claz.getMethod("main", paramTypes);
//				Object[] param = {null};
//				method.invoke(null, param);
//
//	    		logger.debug("web�˻������ݼ������!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//				
//			} catch (Exception e) {
//				logger.error("web�˻�����س����쳣��", e);
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
	 * ��transactionidȷ�����Ӧ��context����
	 * 
	 * @param transactionid
	 * 
	 * @return
	 */
	public CoreAppContext getContext(String transactionid)
			throws BaseCheckedException {
		// 1. ��transactionid�õ���Ӧ��contextid
		String contextid = (String) this.ctx_tx_mapping.get(transactionid);
		CoreAppContext protoContext = (CoreAppContext) this.contexts.get(contextid);

		if (protoContext == null) {
			return null;
		}

		CoreAppContext concreteContext = null;
		try {
			concreteContext = (CoreAppContext) protoContext.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("clone Context����ʱ�����쳣!", e);
			throw new BaseCheckedException("00000001");
		}
		return concreteContext;
	}

    protected CoreAppServer() throws BaseCheckedException { 
    	modelConfigs = ModelContext.singleton().getModelConfigs();
    	
        init();
    }
    
    /**
     * ��ʼ��
     * 
     * ��ʼ�����̰���:
     * 1. ���ϵͳ���ò���,ȷ���Ƿ���Ҫ�Զ����������
     * 2. ��ʼ���������
     * 3. ���ز���ʼ���־ò�ͨ��������Ϣ.  ���ݳ־ò�������Դ�Ķ�����Ϣ, ��ʼ��Hibernate��Configuration����,��
     *    ��Configuration��ʼ��PersistenceManager����ʵ��,����PersistenceManager���浽ϵͳ�Ķ��󻺴����.
     * 
     * @throws CSSBaseCheckedException
     */
    private void init() throws BaseCheckedException { 
    	//0. ����bootstrap.xml
    	ApplicationContext.singleton();
		this.runningmodel = (String)ApplicationContext.singleton().getValueByKey("running-model");
    	
    	//1. ��ʼ��transactionid��context��ӳ���
    	Map<String,ModelConfig> modelConfigs = ModelContext.singleton().getModelConfigs();
    	routerRegister(modelConfigs);
    	   
    	//3. ��ʼ���־ò�ͨ��������Ϣ
    	this.persistenceManagers = this.createPersistenceManager();
    	
		//4. ��Ʒ��Ȩ��֤

        //5. ����ҵ��Ԫ-�ȱ�֤����framework��Ԫ        
        if(modelConfigs.get("framework")!=null) {
        	ModelConfig framework = (ModelConfig)modelConfigs.get("framework");
        	
			//�����Ȩ�����
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
				
				//�����Ȩ�����
				licenseCheck(modelConfig.getModelName());
				
				logger.info("========================================");
				logger.debug("Model Name: " + modelConfig.getModelName() + "]");
				logger.info("========================================");
				
				CoreAppContext contextInstance = createContext(modelConfig, this);
				if(contextInstance == null){ 
					logger.error(modelConfig.getModelName() + "ģ�����ʧ��!!!");
					//�������ʧ�ܣ�����������һ��ʵ����
					continue;
				}
				this.contexts.put(modelConfig.getModelName(),contextInstance);
			}
		}
		
		//6. ���г�ʼ���־ò��������SessionFactory����
		Iterator<PersistenceManager> iterPM = this.persistenceManagers.values().iterator();
		while(iterPM.hasNext()){
			PersistenceManager pm = iterPM.next();
			pm.initSessionFactory();
		}
    } 
    
    private void licenseCheck(String compName){
    	//��ʱ����license��Ȩ���
//    	if(true) return;
    	
    	LcManager ilm = LcManagerContext.singleton().getInstance();
		String[] licensed = ilm.getLicenseComponents();
		if (licensed == null || licensed.length == 0) {
			String msg = "\r\n";
			msg += "+----------------------------------------------------------+\r\n";
			msg += "+ ��Ҫ��Ϣ��ʾ!!!!                                            \r\n";
			msg += "+ ϵͳ��û����Ȩ�������е�ģ��,��������������ϵ            \r\n";
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
			Logger.log("ģ�� " + compName + " û����Ȩ������ϵͳ��Ȩ�ļ�������������ʼ����ֹ��");
			Logger.log("========================================");
			System.exit(-1);
		}
    }
    
    /**
     * ִ������
     * 
     * @param reqEvent
     * @return
     * @throws BaseCheckedException
     */
    public BaseResponseEvent execute(BaseRequestEvent reqEvent)	throws BaseCheckedException {
    	//������־������ʼʱ��
    	long t0 = System.currentTimeMillis();
    	
    	//1. ����û��Ƿ��Ѿ���¼
    	checkUserLoginState(reqEvent); 
    	
    	//2. �Ҷ�Ӧ��Context����
        String transactionid = reqEvent.getTransactionID();
        logger.debug("��ǰ�� tid = " + transactionid);
        CoreAppContext context = getContext(transactionid);
        if(context == null){
//        	logger.error("���� tid = " + transactionid + " û���ҵ���Ӧ�ķ��������߼�ӳ�䣬��˲�BLH�����ƿͻ��˳�����requestEvent�������е�transactionid���������Ƿ���ȷ! ");
        	List<String> params = new ArrayList<String>();
        	params.add(transactionid);
        	throw new BaseCheckedException("00000301",params);
        }      
        context.setServer(this);
        
        BaseResponseEvent respEvent = context.execute(reqEvent);
        
        
        //������־���������ʱ��
        long t1 = System.currentTimeMillis();
        
      //��¼������־
        try {
			perflog(t1-t0, reqEvent);
		} catch (Exception e) {
			logger.error("��¼������־ʱ�����쳣��",e);
		}
        
        return respEvent;
    }

    /**
     * <p>��¼������־</p>
     *
     * @param t
     * @param reqEvent
     */
    private void perflog(long t, BaseRequestEvent reqEvent){
    	//���������־��¼�����Ƿ�򿪣����û�д򿪣��򲻼�¼������־
		String perflog = (String)ApplicationContext.singleton().getValueByKey("perf-log");
		if(perflog == null || !perflog.equalsIgnoreCase("true")){
			return;
		}
    	
    	//��ǰʱ��
    	String curDateTime = DateUtils.toDateTimeStr(Calendar.getInstance());
    	
    	//�û����
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
    	
    	//BLH����
    	String transactionID = reqEvent.getTransactionID();
    	
    	//BLH����
    	String methodName = reqEvent.getMethod();
    	
    	PerformanceLog perfLogger = LogFactory.getPerfLogger();
    	perfLogger.perflog(curDateTime, usercode, transactionID, methodName, t);
    }
    
    
    /**
     * ��ʼ���־ò�ͨ��������Ϣ���������յ�PersistenceManager���󼯺�
     * 
     * @return
     */
    private Map<String, PersistenceManager> createPersistenceManager(){
    	 Map<String, PersistenceManager> persistenceManagers = new HashMap<String, PersistenceManager>();
    	 
    	 //���������л�ȡ�־ò�ͨ��������Ϣ
    	 Map<String,PersistenceCommonConfig> persistenceCommonConfigs = ModelContext.singleton().getPersistenceCommonConfig();
    	 Iterator<String> iter = persistenceCommonConfigs.keySet().iterator();
    	 while(iter.hasNext()){
    		 String sessionFactoryName = iter.next();
    		 PersistenceCommonConfig persistenceCommonConfig = persistenceCommonConfigs.get(sessionFactoryName);
    		 PersistenceManager persistenceManager = new PersistenceManager(persistenceCommonConfig);
    		 
    		 //���´�����persistenceManager�������Server��������
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
			logger.error("ж��context " + contextName + " ʱ�����쳣!", e);
			return false;
		}   	
    }
    
    /**
     * 
     * <p>�˳�ϵͳ����ϵͳ��ʼ�������У���������ش��쳣������ֹ������ϵͳ�˳���</p>
     *
     * @param msg
     */
    public void exit(Exception e){
    	logger.error("ϵͳ��ʼ��ʱ�����쳣�������˳�������ϵͳ��������Ƿ���ȷ��");
      	logger.error("ϵͳ�����쳣��ϢΪ��" + e.getMessage());
      	logger.error("ϵͳ�����쳣��ջΪ��", e);
    	System.exit(-1);
    }
    
    /**
     * ����Contextʵ��
     * ����Contextʵ��ʧ��ʱ,������nullֵ,�����쳣��Ϣ��¼��־. Server�ڼ�⵽��nullֵʱ,
     * ��������Context�ĳ�ʼ��,��������һ��Context�ĳ�ʼ������.
     * 
     * @return contextʵ��
     */
    private CoreAppContext createContext(ModelConfig modelConfig, CoreAppServer server) {
    	CoreAppContext context = null;
        
        try {
        	
        	//----------------------------------------lfw 20100310 ��Ӱ�ȫ��������
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
			
			//�������Ƿ�������class�ļ���������ǣ�����������ļ��Ǽ��ܵģ���Ҫ����
			//��������㣬�жϽ���Ƿ�Ϊ0
			if(((byte)(data[0] ^ 0xCA))!=0 
					|| ((byte)(data[1] ^ 0xFE))!=0 
					|| ((byte)(data[2] ^ 0xBA))!=0  
					|| ((byte)(data[3] ^ 0xBE))!=0){
				
				SecurityUtils su = new SecurityUtils();
				data = su.decrypt(data); //����
			}
			
        	Class claz = sl.defineClass2(null, data, 0, data.length);
        	Constructor<CoreAppContext> constructor = claz.getConstructor(ModelConfig.class, CoreAppServer.class);
        	context = constructor.newInstance(modelConfig, server);
        	
        	//----------------------------------------
        	
//			context = new CoreAppContextImp(modelConfig, server);
		}
        catch (Exception e) {
			logger.error("����ʵ�� " + modelConfig.getModelName() + " ʧ��, ����ϵͳ���ã�", e);
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
		//����ܿ����Ƿ�򿪣����Ϊfalse����������ȫ���˼���߼�
		boolean flag = ApplicationContext.singleton().checkRuntimeSecurityManager();
		if(flag == false){
			return;
		}

		//1. ����ģʽ�²�����û��ĵ�¼״̬
		String running_model = "" + ApplicationContext.singleton().getValueByKey("running-model");
		if("develop".equalsIgnoreCase(running_model)){
			return;
		}

    	//2. ����Ƿ���ҪУ���¼״̬
    	if(reqEvent instanceof SysRequestEvent){
    		return;
    	}

    	String sessionid = reqEvent.getSessionID();
    	String transactionid = reqEvent.getTransactionID();
    	logger.debug("sessionid = " + sessionid);
    	logger.debug("transactionid = " + transactionid);

    	//3.  ���sessionid����Ƿ�Ϊ��
    	if(sessionid == null){ //
    		logger.error("00000500: ��鵽���������user sessionid���Ϊ��,��ǰ����û�е�¼,���Ƿ����ʣ���˶Ե�¼״̬��");
    		logger.error("reqEvent = " + reqEvent.getClass().getName());
    		logger.error("method = " + reqEvent.getMethod());
    		
    		throw new BaseCheckedException("00000500"); 
    	}

    	//4. �������transactionid
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
    	//4.1 ����Ƿ�Ϊ���transactionid
    	for(int i=0; i<this.tidNotCheckeds.size(); i++){
    		if(transactionid.equals("" + this.tidNotCheckeds.get(i))){
    			logger.debug("��⵽��� transactionid = " + transactionid);
    			return; //��鵽��transactionidΪ����id,ֱ�ӷ���
    		}
    	}


    	//4. ����û��Ƿ��Ѿ���¼
    	CacheSession cacheSession = CacheSessionManagerImpl.singleton().getCacheSession(sessionid);
    	Object ticket = cacheSession.getValue("ticket");
       	logger.debug("cacheSession = " + cacheSession);
       	logger.debug("ticket = " + ticket);
    	if(ticket == null){
    		//��⵽�û�û�е�¼, �׳��쳣!
    		logger.error("00000300: ��⵽�û�û�е�¼��");
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
