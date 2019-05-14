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
		int rowNumB = 0; //��ʼ����
		if (pageIndex > 0) { // ���÷�ҳҳ��
			rowNumB = (pageIndex-1) * rowsPerPage; // //jdbc��1��ʼ
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
			logger.error("00000416: ��ҳ����MySQL���ݿ��Ƿ����쳣��", e);
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
			
			String alias = "alias_" +  RandomStringUtils.randomAlphabetic(5);
			sb.append("select count(*) from (").append(sql).append(") " ).append(alias);

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
			logger.error("00000437: �ڻ�ý������Сʱ�����쳣(MySQL)��");
			throw new BaseCheckedException("00000438", e);
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
			List<Object> lstRet = dao.storedProcedureInvoke(pname, params);

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
	public String getDateQuerySQL() {
		return "select now() as DATETIME";
	}

}
