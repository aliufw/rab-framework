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
 * <P>����˵�������Թ���ӿ�</P>
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
	 * <p>����DAO����</p>
	 *
	 * @param dao
	 */
	public void setDao(PersistenceDAO dao);
	
//	/**
//	 * 
//	 * <p>��Բ�ͬ���ݿ�ķ�ҳ��ѯ</p>
//	 * 
//	 * ���ݲ�ͬ���ݿ⣬���ò�ͬ��������ķ�ҳ���Խ��з�ҳ��ѯ��������ݿⲻ֧�ַ�ҳ��ѯ��
//	 * ��ʹ��ͨ��ʵ�֡�ʹ���α��Ч�ʽ�ʮ�ֵ�
//	 *
//	 * @param sqlStr      sql���
//	 * @param sqlParams   sql����
//	 * @param totalNum    ÿҳ����
//	 * @param pageNum     ҳ��
//	 * @return
//	 * @throws VHBaseCheckedException
//	 */
//	public CachedRowSet queryPageBySQL(String sqlStr, List<?> sqlParams,
//			int totalNum, int pageNum) throws VHBaseCheckedException;

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
			PaginationMetaData metaData) throws BaseCheckedException;

	
	/**
	 * 
	 * <p>��ȡָ����ѯ�ķ��ؼ�¼����</p>
	 *
	 * @param sql       sql���
	 * @param params    sql����
	 * @return
	 * @throws BaseCheckedException
	 */
	public int getTotalRecorder(String sql, List<Object> params) throws BaseCheckedException;
	
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
	public long[] getSequence(String name, int num, PersistenceDAO dao)  throws BaseCheckedException;

	
	/**
	 * 
	 * <p>���ز�ѯ���ݿ��׼ʱ���sql���</p>
	 *
	 * @return
	 */
	public abstract String getDateQuerySQL();
}
