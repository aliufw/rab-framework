package com.rab.framework.dao.dialect;

import java.util.List;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: IDialect</P>
 * <P>Description: </P>
 * <P>程序说明：方言管理接口</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public interface Dialect {
	
	/**
	 * <p>设置DAO对象</p>
	 *
	 * @param dao
	 */
	public void setDao(PersistenceDAO dao);
	
//	/**
//	 * 
//	 * <p>针对不同数据库的分页查询</p>
//	 * 
//	 * 根据不同数据库，利用不同数据自身的分页特性进行分页查询。如果数据库不支持分页查询，
//	 * 将使用通用实现。使用游标的效率将十分低
//	 *
//	 * @param sqlStr      sql语句
//	 * @param sqlParams   sql参数
//	 * @param totalNum    每页条数
//	 * @param pageNum     页数
//	 * @return
//	 * @throws VHBaseCheckedException
//	 */
//	public CachedRowSet queryPageBySQL(String sqlStr, List<?> sqlParams,
//			int totalNum, int pageNum) throws VHBaseCheckedException;

	/**
	 * 
	 * <p>针对不同数据库的分页查询</p>
	 *
	 * 根据不同数据库，利用不同数据自身的分页特性进行分页查询。如果数据库不支持分页查询，
	 * 将使用通用实现。使用游标的效率将十分低
	 * 
	 * @param sqlStr    sql语句
	 * @param params    sql参数
	 * @param metaData  分页描述对象
	 * @return
	 * @throws BaseCheckedException
	 */
	public CachedRowSet queryPageBySQL(String sqlStr, List<Object> params, 
			PaginationMetaData metaData) throws BaseCheckedException;

	
	/**
	 * 
	 * <p>获取指定查询的返回记录条数</p>
	 *
	 * @param sql       sql语句
	 * @param params    sql参数
	 * @return
	 * @throws BaseCheckedException
	 */
	public int getTotalRecorder(String sql, List<Object> params) throws BaseCheckedException;
	
	/**
	 * 
	 * <p>根据名字，生成序列号，用于数据记录的主键</p>
	 *
	 * @param name  序列号名称
	 * @param num   一次性取序列号的数量
	 * @param dao   取序列号所需的持久层数据访问接口
	 * 
	 * @return  返回序列号，用数据类型long表示
	 * @throws BaseCheckedException
	 */
	public long[] getSequence(String name, int num, PersistenceDAO dao)  throws BaseCheckedException;

	
	/**
	 * 
	 * <p>返回查询数据库标准时间的sql语句</p>
	 *
	 * @return
	 */
	public abstract String getDateQuerySQL();
}
