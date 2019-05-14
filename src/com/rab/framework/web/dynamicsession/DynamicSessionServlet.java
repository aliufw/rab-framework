package com.rab.framework.web.dynamicsession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * 
 * <P>Title: DynamicSessionServlet</P>
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
public class DynamicSessionServlet extends HttpServlet{
	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 4047868029192582507L;

	/**
	 * 初始化并启动动态缓存监控
	 */
    public void init() throws ServletException {
    	DynamicSessionManager dsm = DynamicSessionManager.singleton();

    	//session timeout时间,单位为分钟
    	String strTimeout = this.getInitParameter("session-timeout");
    	if(strTimeout != null && strTimeout.trim().length() > 0){
    		int timeout = Integer.parseInt(strTimeout.trim());
    		dsm.setTimeout(timeout);
    	}
    }
}
