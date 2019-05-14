package com.rab.framework.service.local;

import com.rab.framework.service.BizServiceFacade;
import com.rab.framework.service.BizServiceFactory;

/**
 * 
 * <P>Title: LocalBizServiceFactory</P>
 * <P>Description: </P>
 * <P>����˵��������ģʽ�ķ���ӿڴ�����</P>
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
	 * �����ľ�̬ʵ��
	 */
	private static LocalBizServiceFactoryImpl factory = new LocalBizServiceFactoryImpl();
	
	/**
	 * ˽�й���������ֹ���ⲿ����ʵ��
	 */
	private LocalBizServiceFactoryImpl(){
		
	}
	
	/**
	 * �����ӿڣ�����EJBBizServiceFactoryʵ��
	 * 
	 * @return
	 */
	public static LocalBizServiceFactoryImpl singleton(){
		return factory;
	}
	
	
	/**
	 * ҵ�����񴴽����ӿڣ�����IBizServiceFacade�ӿ�ʵ��
	 * 
	 * @return
	 * @throws Exception
	 */
	public BizServiceFacade createBizServiceFacade() throws Exception{
		BizServiceFacade facade = LocalServiceFacadeBean.getInstance();
		
		return facade;
	}

}
