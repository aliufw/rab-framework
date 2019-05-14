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
 * <P>����˵����</P>
 * <P>רҵ����������</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public final class CacheSession implements Serializable {
    /**
	 * ���б�ʶ
	 */
	private static final long serialVersionUID = 6872588202671094123L;

	/**
     * ��������
     */
    private Map<String,Object> pool = new HashMap<String,Object>();

    /**
     * ���������־, һ���������ж����������
     */
    private String usersessionid;

    /**
     * ����ʱ��
     */
    private Calendar createTime;

    /**
     * ���һ���޸�ʱ��
     */
    private long lastModified;

    /**
     * ���ݻ���������, �ص�����
     */
    private CacheSessionManagerImpl manager;

    /**
     * ������
     *
     * @param usersessionid רҵ��������������������, һ���������ж����������
     */
    public CacheSession(String usersessionid) {
        this.usersessionid = usersessionid;
        createTime = Calendar.getInstance();
    }

    /**
     * ��������ȡ��ָ������
     *
     * @param key �ؼ���
     * @return ���ز��ҵ��Ķ���
     */
    public Object getValue(String key) {
        
    	Object obj = pool.get(key);
    	
    	//���û��,ȡ���ݿ�
    	if(obj==null && manager.isDbreplication()){
    		DBReplicationManager dbrm = DBReplicationManager.getInstance();
    		obj = dbrm.readDataFromDB(this.usersessionid, key);
    		pool.put(key, obj);
    	}
    	
        return obj;
    }

    /**
     * ��������뻺��������, �ɿͻ��˵���, ��Ҫ��ͬ��
     *
     * @param key   �ؼ���
     * @param value ���ݶ���
     */
    public void setValue(String key, Serializable value) {
        //�����޸�ʱ��
        lastModified = System.currentTimeMillis();

        //����ͬ��
        manager.synchronizeUpdate(usersessionid, key, value, lastModified);

        pool.put(key, value);
    }

    /**
     * ��������뻺��������, ����������, ����Ҫ��ͬ��
     *
     * @param key          �ؼ���
     * @param value        ���ݶ���
     * @param lastModified ���һ�α��û��޸ĵ�ʱ��
     */
    public void setValue2(String key, Object value, long lastModified) {
        //���ƾ֤�����Ƿ�����ظ���ƾ֤��
        if(value instanceof Ticket){
        	value = BaseAuthorizationManager.dealRepeatedPrincipal((Ticket)value);
        }

        pool.put(key, value);

        //�����޸�ʱ��
        this.lastModified = lastModified;
    }

    /**
     * ������ӻ���������ɾ��, �ɿͻ��˵���, ��Ҫ��ͬ��
     *
     * @param key �ؼ���
     */
    public void remove(String key) {
        //�����޸�ʱ��
        lastModified = System.currentTimeMillis();

        //����ͬ��
        manager.synchronizeDel(usersessionid, key, lastModified);

        pool.remove(key);
    }

    /**
     * ������ӻ���������ɾ��, ����������, ����Ҫ��ͬ��
     *
     * @param key          �ؼ���
     * @param lastModified ���һ�α��û��޸ĵ�ʱ��
     */
    public void remove2(String key, long lastModified) {
        //�����޸�ʱ��
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
