package com.rab.framework.comm.security;

import javax.servlet.ServletRequest;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: SecurityManager</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-9</P>
 *
 */
public interface SecurityManager {
	
	/**
	 * <p>检查用户请求URI是否是合法的授权访问</p>
	 *
	 * @param uri  待检查的URI
	 * @param request  当前的ServletRequest对象
	 * 
	 * @return  true - 有访问权限， false - 无访问权限
	 */
	public boolean securityURICheck(String uri, ServletRequest request);
	
	/**
	 * <p>检查当前用户是否拥有指定功能权限</p>
	 *
	 * @param permid  功能权限标识
	 * @param request  当前的ServletRequest对象
	 * @return  true - 有访问权限， false - 无访问权限
	 */
	public boolean securityPermidCheck(String permid, ServletRequest request);
	
	
	/**
	 * 
	 * <p>获取数据权限控制SQL字符串</p>
	 *
	 * @param compCode        当前的工作环境参数：单位
	 * @param copyCode        当前的工作环境参数：帐套
	 * @param user            当前用户
	 * @param tableId         提供数据范围标识的字典表名称
	 * @param bizTable        被过滤的业务数据表名称
	 * @param PersistenceDAO  持久层访问对象
	 * 
	 * @return SQL查询过滤字符串
	 */
	public String createDataRightFilter(
			String compCode,
			String copyCode,
			User user,
			String tableId,
			String bizTable,
			PersistenceDAO dao) throws BaseCheckedException;

}
