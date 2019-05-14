package com.rab.framework.domain.domainconfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <P>Title: TransactionCfg</P>
 * <P>Description: </P>
 * <P>程序说明：交易配置信息</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class TransactionCfg {
	/**
	 * 本次交易的名称标识
	 */
	private String name;
	
	/**
	 * 本次交易所执行的类文件名称
	 */
	private String className;
	
	/**
	 * 本次交易所需要的数据源名称
	 */
	private List<String> dsNames = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getDsNames() {
		return dsNames;
	}

	public void setDsNames(List<String> dsNames) {
		this.dsNames = dsNames;
	}

	public String toString() {
		String s = "TransactionCfg = {";

		s += "name = " + this.name;
		s += ", className = " + this.className;
		s += ", dsNames = (";
		for (int i = 0; i < dsNames.size(); i++) {
			s += dsNames.get(i) + ",";
		}
		s += ")}";

		return s;
	}
}
