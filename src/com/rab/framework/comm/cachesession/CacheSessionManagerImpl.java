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
 * <P>程序说明：</P>
 * <P>对象缓存管理器</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class CacheSessionManagerImpl implements CacheSessionManager {
    /**
     * 日志记录对象
     */
	private final static LogWritter logger = LogFactory.getLogger(CacheSessionManagerImpl.class);
    
    //------------------------------------------------------------------------------ 属性定义

	/**
     * 是否使用数据库作为中介来进行数据复制
     */
    private boolean isDbreplication = false;
    
    /**
     * 缓存对象池, 其中包含了多个专业化的对象容器(SpecialCacheContainer)
     */
    private Map<String, CacheSession> cacheSessionPool = new HashMap<String, CacheSession>();

    /**
     * 静态实例
     */
    private static CacheSessionManager scm = null;

    private static Map<String, Properties> threadLocal = new HashMap<String, Properties>();
    //------------------------------------------------------------------------------ 构造器

    /**
     * 私有构造器
     */
    private CacheSessionManagerImpl() {
        init();

    }


    //------------------------------------------------------------------------------ public方法


    /**
     * 单例方法
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
     * 从缓存池中取出待处理的特定缓存容器对象
     * 如果对应的特定缓存容器对象不存在,则创建一个新特定缓存容器对象并缓存
     *
     * @param sessionid 用户编号
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
    //------------------------------------------------------------------------------ protected方法


    /**
     * 数据广播发送, 添加或更新数据
     *
     * @param sessionid 用户标识
     * @param key       数据关键词
     * @param data      可序列化的数据对象
     */
    protected void synchronizeUpdate(String sessionid, String key, Serializable data, long lastModified) {
              
		if(this.isDbreplication){
			DBReplicationManager dbrm = DBReplicationManager.getInstance();
			dbrm.saveDataToDB(sessionid, key, data, lastModified);
		}
    }

    /**
     * 数据广播发送, 删除数据
     *
     * @param sessionid 用户标识
     * @param key       数据关键词
     */
    protected void synchronizeDel(String sessionid, String key, long lastModified) {
    	
    	if(this.isDbreplication){
    		DBReplicationManager dbrm = DBReplicationManager.getInstance();
    		dbrm.deleteDataFromDB(sessionid, key);
    	}
    }

    /**
     * 从缓存中删除超时的缓存对象
     *
     * @param date
     */
    public void cacheSessionTimeout(Calendar date) {
        //从内存中删除
        Iterator<String> iter = cacheSessionPool.keySet().iterator();
        List<String> keyList = new ArrayList<String>();
        while (iter.hasNext()){
        	keyList.add(iter.next());
        }
        	
        for(int i=0; i<keyList.size(); i++) {
            Object key = keyList.get(i);
            CacheSession session = cacheSessionPool.get(key);
            if (session.getCreateTime().before(date)) {
                //TODO: 保留哪些特定的缓存对象????
                cacheSessionPool.remove(key);  //从内存中删除
            }
        }
        
        //从数据库中删除
        if(isDbreplication){
        	DBReplicationManager dbrm = DBReplicationManager.getInstance();
        	logger.info("开始从数据库中清除session数据...");
        	dbrm.deleteAllDataFromDB(date.getTimeInMillis());
        	logger.info("从数据库中清除session数据完毕!!!");
        }
    }

    public Map<String, CacheSession> getCacheSessionPool() {
        return cacheSessionPool;
    }

    //------------------------------------------------------------------------------ private方法

    /**
     * 初始化
     */
    private void init() {

    	Properties props = (Properties)ApplicationContext.singleton().getValueByKey("cluster");
    	
        //设置集群标记
    	String stat = props.getProperty("state");
    	if(stat.trim().toLowerCase().equals("on")){
        	this.isDbreplication = true;
    	}
    }

	public boolean isDbreplication() {
		return isDbreplication;
	}
    
}
