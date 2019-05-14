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
 * <P>����˵��������ƽ̨��web������</P>
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
	 * ���л����
	 */
	private static final long serialVersionUID = -3385948708511637890L;

	public void init() throws ServletException {
		//��������ʼ���߳��б��Ϊ��web��������
		Map<Object,Object> map = ThreadLocalManager.getThreadLocalMap();
       	map.put("isWebappServer", "true");
       	
		StartPlatform sp = new StartPlatform();
		sp.start();		
    }
}