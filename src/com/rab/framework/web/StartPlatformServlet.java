package com.rab.framework.web;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.rab.framework.comm.threadlocal.ThreadLocalManager;
import com.rab.framework.domain.server.StartPlatform;

/**
 * 
 * <P>Title: StartPlatformServlet</P>
 * <P>Description: </P>
 * <P>程序说明：启动平台的web框架入口</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class StartPlatformServlet extends HttpServlet {
    /**
	 * 序列化编号
	 */
	private static final long serialVersionUID = -3385948708511637890L;

	public void init() throws ServletException {
		//在启动初始化线程中标记为从web容器启动
		Map<Object,Object> map = ThreadLocalManager.getThreadLocalMap();
       	map.put("isWebappServer", "true");
       	
		StartPlatform sp = new StartPlatform();
		sp.start();		
    }
}