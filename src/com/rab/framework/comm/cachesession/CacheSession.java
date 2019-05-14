package com.rab.framework.comm.cachesession;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.rab.framework.comm.cachesession.dbreplication.DBReplicationManager;
import com.rab.framework.comm.security.BaseAuthorizationManager;
import com.rab.framework.comm.security.Ticket;


/**
 * 
 * <P>Title: CacheSession</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>专业化对象容器</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public final class CacheSession implements Serializable {
    /**
	 * 序列标识
	 */
	private static final long serialVersionUID = 6872588202671094123L;

	/**
     * 对象容器
     */
    private Map<String,Object> pool = new HashMap<String,Object>();

    /**
     * 对象种类标志, 一般是容器中对象的类名称
     */
    private String usersessionid;

    /**
     * 创建时间
     */
    private Calendar createTime;

    /**
     * 最后一次修改时间
     */
    private long lastModified;

    /**
     * 数据缓存管理对象, 回调操作
     */
    private CacheSessionManagerImpl manager;

    /**
     * 构造器
     *
     * @param usersessionid 专业化对象容器的种类属性, 一般是容器中对象的类名称
     */
    public CacheSession(String usersessionid) {
        this.usersessionid = usersessionid;
        createTime = Calendar.getInstance();
    }

    /**
     * 从容器中取出指定对象
     *
     * @param key 关键词
     * @return 返回查找到的对象
     */
    public Object getValue(String key) {
        
    	Object obj = pool.get(key);
    	
    	//如果没有,取数据库
    	if(obj==null && manager.isDbreplication()){
    		DBReplicationManager dbrm = DBReplicationManager.getInstance();
    		obj = dbrm.readDataFromDB(this.usersessionid, key);
    		pool.put(key, obj);
    	}
    	
        return obj;
    }

    /**
     * 将对象放入缓存容器中, 由客户端调用, 需要做同步
     *
     * @param key   关键词
     * @param value 数据对象
     */
    public void setValue(String key, Serializable value) {
        //设置修改时间
        lastModified = System.currentTimeMillis();

        //数据同步
        manager.synchronizeUpdate(usersessionid, key, value, lastModified);

        pool.put(key, value);
    }

    /**
     * 将对象放入缓存容器中, 由容器调用, 不需要做同步
     *
     * @param key          关键词
     * @param value        数据对象
     * @param lastModified 最后一次被用户修改的时间
     */
    public void setValue2(String key, Object value, long lastModified) {
        //检查凭证池中是否存在重复的凭证！
        if(value instanceof Ticket){
        	value = BaseAuthorizationManager.dealRepeatedPrincipal((Ticket)value);
        }

        pool.put(key, value);

        //设置修改时间
        this.lastModified = lastModified;
    }

    /**
     * 将对象从缓存容器中删除, 由客户端调用, 需要做同步
     *
     * @param key 关键词
     */
    public void remove(String key) {
        //设置修改时间
        lastModified = System.currentTimeMillis();

        //数据同步
        manager.synchronizeDel(usersessionid, key, lastModified);

        pool.remove(key);
    }

    /**
     * 将对象从缓存容器中删除, 由容器调用, 不需要做同步
     *
     * @param key          关键词
     * @param lastModified 最后一次被用户修改的时间
     */
    public void remove2(String key, long lastModified) {
        //设置修改时间
        this.lastModified = lastModified;

        pool.remove(key);
    }

    public String getUsersessionid() {
        return usersessionid;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void setManager(CacheSessionManagerImpl manager) {
        this.manager = manager;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public Map<String,Object> getAllSession() {
        return pool;
    }
}
