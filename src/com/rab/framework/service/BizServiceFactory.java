package com.rab.framework.service;

/**
 * 
 * <P>Title: IBizServiceFactory</P>
 * <P>Description: </P>
 * <P>程序说明：业务层服务创建器接口</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-9</P>
 *
 */
public interface BizServiceFactory {
	/**
	 * 业务层服务创建器接口，所有的创建器，最终都要返回IBizServiceFacade接口实例
	 * 
	 * @return
	 * @throws Exception
	 */
	public BizServiceFacade createBizServiceFacade() throws Exception;
}
