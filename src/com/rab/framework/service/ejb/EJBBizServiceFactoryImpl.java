package com.rab.framework.service.ejb;

import com.rab.framework.comm.locator.EJBLocator;
import com.rab.framework.service.BizServiceFacade;
import com.rab.framework.service.BizServiceFactory;

/**
 * 
 * <P>Title: EJBBizServiceFactory</P>
 * <P>Description: </P>
 * <P>����˵����EJBģʽ�ķ���ӿڴ�����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-9</P>
 *
 */
public class EJBBizServiceFactoryImpl implements BizServiceFactory {

	/**
	 * �����ľ�̬ʵ��
	 */
	private static EJBBizServiceFactoryImpl factory = new EJBBizServiceFactoryImpl();
	
	/**
	 * ˽�й���������ֹ���ⲿ����ʵ��
	 */
	private EJBBizServiceFactoryImpl(){
		
	}
	
	/**
	 * �����ӿڣ�����EJBBizServiceFactoryʵ��
	 * 
	 * @return
	 */
	public static EJBBizServiceFactoryImpl singleton(){
		return factory;
	}
	
	/**
	 * ҵ�����񴴽����ӿڣ�����IBizServiceFacade�ӿ�ʵ��
	 * 
	 * @return
	 * @throws Exception
	 */
	public BizServiceFacade createBizServiceFacade() throws Exception{
		BizServiceFacade facade = null;
		
		try {
			facade = (BizServiceFacade)EJBLocator.singleton().getDomainFacade("VHFacadeBean");
		} catch (Exception e) {
			throw e;
		}
		
		return facade;
	}

}
