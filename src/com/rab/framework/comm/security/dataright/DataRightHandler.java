package com.rab.framework.comm.security.dataright;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: DataRightHandler</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-11</P>
 *
 */
public interface DataRightHandler {

	/**
	 * 
	 * <p>获取数据权限控制SQL字符串</p>
	 *
	 * @param compCode        当前的工作环境参数：单位
	 * @param copyCode        当前的工作环境参数：帐套
	 * @param user            当前用户
	 * @param tableId         提供数据范围标识的字典表名称
	 * @param codeField       用作关键过滤字段的字典表字段名称
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
			String codeField,
			String bizTable,
			PersistenceDAO dao) throws BaseCheckedException;
}
