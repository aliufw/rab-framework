package com.rab.framework.comm.cachesession;

import java.util.Properties;



/**
 * 
 * <P>Title: ICacheSessionManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>���ݻ����������Ľӿ�</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public interface CacheSessionManager {
    /**
     * ����CacheSession����
     *
     * @param sessionid �û���־
     * @return �� sessionid ��Ӧ�� CacheSession ����
     */
    public CacheSession getCacheSession(String sessionid);
    
    public Properties getThreadLocal();

}