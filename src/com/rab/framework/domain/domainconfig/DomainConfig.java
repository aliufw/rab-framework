package com.rab.framework.domain.domainconfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <P>Title: DomainConfig</P>
 * <P>Description: </P>
 * <P>程序说明：业务层配置描述对象</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class DomainConfig {
	/**
	 * 模块名称
	 */
	private String modelName;
	
	/**
	 * 缺省的数据源名称 
	 */
	private String defaultDataSourceName;

	
	/**
	 * 交易配置信息，内放TransactionCfg实例
	 */
	private Map<String,TransactionCfg> transactions = new HashMap<String,TransactionCfg>();


	public String getModelName() {
		return modelName;
	}


	public void setModelName(String modelName) {
		this.modelName = modelName;
	}


	public String getDefaultDataSourceName() {
		return defaultDataSourceName;
	}

	public TransactionCfg getTransactionCfg(String transactionid){
		return (TransactionCfg)this.transactions.get(transactionid);
	}

	public void setDefaultDataSourceName(String defaultDataSourceName) {
		this.defaultDataSourceName = defaultDataSourceName;
	}


	public Map<String,TransactionCfg> getTransactions() {
		return transactions;
	}


	public void setTransactions(Map<String,TransactionCfg> transactions) {
		this.transactions = transactions;
	}

	
	
}
