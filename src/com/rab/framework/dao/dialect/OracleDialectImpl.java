package com.rab.framework.dao.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceUtils;

/**
 * 
 * <P>Title: OracleDialect</P>
 * <P>Description: </P>
 * <P>程序说明：oracle 数据库方言</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class OracleDialectImpl extends BaseDialectImpl {

	private final static LogWritter logger = LogFactory.getLogger(OracleDialectImpl.class);

//	/**
//	 * 
//	 * <p>数据库的分页查询</p>
//	 *
//	 * @param sqlStr      sql语句
//	 * @param sqlParams   sql参数
//	 * @param totalNum    每页条数
//	 * @param pageNum     页数
//	 * @return
//	 * @throws VHBaseCheckedException
//	 */
//	public CachedRowSet queryPageBySQL(String sqlStr, List<?> sqlParams,
//			int totalNum, int pageNum) throws VHBaseCheckedException {
//
//
//		if (pageNum >= 0) { // 设置分页页数
//			pageNum = pageNum * totalNum; // //jdbc从1开始，hibernate从0开始
//		} 
//		else {
//			pageNum = 0;
//		}
//
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		StringBuffer sbStr = new StringBuffer();
//		if (totalNum < 0) {
//			sbStr.append(sqlStr);
//		} else {
//			totalNum += pageNum;
//			sbStr.append(
//					"select * from ( select row_.*, rownum rownum_ from ( ")
//					.append(sqlStr).append(" ) row_ where rownum <= ").append(
//							totalNum).append(" ) where rownum_ > ").append(
//							pageNum);
//		}
//		
//		try {
//			
//			PersistenceUtils.doSqlLog("SQL=" + sbStr, sqlParams);
//			ps = preparedStatementCreate(this.getDao().getConnection(), sbStr
//					.toString(), sqlParams); // 生成preparedStatement实例
//			
//			rs = ps.executeQuery(); // 执行查询操作
//			CachedRowSet rowSet = new CachedRowSet(); // 生成一个CachedRowSet对象
//			rowSet.populate(rs); // 将查询结果加入到rowSet 实例中
//			return rowSet;
//		} 
//		catch (Exception e) {
//			logger.error("00000415: 分页检索Oracle数据库是发生异常！", e);
//			throw new VHBaseCheckedException("00000415", e);
//		} 
//		finally {
//			try {
//				if (rs != null) {
//					rs.close(); // 关闭结果集
//				}
//				if (ps != null) {
//					ps.close(); // 关闭preparedStatement对象
//				}
//			} catch (SQLException ex1) {
//			}
//		}
//	}
//	
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
			PaginationMetaData metaData) throws BaseCheckedException{

		String sortFlag = metaData.getSortFlag();
		String sortFieldName = metaData.getSortFieldName();
		if (sortFlag!=null && sortFieldName != null && sortFieldName.trim().length()>0) {
			sqlStr = getSortSql(sqlStr, sortFlag, sortFieldName);
		}

		int pageIndex = metaData.getPageIndex();
		int rowsPerPage = metaData.getRowsPerPage();
		//rowsPerPage, pageIndex
		//totalNum,    pageNum
		
		int rowNumB = 0; //开始行数
		int rowNumE = 0; //结束行数
		if (pageIndex > 0) { // 设置分页页数
			rowNumB = (pageIndex-1) * rowsPerPage; // //jdbc从1开始
		} 

		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sbStr = new StringBuffer();
		rowNumE = rowNumB + rowsPerPage;
		sbStr.append(
				"select * from ( select row_.*, rownum rownum_ from ( ")
				.append(sqlStr).append(" ) row_ where rownum <= ").append(
						rowNumE).append(" ) where rownum_ > ").append(
								rowNumB);
		
		try {
			
			PersistenceUtils.doSqlLog("SQL=" + sbStr, params);
			ps = preparedStatementCreate(this.getDao().getConnection(), sbStr
					.toString(), params); // 生成preparedStatement实例
			
			rs = ps.executeQuery(); // 执行查询操作
			CachedRowSet rowSet = new CachedRowSet(); // 生成一个CachedRowSet对象
			rowSet.populate(rs); // 将查询结果加入到rowSet 实例中
			return rowSet;
		} 
		catch (Exception e) {
			logger.error("00000415: 分页检索Oracle数据库是发生异常！", e);
			throw new BaseCheckedException("00000415", e);
		} 
		finally {
			try {
				if (rs != null) {
					rs.close(); // 关闭结果集
				}
				if (ps != null) {
					ps.close(); // 关闭preparedStatement对象
				}
			} catch (SQLException ex1) {
			}
		}
	}
	
	/**
	 * 
	 * <p>获取指定查询的返回记录条数</p>
	 *
	 * @param sql       sql语句
	 * @param params    sql参数
	 * @return
	 * @throws BaseCheckedException
	 */
	public int getTotalRecorder(String sql, List<Object> params) throws BaseCheckedException{
		StringBuffer sb = new StringBuffer();
		int count = -1;
		try {
			sb.append("select count(*) from (").append(sql).append(")");
			
			String sql2 = sb.toString();
			
			Connection con = this.getDao().getConnection();
			
			PersistenceUtils.doSqlLog(sql2, params); //输出日志
			
			PreparedStatement pstmt = con.prepareStatement(sql2);
			pstmt = PersistenceUtils.prepareSqlParams(pstmt, params);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()){
				count = rs.getInt(1);
			}
		} 
		catch (Exception e) {
			logger.error("00000436: 在获得结果集大小时出现异常(oracle)！");
			throw new BaseCheckedException("00000436", e);
		}
		
		return count;

	}
	
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
	public long[] getSequence(String name, int num, PersistenceDAO dao) throws BaseCheckedException{
		String sql = "SELECT " + name + ".NEXTVAL FROM DUAL";
		
		long[] result = new long[num];
		
		try {
			for(int i=0; i<num; i++){
				CachedRowSet rowset = dao.queryToCachedRowSetBySQL(sql, null);
				if (rowset.first()) {
					result[i] = rowset.getLong(1);
			    }
			}
		} catch (SQLException ex) {
			logger.error("00000429=生成Oracle数据库序列号(名称:" + name + ", 数量:" + num + ")时出现异常！", ex);
			List<String> params = new ArrayList<String>();
			params.add(name);
			params.add("" + num);
			throw new BaseCheckedException("00000429", params, ex);
		}
		
		return result;
	}

	/**
	 * 
	 * <p>返回查询数据库标准时间的sql语句</p>
	 *
	 * @return
	 */
	public String getDateQuerySQL(){
		return "SELECT SYSDATE FROM DUAL";
	}

}
