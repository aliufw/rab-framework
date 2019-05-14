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
 * <P>����˵����oracle ���ݿⷽ��</P>
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
//	 * <p>���ݿ�ķ�ҳ��ѯ</p>
//	 *
//	 * @param sqlStr      sql���
//	 * @param sqlParams   sql����
//	 * @param totalNum    ÿҳ����
//	 * @param pageNum     ҳ��
//	 * @return
//	 * @throws VHBaseCheckedException
//	 */
//	public CachedRowSet queryPageBySQL(String sqlStr, List<?> sqlParams,
//			int totalNum, int pageNum) throws VHBaseCheckedException {
//
//
//		if (pageNum >= 0) { // ���÷�ҳҳ��
//			pageNum = pageNum * totalNum; // //jdbc��1��ʼ��hibernate��0��ʼ
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
//					.toString(), sqlParams); // ����preparedStatementʵ��
//			
//			rs = ps.executeQuery(); // ִ�в�ѯ����
//			CachedRowSet rowSet = new CachedRowSet(); // ����һ��CachedRowSet����
//			rowSet.populate(rs); // ����ѯ������뵽rowSet ʵ����
//			return rowSet;
//		} 
//		catch (Exception e) {
//			logger.error("00000415: ��ҳ����Oracle���ݿ��Ƿ����쳣��", e);
//			throw new VHBaseCheckedException("00000415", e);
//		} 
//		finally {
//			try {
//				if (rs != null) {
//					rs.close(); // �رս����
//				}
//				if (ps != null) {
//					ps.close(); // �ر�preparedStatement����
//				}
//			} catch (SQLException ex1) {
//			}
//		}
//	}
//	
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
		//rowsPerPage, pageIndex
		//totalNum,    pageNum
		
		int rowNumB = 0; //��ʼ����
		int rowNumE = 0; //��������
		if (pageIndex > 0) { // ���÷�ҳҳ��
			rowNumB = (pageIndex-1) * rowsPerPage; // //jdbc��1��ʼ
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
					.toString(), params); // ����preparedStatementʵ��
			
			rs = ps.executeQuery(); // ִ�в�ѯ����
			CachedRowSet rowSet = new CachedRowSet(); // ����һ��CachedRowSet����
			rowSet.populate(rs); // ����ѯ������뵽rowSet ʵ����
			return rowSet;
		} 
		catch (Exception e) {
			logger.error("00000415: ��ҳ����Oracle���ݿ��Ƿ����쳣��", e);
			throw new BaseCheckedException("00000415", e);
		} 
		finally {
			try {
				if (rs != null) {
					rs.close(); // �رս����
				}
				if (ps != null) {
					ps.close(); // �ر�preparedStatement����
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
		StringBuffer sb = new StringBuffer();
		int count = -1;
		try {
			sb.append("select count(*) from (").append(sql).append(")");
			
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
			logger.error("00000436: �ڻ�ý������Сʱ�����쳣(oracle)��");
			throw new BaseCheckedException("00000436", e);
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
			logger.error("00000429=����Oracle���ݿ����к�(����:" + name + ", ����:" + num + ")ʱ�����쳣��", ex);
			List<String> params = new ArrayList<String>();
			params.add(name);
			params.add("" + num);
			throw new BaseCheckedException("00000429", params, ex);
		}
		
		return result;
	}

	/**
	 * 
	 * <p>���ز�ѯ���ݿ��׼ʱ���sql���</p>
	 *
	 * @return
	 */
	public String getDateQuerySQL(){
		return "SELECT SYSDATE FROM DUAL";
	}

}
