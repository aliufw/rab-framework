package com.rab.framework.comm.security.dataright;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.BaseAuthorizationManager;

/**
 * 
 * <P>Title: DataRightHandlerFactory</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-11</P>
 *
 */
public class DataRightHandlerFactory {
    /**
     * ��ʼ����־��¼����
     */
	private final static LogWritter logger = LogFactory.getLogger(BaseAuthorizationManager.class);

	/**
	 * <p>��������Ȩ�޴�����ʵ��</p>
	 *
	 * @param handlerName  ���������ƣ����������Ϊ�գ���ʹ��ϵͳĬ�ϵĴ�����
	 * @return
	 */
	public static DataRightHandler createDataRightHandler(String handlerName){
		
		String className = "";
		if(className == null || className.trim().length() == 0){
			className = DefaultDataRightHandlerImpl.class.getName();
		}
		else{
			className = "com.rab.framework.comm.security.dataright." + handlerName;
		}
		
		DataRightHandler handler = null;
		try {
			handler = (DataRightHandler)Class.forName(className).newInstance();
		} 
		catch (Exception e) {
			logger.error("00000509: ���� DataRightHandler ʵ��ʱ�����쳣��", e);
		} 
		
		return handler;
	}
}
