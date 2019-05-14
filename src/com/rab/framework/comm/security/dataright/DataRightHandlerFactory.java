package com.rab.framework.comm.security.dataright;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.BaseAuthorizationManager;

/**
 * 
 * <P>Title: DataRightHandlerFactory</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
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
     * 初始化日志记录对象
     */
	private final static LogWritter logger = LogFactory.getLogger(BaseAuthorizationManager.class);

	/**
	 * <p>创建数据权限处理类实例</p>
	 *
	 * @param handlerName  处理类名称，如果该名称为空，则使用系统默认的处理类
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
			logger.error("00000509: 创建 DataRightHandler 实例时出现异常！", e);
		} 
		
		return handler;
	}
}
