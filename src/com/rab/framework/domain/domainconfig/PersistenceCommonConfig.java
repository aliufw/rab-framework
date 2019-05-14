package com.rab.framework.domain.domainconfig;

import java.util.Properties;

/**
 * 
 * <P>Title: PersistenceCommonConfig</P>
 * <P>Description: </P>
 * <P>程序说明：持久层通用配置信息</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class PersistenceCommonConfig {
	/**
	 * <session-factory name="persistence.ds001"/>
	 */
	private String sessionFactoryName;
	
	/**
	 * <session-factory-config/> 
	 */
	private Properties sessionFactoryconfig;

	public String getSessionFactoryName() {
		return sessionFactoryName;
	}

	public void setSessionFactoryName(String sessionFactoryName) {
		this.sessionFactoryName = sessionFactoryName;
	}

	public Properties getSessionFactoryconfig() {
		return sessionFactoryconfig;
	}

	public void setSessionFactoryconfig(Properties sessionFactoryconfig) {
		this.sessionFactoryconfig = sessionFactoryconfig;
	}

}
