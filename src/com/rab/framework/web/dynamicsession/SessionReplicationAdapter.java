package com.rab.framework.web.dynamicsession;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.component.multicast.BaseMulticastObjectInfoServerAdapter;

/**
 * 
 * <P>Title: SessionReplicationAdapter</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class SessionReplicationAdapter implements BaseMulticastObjectInfoServerAdapter {
	/**
	 * 日志记录器
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

	public void dataHander(Object obj) {
		SessionReplicationWrapper wrapper = (SessionReplicationWrapper)obj;
		String sessionid = wrapper.getSessionid();
		String key = wrapper.getKey();
		Object value = wrapper.getValue();
		
		DynamicSessionManager dsm = DynamicSessionManager.singleton();
		
//		log.debug("接收包：sessionid = " + sessionid);
//		log.debug("接收包：key       = " + key);
//		log.debug("接收包：value     = " + value);

		
		dsm.setData(sessionid, key, value);
	}
}
