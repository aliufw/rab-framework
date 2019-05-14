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
 * <P>����˵����</P>
 * <P>����MS SQLServer 2000���ݿ⡣���಻֧��2005</P>
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
	 * <p>��Բ�ͬ���ݿ�ķ�ҳ��ѯ</p>
	 *
	 * ���ݲ�ͬ���ݿ⣬���ò�ͬ��������ķ�ҳ���Խ��з�ҳ��ѯ��������ݿⲻ֧�ַ�ҳ��ѯ��
	 * ��ʹ��ͨ��ʵ�֡�ʹ���α��Ч�ʽ�ʮ�ֵ�
	 * 
	 * @param sqlStr    sql���
	 * @param params    sql����
	 * @param metaData  ��ҳ��������
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
		int rowNumE = 0; //��������
		if (pageIndex > 0) { // ���÷�ҳҳ��
			rowNumE = pageIndex * rowsPerPage; // //jdbc��1��ʼ
		} 
		
		String sql = null;

		int orderByIndex = sqlStr.toLowerCase().lastIndexOf("order by");
		if(orderByIndex>0){
			String order = sqlStr.substring(orderByIndex + 8).trim();
			/*���SqlServer  order by v.acct_name ��д����ͻ���� by Zhangbin since 2011-01-07
			* ��ʱӦ�����㷨����2������bug  1��orderByIndex �㷨����  2��order��replace����
			* 
			* lfw20110125: 
			* BaseDialectImpl.getSortSql(...)����ͬ���������������⣬��һ������
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
			logger.error("00000439: SQLServer���ݿ�������ҳ��ѯʱ��Ҫָ�������ֶΣ������޷������ȷ�ķ�ҳ���");
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
			logger.error("00000414: ��ҳ����SQL Server���ݿ��Ƿ����쳣��", e);
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
	 * <p>��ȡָ����ѯ�ķ��ؼ�¼����</p>
	 *
	 * @param sql       sql���
	 * @param params    sql����
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

			PersistenceUtils.doSqlLog(sql2, params); //�����־

			PreparedStatement pstmt = con.prepareStatement(sql2);
			pstmt = PersistenceUtils.prepareSqlParams(pstmt, params);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next()){
				count = rs.getInt(1);
			}
		}
		catch (Exception e) {
			logger.error("00000437: �ڻ�ý������Сʱ�����쳣(sqlserver)��");
			throw new BaseCheckedException("00000437", e);
		}

		return count;

	}

	/**
	 *
	 * <p>�������֣��������кţ��������ݼ�¼������</p>
	 *
	 * @param name  ���к�����
	 * @param num   һ����ȡ���кŵ�����
	 * @param dao   ȡ���к�����ĳ־ò����ݷ��ʽӿ�
	 *
	 * @return  �������кţ�����������long��ʾ
	 * @throws BaseCheckedException
	 */
	public long[] getSequence(String name, int num, PersistenceDAO dao)  throws BaseCheckedException{
		long[] ret = new long[num];

//		String sql = "{call " + name + " (?)}";

		String pname = "p_getSequenceId";

		List<StoredProcParamObj> params = new ArrayList<StoredProcParamObj>();

		//�������������ֵ
		StoredProcParamObj retParam = new StoredProcParamObj();
		retParam.setIndex(1);
		retParam.setDataType(Types.NUMERIC);
		retParam.setParamType(StoredProcParamObj.OUT);
		params.add(retParam);

		//������������к�����
		StoredProcParamObj seqName = new StoredProcParamObj();
		seqName.setIndex(2);
		seqName.setDataType(Types.VARCHAR);
		seqName.setParamType(StoredProcParamObj.IN);
		seqName.setValue(name);
		params.add(seqName);


		for(int i=0; i<num; i++){
			//((PersistenceDAOImpl)dao).beginTransaction(); //��Ҫ�������1��������־û�  //ɾ�������������Ϊ��������������˴洢������
			List<Object> lstRet = dao.storedProcedureInvoke(pname, params);
			//((PersistenceDAOImpl)dao).commitTransaction();

			//logger.debug("ȡ���кţ��Ӵ洢���̷��ص���������Ϊ �� type = " + lstRet.get(0).getClass());

			ret[i] = ((BigDecimal)lstRet.get(0)).longValue();
		}

		return ret;
	}

	/**
	 *
	 * <p>���ز�ѯ���ݿ��׼ʱ���sql���</p>
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

insert into t_sys_sequence values('SQ_SYS_COMPANY',10000,'��λ�����ţ����к�');
insert into t_sys_sequence values('SQ_SYS_EMP',10000,'��Ա���к�');
insert into t_sys_sequence values('SQ_SYS_GROUP',10000,'�û������к�');
insert into t_sys_sequence values('SQ_SYS_USER',10000,'�û����к�');




*/