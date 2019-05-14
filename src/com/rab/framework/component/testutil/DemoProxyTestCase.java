package com.rab.framework.component.testutil;


/**
 * 
 * <P>Title: DemoProxyTestCase</P>
 * <P>Description: </P>
 * <P>程序说明：单元测试模板示例</P>
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
		//1. 一定要设置当前用例的 Transactionid，位置在 super.setUp(); 行之上
		this.setBlhName("TestBLH");
		
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testQueryAll() throws Exception{
//		PersistenceDAO dao = this.getDAO();

		//TODO:
		//写测试程序
		
		return ;
	}


}
