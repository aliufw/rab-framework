package com.rab.framework.comm.security;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 
 * 
 * <P>Title: LogonEnvironment</P>
 * <P>Description: </P>
 * <P>程序说明：用户登录系统后的环境变量</P>
 * <P>和用户登录身份有关的应用环境信息，和用户权限不同</P>
 * <p>用户权限：登陆后不可变，不随业务场景而变化</p>
 * <p>环境变量：登陆后可变的，随业务场景不同而变化</p>
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
	 * 帐套信息
	 */
	private String SOB;
	
	/**
	 * 机构ID
	 */
	private String orgId;
	
	/**
	 * 当前业务操作时间
	 */
	private Calendar currDate;
	
	/**
	 * 会计年度信息
	 */
	private String acctYear;
	
	/**
	 * 功能模块信息
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
