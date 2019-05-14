package com.rab.framework.dao.dialect;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceUtils;
import com.rab.framework.dao.StoredProcParamObj;

public class MySQLDialectImpl extends BaseDialectImpl {

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
		int rowNumB = 0; //开始行数
		if (pageIndex > 0) { // 设置分页页数
			rowNumB = (pageIndex-1) * rowsPerPage; // //jdbc从1开始
		} 
		
		String sql = null;
		
		StringBuffer sbStr = new StringBuffer();
		String alias1 = "alias1_" +  RandomStringUtils.randomAlphabetic(5);
		
		sbStr.append("select * from (");
		sbStr.append(sqlStr);
		sbStr.append(") " + alias1 + " limit " + rowNumB + "," + rowsPerPage);
		
		sql = sbStr.toString();

		PreparedStatement ps = null;
		ResultSet rs = null;
		CachedRowSet rowSet;
		try {
			PersistenceUtils.doSqlLog("SQL=" + sql, params);
			ps = preparedStatementCreate(this.getDao().getConnection(), sql, params);
			rs = ps.executeQuery();
			rowSet = new CachedRowSet();
			rowSet.populate(rs);
			return rowSet;
		} catch (Exception e) {
			logger.error("00000416: 分页检索MySQL数据库是发生异常！", e);
			throw new BaseCheckedException("00000416", e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
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

		int count = -1;
		try {
			int orderByIndex = sql.toLowerCase().lastIndexOf("order by");
			if(orderByIndex > 0){
				sql = sql.substring(0, orderByIndex).trim();
			}

			StringBuffer sb = new StringBuffer();
			
			String alias = "alias_" +  RandomStringUtils.randomAlphabetic(5);
			sb.append("select count(*) from (").append(sql).append(") " ).append(alias);

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
			logger.error("00000437: 在获得结果集大小时出现异常(MySQL)！");
			throw new BaseCheckedException("00000438", e);
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
	public long[] getSequence(String name, int num, PersistenceDAO dao)  throws BaseCheckedException{
		long[] ret = new long[num];

//		String sql = "{call " + name + " (?)}";

		String pname = "p_getSequenceId";

		List<StoredProcParamObj> params = new ArrayList<StoredProcParamObj>();

		//定义参数：返回值
		StoredProcParamObj retParam = new StoredProcParamObj();
		retParam.setIndex(1);
		retParam.setDataType(Types.NUMERIC);
		retParam.setParamType(StoredProcParamObj.OUT);
		params.add(retParam);

		//定义参数：序列号名称
		StoredProcParamObj seqName = new StoredProcParamObj();
		seqName.setIndex(2);
		seqName.setDataType(Types.VARCHAR);
		seqName.setParamType(StoredProcParamObj.IN);
		seqName.setValue(name);
		params.add(seqName);


		for(int i=0; i<num; i++){
			List<Object> lstRet = dao.storedProcedureInvoke(pname, params);

			//logger.debug("取序列号，从存储过程返回的数据类型为 ： type = " + lstRet.get(0).getClass());
			ret[i] = ((BigDecimal)lstRet.get(0)).longValue();
		}

		return ret;
	}

	/**
	 *
	 * <p>返回查询数据库标准时间的sql语句</p>
	 *
	 * @return
	 */
	public String getDateQuerySQL() {
		return "select now() as DATETIME";
	}

}
