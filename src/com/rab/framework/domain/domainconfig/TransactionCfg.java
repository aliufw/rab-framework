package com.rab.framework.domain.domainconfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <P>Title: TransactionCfg</P>
 * <P>Description: </P>
 * <P>����˵��������������Ϣ</P>
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
	 * ���ν��׵����Ʊ�ʶ
	 */
	private String name;
	
	/**
	 * ���ν�����ִ�е����ļ�����
	 */
	private String className;
	
	/**
	 * ���ν�������Ҫ������Դ����
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
