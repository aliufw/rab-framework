package com.rab.framework.web.dynamicsession;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.component.multicast.MulticastMessageSender;

/**
 * 
 * <P>Title: SessionReplicationManager</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>集群环境下数据复制管理器</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class SessionReplicationManager {
	/**
	 * 日志记录器
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());
	
    /**
     * 单例实例
     */
	private static SessionReplicationManager manager = new SessionReplicationManager();
	
	/**
	 * 动态session集群复制数据解析适配器类名
	 */
	private String adapter = SessionReplicationAdapter.class.getName();
	
	/**
	 * 私有构造器
	 *
	 */
	private SessionReplicationManager(){
		
	}
	
	/**
	 * 单例接口
	 * 
	 * @return
	 */
	public static SessionReplicationManager singleton(){
		return manager;
	}
	
	/**
	 * 数据复制接口
	 * 
	 * @param data 
	 */
	public void dataSend(String sessionid, String key, Object value){
		try {
			SessionReplicationWrapper wrapper = new SessionReplicationWrapper();
			wrapper.setSessionid(sessionid);
			wrapper.setKey(key);
			wrapper.setValue(value);
			
			MulticastMessageSender.singleton().dataSend(wrapper, adapter);
			
		} catch (Exception e) {
			log.error("动态session管理: 数据广播发送失败!", e);
		}
	}
}
