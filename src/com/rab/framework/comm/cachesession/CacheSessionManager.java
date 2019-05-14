package com.rab.framework.comm.cachesession;

import java.util.Properties;



/**
 * 
 * <P>Title: ICacheSessionManager</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>数据缓存管理组件的接口</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public interface CacheSessionManager {
    /**
     * 返回CacheSession对象
     *
     * @param sessionid 用户标志
     * @return 与 sessionid 对应的 CacheSession 对象
     */
    public CacheSession getCacheSession(String sessionid);
    
    public Properties getThreadLocal();

}