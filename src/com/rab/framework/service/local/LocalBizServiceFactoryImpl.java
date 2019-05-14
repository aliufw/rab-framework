package com.rab.framework.service.local;

import com.rab.framework.service.BizServiceFacade;
import com.rab.framework.service.BizServiceFactory;

/**
 * 
 * <P>Title: LocalBizServiceFactory</P>
 * <P>Description: </P>
 * <P>程序说明：本地模式的服务接口创建器</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-9</P>
 *
 */
public class LocalBizServiceFactoryImpl implements BizServiceFactory {

	/**
	 * 单例的静态实例
	 */
	private static LocalBizServiceFactoryImpl factory = new LocalBizServiceFactoryImpl();
	
	/**
	 * 私有构造器，阻止从外部创建实例
	 */
	private LocalBizServiceFactoryImpl(){
		
	}
	
	/**
	 * 单例接口，返回EJBBizServiceFactory实例
	 * 
	 * @return
	 */
	public static LocalBizServiceFactoryImpl singleton(){
		return factory;
	}
	
	
	/**
	 * 业务层服务创建器接口，返回IBizServiceFacade接口实例
	 * 
	 * @return
	 * @throws Exception
	 */
	public BizServiceFacade createBizServiceFacade() throws Exception{
		BizServiceFacade facade = LocalServiceFacadeBean.getInstance();
		
		return facade;
	}

}
