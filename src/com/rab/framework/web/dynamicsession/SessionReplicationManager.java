package com.rab.framework.web.dynamicsession;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.component.multicast.MulticastMessageSender;

/**
 * 
 * <P>Title: SessionReplicationManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>��Ⱥ���������ݸ��ƹ�����</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class SessionReplicationManager {
	/**
	 * ��־��¼��
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());
	
    /**
     * ����ʵ��
     */
	private static SessionReplicationManager manager = new SessionReplicationManager();
	
	/**
	 * ��̬session��Ⱥ�������ݽ�������������
	 */
	private String adapter = SessionReplicationAdapter.class.getName();
	
	/**
	 * ˽�й�����
	 *
	 */
	private SessionReplicationManager(){
		
	}
	
	/**
	 * �����ӿ�
	 * 
	 * @return
	 */
	public static SessionReplicationManager singleton(){
		return manager;
	}
	
	/**
	 * ���ݸ��ƽӿ�
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
			log.error("��̬session����: ���ݹ㲥����ʧ��!", e);
		}
	}
}
