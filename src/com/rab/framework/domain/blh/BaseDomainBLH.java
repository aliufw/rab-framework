package com.rab.framework.domain.blh;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.domain.session.DomainSession;

/**
 * 
 * <P>Title: BaseDomainBLH</P>
 * <P>Description: </P>
 * <P>程序说明：BLH基类</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public abstract class BaseDomainBLH {

	/**
	 * 本次交互所使用的Session对象
	 */
	protected DomainSession domainSession;

	protected final LogWritter logger = LogFactory.getLogger(this.getClass());

	public void setDomainSession(DomainSession domainSession) {
		this.domainSession = domainSession;
	}
		
}