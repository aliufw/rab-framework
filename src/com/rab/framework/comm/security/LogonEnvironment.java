package com.rab.framework.comm.security;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 
 * 
 * <P>Title: LogonEnvironment</P>
 * <P>Description: </P>
 * <P>����˵�����û���¼ϵͳ��Ļ�������</P>
 * <P>���û���¼����йص�Ӧ�û�����Ϣ�����û�Ȩ�޲�ͬ</P>
 * <p>�û�Ȩ�ޣ���½�󲻿ɱ䣬����ҵ�񳡾����仯</p>
 * <p>������������½��ɱ�ģ���ҵ�񳡾���ͬ���仯</p>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-28</P>
 *
 */
public class LogonEnvironment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -999482912103516596L;

	/**
	 * ������Ϣ
	 */
	private String SOB;
	
	/**
	 * ����ID
	 */
	private String orgId;
	
	/**
	 * ��ǰҵ�����ʱ��
	 */
	private Calendar currDate;
	
	/**
	 * ��������Ϣ
	 */
	private String acctYear;
	
	/**
	 * ����ģ����Ϣ
	 */
	private String module;

	public String getSOB() {
		return SOB;
	}

	public void setSOB(String sob) {
		SOB = sob;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Calendar getCurrDate() {
		return currDate;
	}

	public void setCurrDate(Calendar currDate) {
		this.currDate = currDate;
	}

	/**
	 * @return the acctYear
	 */
	public String getAcctYear() {
		return acctYear;
	}

	/**
	 * @param acctYear the acctYear to set
	 */
	public void setAcctYear(String acctYear) {
		this.acctYear = acctYear;
	}

	/**
	 * @return the module
	 */
	public String getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}
		
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getClass().getName() + ":" + this.hashCode() + " [\r\n");
		sb.append("\tSOB = " + this.SOB + "\r\n");
		sb.append("\torgId = " + this.orgId + "\r\n");
		sb.append("\tcurrDate = ");
		sb.append(this.currDate.get(Calendar.YEAR) + "-");
		sb.append((this.currDate.get(Calendar.MONTH)+1) + "-");
		sb.append(this.currDate.get(Calendar.DAY_OF_MONTH) + " ");
		sb.append(this.currDate.get(Calendar.HOUR_OF_DAY) + ":");
		sb.append(this.currDate.get(Calendar.MINUTE)+ "\r\n");
		sb.append("\tacctYear = " + this.acctYear + "\r\n");
		sb.append("\tmodule = " + this.module + "\r\n");
		sb.append("]\r\n");
		
		return sb.toString();
	}
	
}
