package com.rab.framework.comm.security;

import java.io.Serializable;

public interface User extends Serializable{

	public String getUsercode();

	public String getUsername();

	public int getUserid();

	public String getUserdesc();

//	public int[] getGroupid();

	public boolean isAdmin();

	public boolean isSuperadmin();

//	public String getEmpcode();

}
