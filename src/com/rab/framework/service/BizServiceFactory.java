package com.rab.framework.service;

/**
 * 
 * <P>Title: IBizServiceFactory</P>
 * <P>Description: </P>
 * <P>����˵����ҵ�����񴴽����ӿ�</P>
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
	 * ҵ�����񴴽����ӿڣ����еĴ����������ն�Ҫ����IBizServiceFacade�ӿ�ʵ��
	 * 
	 * @return
	 * @throws Exception
	 */
	public BizServiceFacade createBizServiceFacade() throws Exception;
}
