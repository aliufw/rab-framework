package com.rab.sys.security;

import com.rab.framework.comm.security.User;


public class UserImpl implements User {

	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = -2016075870236352318L;

	/**
	 * 用户ID
	 */
	private String usercode;
	
	/**
	 * 用户名称
	 */
	private String username;
	
	private int userid;
	
	private String userdesc;
	
//	private int[] groupid;
	
	private boolean admin;
	
	private boolean superadmin;
	
//	private String empcode;

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getUserdesc() {
		return userdesc;
	}

	public void setUserdesc(String userdesc) {
		this.userdesc = userdesc;
	}

//	public int[] getGroupid() {
//		return groupid;
//	}
//
//	public void setGroupid(int[] groupid) {
//		this.groupid = groupid;
//	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isSuperadmin() {
		return superadmin;
	}

	public void setSuperadmin(boolean superadmin) {
		this.superadmin = superadmin;
	}

//	public String getEmpcode() {
//		return empcode;
//	}
//
//	public void setEmpcode(String empcode) {
//		this.empcode = empcode;
//	}


}
