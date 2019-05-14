package com.rab.framework.component.testutil;

import junit.framework.TestCase;

import org.hibernate.Transaction;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceDAOImpl;
import com.rab.framework.domain.session.DomainSession;


/**
 * 
 * <P>Title: BaseDomainTestCase</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-1</P>
 *
 */
public class BaseDomainTestCase extends TestCase {
	
	/**
	 * 日志记录对象
	 */
	protected static final LogWritter logger = LogFactory.getLogger(BaseDomainTestCase.class);

	protected DomainSession domainSession = null;
	
	/**
	 * 当前测试用例对应的transactionid
	 */
	protected String blhName = null;
	
	/**
	 * 事务控制描述对象
	 */
	private Transaction tx = null;
	
	public BaseDomainTestCase() {
		super();
	}

	protected void setUp() throws Exception {

		if(this.blhName == null){
			logger.error("请先设置被测用例的 transactionid 信息！");
			throw new Exception("请先设置被测用例的 transactionid 信息！");
		}
		
		DebugContext dc = new DebugContext();
		this.domainSession = dc.createDebugDomainSession(blhName);
		
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		
		((PersistenceDAOImpl)dao).beginTransaction();
		
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		tx.commit();
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		((PersistenceDAOImpl)dao).close();
	}


	public void setBlhName(String blhName) {
		this.blhName = blhName;
	}

	protected PersistenceDAO getDAO(){
		return this.domainSession.getPersistenceDAO();
		
	}
	protected PersistenceDAO getDAO(String dataSourceName){
		return this.domainSession.getPersistenceDAO(dataSourceName);
		
	}
}