package com.rab.framework.component.testutil;


/**
 * 
 * <P>Title: DemoProxyTestCase</P>
 * <P>Description: </P>
 * <P>����˵������Ԫ����ģ��ʾ��</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class DemoProxyTestCase extends BaseDomainTestCase {
	protected void setUp() throws Exception {
		//1. һ��Ҫ���õ�ǰ������ Transactionid��λ���� super.setUp(); ��֮��
		this.setBlhName("TestBLH");
		
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testQueryAll() throws Exception{
//		PersistenceDAO dao = this.getDAO();

		//TODO:
		//д���Գ���
		
		return ;
	}


}
