package com.rab.framework.comm.cachesession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.cachesession.dbreplication.DBReplicationManager;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;


/**
 * 
 * <P>Title: CacheSessionManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>���󻺴������</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class CacheSessionManagerImpl implements CacheSessionManager {
    /**
     * ��־��¼����
     */
	private final static LogWritter logger = LogFactory.getLogger(CacheSessionManagerImpl.class);
    
    //------------------------------------------------------------------------------ ���Զ���

	/**
     * �Ƿ�ʹ�����ݿ���Ϊ�н����������ݸ���
     */
    private boolean isDbreplication = false;
    
    /**
     * ��������, ���а����˶��רҵ���Ķ�������(SpecialCacheContainer)
     */
    private Map<String, CacheSession> cacheSessionPool = new HashMap<String, CacheSession>();

    /**
     * ��̬ʵ��
     */
    private static CacheSessionManager scm = null;

    private static Map<String, Properties> threadLocal = new HashMap<String, Properties>();
    //------------------------------------------------------------------------------ ������

    /**
     * ˽�й�����
     */
    private CacheSessionManagerImpl() {
        init();

    }


    //------------------------------------------------------------------------------ public����


    /**
     * ��������
     *
     * @return
     */
    public static CacheSessionManager singleton() {
        if (scm == null) {
            scm = new CacheSessionManagerImpl();
        }

        return scm;
    }

    /**
     * �ӻ������ȡ����������ض�������������
     * �����Ӧ���ض������������󲻴���,�򴴽�һ�����ض������������󲢻���
     *
     * @param sessionid �û����
     * @return
     */
    public CacheSession getCacheSession(String sessionid) {
        CacheSession cs = this.cacheSessionPool.get(sessionid);
        if (cs == null) {
            cs = new CacheSession(sessionid);
            cs.setManager(this);
            this.cacheSessionPool.put(sessionid, cs);
        }

        return cs;
    }

    public Properties getThreadLocal(){
    	long threadId = Thread.currentThread().getId();
    	Properties props = new Properties();
    	threadLocal.put("" + threadId, props);
    	return props;
    }
    //------------------------------------------------------------------------------ protected����


    /**
     * ���ݹ㲥����, ��ӻ��������
     *
     * @param sessionid �û���ʶ
     * @param key       ���ݹؼ���
     * @param data      �����л������ݶ���
     */
    protected void synchronizeUpdate(String sessionid, String key, Serializable data, long lastModified) {
              
		if(this.isDbreplication){
			DBReplicationManager dbrm = DBReplicationManager.getInstance();
			dbrm.saveDataToDB(sessionid, key, data, lastModified);
		}
    }

    /**
     * ���ݹ㲥����, ɾ������
     *
     * @param sessionid �û���ʶ
     * @param key       ���ݹؼ���
     */
    protected void synchronizeDel(String sessionid, String key, long lastModified) {
    	
    	if(this.isDbreplication){
    		DBReplicationManager dbrm = DBReplicationManager.getInstance();
    		dbrm.deleteDataFromDB(sessionid, key);
    	}
    }

    /**
     * �ӻ�����ɾ����ʱ�Ļ������
     *
     * @param date
     */
    public void cacheSessionTimeout(Calendar date) {
        //���ڴ���ɾ��
        Iterator<String> iter = cacheSessionPool.keySet().iterator();
        List<String> keyList = new ArrayList<String>();
        while (iter.hasNext()){
        	keyList.add(iter.next());
        }
        	
        for(int i=0; i<keyList.size(); i++) {
            Object key = keyList.get(i);
            CacheSession session = cacheSessionPool.get(key);
            if (session.getCreateTime().before(date)) {
                //TODO: ������Щ�ض��Ļ������????
                cacheSessionPool.remove(key);  //���ڴ���ɾ��
            }
        }
        
        //�����ݿ���ɾ��
        if(isDbreplication){
        	DBReplicationManager dbrm = DBReplicationManager.getInstance();
        	logger.info("��ʼ�����ݿ������session����...");
        	dbrm.deleteAllDataFromDB(date.getTimeInMillis());
        	logger.info("�����ݿ������session�������!!!");
        }
    }

    public Map<String, CacheSession> getCacheSessionPool() {
        return cacheSessionPool;
    }

    //------------------------------------------------------------------------------ private����

    /**
     * ��ʼ��
     */
    private void init() {

    	Properties props = (Properties)ApplicationContext.singleton().getValueByKey("cluster");
    	
        //���ü�Ⱥ���
    	String stat = props.getProperty("state");
    	if(stat.trim().toLowerCase().equals("on")){
        	this.isDbreplication = true;
    	}
    }

	public boolean isDbreplication() {
		return isDbreplication;
	}
    
}
