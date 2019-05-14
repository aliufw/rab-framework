package com.rab.framework.dao.dialect;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.exception.ReturnExceptionFactory;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceUtils;
import com.rab.framework.dao.StoredProcParamObj;

/**
 *
 * <P>Title: SqlServerDialect</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>处理MS SQLServer 2000数据库。此类不支持2005</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-26</P>
 *
 */
public class SqlServerDialectImpl extends BaseDialectImpl {

	private final static LogWritter logger = LogFactory
			.getLogger(SqlServerDialectImpl.class);


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
		int rowNumE = 0; //结束行数
		if (pageIndex > 0) { // 设置分页页数
			rowNumE = pageIndex * rowsPerPage; // //jdbc从1开始
		} 
		
		String sql = null;

		int orderByIndex = sqlStr.toLowerCase().lastIndexOf("order by");
		if(orderByIndex>0){
			String order = sqlStr.substring(orderByIndex + 8).trim();
			/*解决SqlServer  order by v.acct_name 的写法冲突问题 by Zhangbin since 2011-01-07
			* 临时应急，算法里有2个隐含bug  1）orderByIndex 算法问题  2）order的replace问题
			* 
			* lfw20110125: 
			* BaseDialectImpl.getSortSql(...)方法同样存在上述两问题，请一并处理
			* 
			*/
			int idx2 = order.indexOf(".");
			if(idx2>-1){
				String tp = order.substring(0,idx2+1);
				order = order.replace(tp.trim(), " ");
			}

			String orderLower = order.toLowerCase();
			if(!orderLower.endsWith("asc") && !orderLower.endsWith("desc")){
				orderLower += " asc";
			}
			
			String orderReverse = null;
			if (orderLower.endsWith(" desc")){
				orderReverse = orderLower.substring(0,orderLower.length()-4);
			}
			else{
				if (orderLower.endsWith(" asc"))
					orderReverse = orderLower.substring(0,orderLower.length()-4);
				orderReverse += " desc";
			}
			

			StringBuffer sbStr = new StringBuffer();
			sbStr.append("select * from ( ");
			sbStr.append(
					"SELECT TOP " + rowsPerPage + " * FROM (SELECT TOP " + rowNumE
							+ " * FROM ( ").append(sqlStr.substring(0,orderByIndex)).append(
					" ) aaa1 ORDER BY ").append(order).append(
					" ) aaa2 ORDER BY ").append(orderReverse);
			sbStr.append(" ) aaa3 order by ").append(order);
			sql = sbStr.toString();
		}
		else{
//			StringBuffer sbStr = new StringBuffer();
//			sbStr.append("select * from ( ");
//			sbStr.append(
//					"SELECT TOP " + rowsPerPage + " * FROM (SELECT TOP " + rowNumE
//							+ " * FROM ( ").append(sqlStr.toString()).append(
//					" ) aaa1 ").append(
//					" ) aaa2 ");
//			sbStr.append(" ) aaa3  ");
//			sql = sbStr.toString();
			logger.error("00000439: SQLServer数据库在做分页查询时需要指定排序字段，否则无法获得正确的分页结果");
			throw new BaseCheckedException("00000439");
		}

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
			logger.error("00000414: 分页检索SQL Server数据库是发生异常！", e);
			throw ReturnExceptionFactory.createReturnCheckedException("00000414", e);
			
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
			sb.append("select count(*) from (").append(sql).append(") aaa");

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
			logger.error("00000437: 在获得结果集大小时出现异常(sqlserver)！");
			throw new BaseCheckedException("00000437", e);
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
			//((PersistenceDAOImpl)dao).beginTransaction(); //重要！否则加1操作不会持久化  //删除事务操作，因为将事务操作放在了存储过程中
			List<Object> lstRet = dao.storedProcedureInvoke(pname, params);
			//((PersistenceDAOImpl)dao).commitTransaction();

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
	public String getDateQuerySQL(){
		return "select getdate()";
	}

}

/*

drop  procedure getSequenceId;

create procedure getSequenceId
@sequenceId 	numeric(16)	output,
@seq_name 	varchar(50)
as
begin
	update t_sys_sequence set seq_value=seq_value+1 where seq_name=@seq_name;
	select @sequenceId=seq_value from t_sys_sequence where seq_name=@seq_name;
end


drop table t_sys_sequence;
create table t_sys_sequence(
	seq_name	varchar(50),
	seq_value	numeric(16) default 1,
	seq_descrip	varchar(100)
);

insert into t_sys_sequence values('SQ_SYS_COMPANY',10000,'单位（部门）序列号');
insert into t_sys_sequence values('SQ_SYS_EMP',10000,'雇员序列号');
insert into t_sys_sequence values('SQ_SYS_GROUP',10000,'用户组序列号');
insert into t_sys_sequence values('SQ_SYS_USER',10000,'用户序列号');




*/