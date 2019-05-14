package com.rab.framework.dao;

import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.domain.po.PersistenceObject;
import com.rab.framework.domain.session.DomainSession;

/**
 * 
 * <P>Title: PersistenceDAO</P>
 * <P>Description: </P>
 * <P>����˵�������ݿ���ʽӿ�</P>
 * 
 * <P>���ݲ���������������</P>
 * <li>������������insert��ͷ</li>
 * <li>ɾ����������delete��ͷ</li>
 * <li>�ģ���������upate��ͷ</li>
 * <li>�飺��������query��ͷ</li>
 * 
 * <li>����������������Batch�ַ���</li>
 * 
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-12</P>
 *
 */
public interface PersistenceDAO {

	//--------------------------------------------------------------------------��01�ࣺ��������ӿ�
	
	/**
	 * ��õ�ǰ�������Ĭ�����ݿ�����
	 * 
	 * @return Connection
	 */
	public Connection getConnection();

	/**
	 * ��õ�ǰ���׵�DomainSession
	 * 
	 * @return com.rab.framework.domain.session.DomainSession ʵ������
	 */
	public DomainSession getDomainSession();
	
	
//	/**
//	 * <p>��������</p>
//	 *
//	 */
//	public void beginTransaction();
//	
//	/**
//	 * <p>�ع�����</p>
//	 *
//	 */
//	public void rollbackTransaction();
//	
//	/**
//	 * 
//	 * <p>�ύ����</p>
//	 *
//	 */
//	public void commitTransaction();
	
//	/**
//	 * <p>�رճ־ò�ӿ�</p>
//	 *
//	 */
//	public void close();
	
	
	//--------------------------------------------------------------------------��02�ࣺinsert�����ӿ�

	/**
	 * <p>�����insert������ÿ������һ����¼</p>
	 * <p>����Ҫ��</p>
	 * 
	 * <p>1. �÷�����ִ��Insert������ʱ�򣬲����BO��Ӧ�ļ����������κβ�����
	 * �����BO�����д���һ���������ӱ������ô�÷���������ӱ�����κ������ϵĲ�����</p>
	 * <p>2. �ڽ���HBMӳ���ļ���ʱ�򣬶�������cascade����һ�����ó�Ϊnone</p>
	 * 
	 * @param bo ҵ�����ʵ�����ڲ������д���������¼���������
	 * 
	 * @return   �����´�����¼��������ֵ
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public Serializable insertSingleRow(PersistenceObject bo) throws BaseCheckedException;

	/**
	 * 
	 * <p>������insert����</p>
	 * <p>����Ҫ��</p>
	 * 
	 * <p>1. �÷�����ִ��Insert������ʱ�򣬲����BO��Ӧ�ļ����������κβ�����
	 * �����BO�����д���һ���������ӱ������ô�÷���������ӱ�����κ������ϵĲ�����</p>
	 * <p>2. �ڽ���HBMӳ���ļ���ʱ�򣬶�������cascade����һ�����ó�Ϊnone</p>
	 *
	 * @param listBO ҵ�����ʵ�����ڲ������д���������¼���������
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public void insertBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException;
	

	//--------------------------------------------------------------------------��03�ࣺupdate�����ӿ�

	/**
	 * <p>�����update������ÿ�θ���һ����¼</p>
	 * <p>����Ҫ��</p>
	 * 
	 * <p>1.�������û��ʹ�ö�Ӧ��setter�������и��£��򲻻ᱻִ�и��¶�����</p>
	 * <p>2.�����BO����һ�����Ե�setter������û�б����ã���ִ��update������</p>
	 * <p>3.���һ��BO�����Ա�ִ��������setter����������¶���ʹ�ú��ߣ�</p>
	 * 
	 * @param bo ҵ�����ʵ�����ڲ������д���������¼���������
	 * 
	 * @return int �����˶�������¼                        
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public int updateSingleRow(PersistenceObject bo) throws BaseCheckedException;

	/**
	 * <p>�����update������ÿ�θ��¶�����¼</p>
	 * <p>����Ҫ��</p>
	 * 
	 * <p>1.�������û��ʹ�ö�Ӧ��setter�������и��£��򲻻ᱻִ�и��¶�����</p>
	 * <p>2.�����BO����һ�����Ե�setter������û�б����ã���ִ��update������</p>
	 * <p>3.���һ��BO�����Ա�ִ��������setter����������¶���ʹ�ú��ߣ�</p>
	 * 
	 * @param listBO ҵ�����ʵ�����ڲ������д���������¼���������
	 * 
	 * @return int �����˶�������¼                        
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public int updateBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException;

	
	/**
	 * 
	 * <p>����ָ����sql�������ݿ��¼</p>
	 *
	 * @param sql     ִ�и��²�����sql���
	 * @param params  ִ�и��²����Ĳ���
	 * @return        �����˶�������¼
	 * 
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public int updateBatchRowBySQL(String sql, List<Object> params) throws BaseCheckedException;

	/**
	 * 
	 * <p>����ָ����sqlKey�������ݿ��¼</p>
	 *
	 * @param key     ִ�и��²�����sql����key
	 * @param params  ִ�и��²����Ĳ���
	 * @return        �����˶�������¼
	 * 
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public int updateBatchRowByKey(String key, List<Object> params) throws BaseCheckedException;
	
	
//	/**
//	 * 
//	 * <p>����ָ��where�������������ݱ�</p>
//	 * 
//	 * @param bo �����µ�BO�����ֵ�������͵�BO���󶼱�����Ϊ��BO������Ӧ������ֵ��
//	 * @param sqlWhere ����������SQL��ʽ��������where���������� name = ?
//	 * @param params   ���where�����еĲ���ֵ
//	 *                        
//	 * @return int �����˶�������¼                        
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public int updateBatchRowByConditionBySQL(BusinessObject bo,
//			String sqlWhere, List<Object> params) throws VHBaseCheckedException;

//	/**
//	 * 
//	 * <p>����ָ��where�������������ݱ�</p>
//	 * <p>sqlValue������������һЩ���ӵĸ���ֵ������: age �� age �� 1</p>
//	 *
//	 * @param bo        �����µ�BO�����ֵ�������͵�BO���󶼱�����Ϊ��BO������Ӧ������ֵ��
//	 * @param setValue  String ���ӵĸ���ֵ���ʽ
//	 * @param sqlWhere  ����������SQL��ʽ��������where���������� name = ?
//	 * @param params    ���where�����еĲ���ֵ
//	 * 
//	 * @return int �����˶�������¼                        
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public int updateBatchRowByConditionBySQL(BusinessObject bo,
//			String setValue, String sqlWhere, List<Object> params) throws VHBaseCheckedException;
	//--------------------------------------------------------------------------��04�ࣺdelete�����ӿ�

	/**
	 * <p>�����delete������ÿ��ɾ��һ����¼</p>
	 * <p>����Ҫ��</p>
	 * 
	 * <p>1. �����ݿ���ɾ����BO����Ӧ�ļ�¼���÷������Ǹ���BO������������ƥ��ɾ��</p>
	 * <p>2. ��֧������������</p>
	 *
	 * @param bo ҵ�����ʵ�����ڲ������д���������¼���������
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public void deleteSingleRow(PersistenceObject bo) throws BaseCheckedException;
	
	/**
	 * 
	 * <p>������delete����</p>
	 *
	 * @param listBO ҵ�����ʵ���б��ڲ������д���������¼���������
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public void deleteBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException;
	
	/**
	 * 
	 * <p>����ָ����sql�����ɾ������</p>
	 *
	 * @param sql     ɾ��sql���
	 * @param params  ɾ��sql�������������б�
	 *                       
	 * @return  ɾ���˶�������¼
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public int deleteBatchRowBySQL(String sql, List<Object> params) throws BaseCheckedException;
	
	/**
	 * 
	 * <p>����ָ����sqlkey��ɾ������</p>
	 *
	 * @param key     ɾ��sql����sqlkey
	 * @param params  ɾ��sql�������������б�
	 *                       
	 * @return  ɾ���˶�������¼
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public int deleteBatchRowByKey(String key, List<Object> params) throws BaseCheckedException;
	

	//--------------------------------------------------------------------------��05�ࣺ��ѯ�ӿ�

//	/**
//	 * 
//	 * <p>����ȫ����ѯ�����������BO List�嵥��ʽ����</p>
//	 *
//	 * @param boClaz ҵ������࣬�����������ݶ����ԭ��class
//	 * @param sql    ��ѯsql���
//	 * @param params ��ѯ����
//	 * 
//	 * @return  ���� BO List
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public List<BusinessObject> queryToBOListBySQL(Class<?> boClaz,
//			String sql, List<?> params) throws VHBaseCheckedException;
//
//	/**
//	 * 
//	 * <p>����ȫ����ѯ�����������BO List�嵥��ʽ����</p>
//	 *
//	 * @param boClaz ҵ������࣬�����������ݶ����ԭ��class
//	 * @param key    ����ѯsql����key�����ݸ�key�������ļ��ж�ȡsql���
//	 * @param params ��ѯ����
//	 * 
//	 * @return  ���� BO List
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public List<BusinessObject> queryToBOListByKey(Class<?> boClaz,
//			String key, List<?> params) throws VHBaseCheckedException;

	/**
	 * 
	 * <p>����ȫ����ѯ�����������CachedRowSet��ʽ����</p>
	 *
	 * @param sql    ��ѯsql���
	 * @param params ��ѯ����
	 * 
	 * @return  ����CachedRowSet
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public CachedRowSet queryToCachedRowSetBySQL(String sql, List<Object> params) 
		throws BaseCheckedException;

	/**
	 * 
	 * <p>����ȫ����ѯ�����������CachedRowSet��ʽ����</p>
	 *
	 * @param key    ����ѯsql����key�����ݸ�key�������ļ��ж�ȡsql���
	 * @param params ��ѯ����
	 * 
	 * @return  ����CachedRowSet
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public CachedRowSet queryToCachedRowSetByKey(String key, List<Object> params) 
		throws BaseCheckedException;

//	/**
//	 * 
//	 * <p>ָ����ҳ�������Է�ҳ��ʽ���ز�ѯ�����������BO List�嵥��ʽ����</p>
//	 *
//	 * @param boClaz ҵ������࣬�����������ݶ����ԭ��class
//	 * @param sql    ��ѯsql���
//	 * @param params ��ѯ����
//	 * @param rowsPerPage  ÿҳ��������
//	 * @param pageIndex ��ǰ��Ҫ��ʾ��ҳ��
//	 * 
//	 * @return  ���� BOList
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public List<BusinessObject> queryPageToBOListBySQL(Class<?> boClaz,
//			String sql, List<?> params, int rowsPerPage, int pageIndex) 
//				throws VHBaseCheckedException;
//
//	
//	/**
//	 * 
//	 * <p>ָ����ҳ�������Է�ҳ��ʽ���ز�ѯ�����������BO List�嵥��ʽ����</p>
//	 *
//	 * @param boClaz ҵ������࣬�����������ݶ����ԭ��class
//	 * @param key    ����ѯsql����key�����ݸ�key�������ļ��ж�ȡsql���
//	 * @param params ��ѯ����
//	 * @param rowsPerPage  ÿҳ��������
//	 * @param pageIndex ��ǰ��Ҫ��ʾ��ҳ��
//	 * 
//	 * @return  ���� BOList
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public List<BusinessObject> queryPageToBOListByKey(Class<?> boClaz,
//			String key, List<?> params, int rowsPerPage, int pageIndex) 
//				throws VHBaseCheckedException;
//
//	/**
//	 * 
//	 * <p>ָ����ҳ�������Է�ҳ��ʽ���ز�ѯ�����������BO List�嵥��ʽ����</p>
//	 *
//	 * @param boClaz ҵ������࣬�����������ݶ����ԭ��class
//	 * @param sql    ��ѯsql���
//	 * @param params ��ѯ����
//	 * @param metaData  ��ѯ��ҳ�������� 
//	 * 
//	 * @return  ���� BOList
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public List<BusinessObject> queryPageToBOListBySQL(Class<?> boClaz,
//			String sql, List<?> params, PaginationMetaData metaData) 
//				throws VHBaseCheckedException;
//
//	
//	
//	/**
//	 * 
//	 * <p>ָ����ҳ�������Է�ҳ��ʽ���ز�ѯ�����������BO List�嵥��ʽ����</p>
//	 *
//	 * @param boClaz ҵ������࣬�����������ݶ����ԭ��class
//	 * @param key    ����ѯsql����key�����ݸ�key�������ļ��ж�ȡsql���
//	 * @param params ��ѯ����
//	 * @param metaData  ��ѯ��ҳ�������� 
//	 * 
//	 * @return  ���� BOList
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public List<BusinessObject> queryPageToBOListByKey(Class<?> boClaz,
//			String key, List<?> params, PaginationMetaData metaData) 
//				throws VHBaseCheckedException;

	
	/**
	 * <p>����ǰ̨��ҳ��ѯ����Ӧ��ʱ��Ӧ��ǰ̨��ҳӦ��һ����</p>
	 *
	 * @param sql       ��ѯsql���
	 * @param params    ��ѯ����
	 * @param metaData  ��ѯ��ҳ��������
	 * 
	 * @return   �������ݼ�
	 * @throws BaseCheckedException
	 */
	public CachedRowSet queryPageToCachedRowSetBySQL(String sql, List<Object> params, 
			PaginationMetaData metaData) throws BaseCheckedException;

	/**
	 * <p>����ǰ̨��ҳ��ѯ����Ӧ��ʱ��Ӧ��ǰ̨��ҳӦ��һ����</p>
	 *
	 * @param key       ����ѯsql����key�����ݸ�key�������ļ��ж�ȡsql���
	 * @param params    ��ѯ����
	 * @param metaData  ��ѯ��ҳ��������
	 * 
	 * @return   �������ݼ�
	 * @throws BaseCheckedException
	 */
	public CachedRowSet queryPageToCachedRowSetByKey(String key, List<Object> params, 
			PaginationMetaData metaData) throws BaseCheckedException;


//	/**
//	 * 
//	 * <p>ָ����ҳ�������Է�ҳ��ʽ���ز�ѯ�����������CachedRowSet��ʽ����</p>
//	 *
//	 * @param sql    ��ѯsql���
//	 * @param params ��ѯ����
//	 * @param rowsPerPage  ÿҳ��������
//	 * @param pageIndex ��ǰ��Ҫ��ʾ��ҳ��
//	 * 
//	 * @return  ���� CachedRowSet
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public CachedRowSet queryPageToCachedRowSetBySQL(String sql, List<Object> params, 
//			int rowsPerPage, int pageIndex) throws VHBaseCheckedException;

//	/**
//	 * 
//	 * <p>ָ����ҳ�������Է�ҳ��ʽ���ز�ѯ�����������CachedRowSet��ʽ����</p>
//	 *
//	 * @param key    ����ѯsql����key�����ݸ�key�������ļ��ж�ȡsql���
//	 * @param params ��ѯ����
//	 * @param rowsPerPage  ÿҳ��������
//	 * @param pageIndex ��ǰ��Ҫ��ʾ��ҳ��
//	 * 
//	 * @return  ���� CachedRowSet
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public CachedRowSet queryPageToCachedRowSetByKey(String key, List<Object> params, 
//			int rowsPerPage, int pageIndex) throws VHBaseCheckedException;



	//--------------------------------------------------------------------------��06�ࣺLOB�������
	
	/**
	 * 
	 * <p>�����ݿ���ָ��bo��blob�������ֽ��������ʽ����</p>
	 *
	 * @param boClaz           Ҫ��ѯ��BO��������
	 * @param id             Ҫ��ѯ�ļ�¼������ֵ
	 * @param blobFieldName  bo�����н�Ҫȡ�õ�blob����������
	 * 
	 * @return               ����BLOB���ݵ��ֽ�����
	 * @throws BaseCheckedException
	 */
	public byte[] blobFetch(Class<?> boClaz, Serializable id, String blobFieldName)
			throws BaseCheckedException ;

	/**
	 * 
	 * <p>�����ݿ���ָ��bo��blob����д��ָ�����������</p>
	 *
	 * @param boClaz           Ҫ��ѯ��BO��������
	 * @param id             Ҫ��ѯ�ļ�¼������ֵ
	 * @param blobFieldName bo�����н�Ҫȡ�õ�blob����������
	 * @param os            ��blob������д����������
	 * 
	 * @throws BaseCheckedException
	 */
	public void blobFetch(Class<?> boClaz, Serializable id, String blobFieldName,
			OutputStream os) throws BaseCheckedException ;

	
	/**
	 * 
	 * <p>���Blob�ֶ�</p>
	 *
	 * @param boClaz           Ҫ����BO��������
	 * @param id             Ҫ���ļ�¼������ֵ
	 * @param blobFieldName bo�����н�Ҫȡ�õ�blob����������
	 * @param data            ��д���������
	 * @throws BaseCheckedException
	 */
	public void blobFill(Class<?> boClaz, Serializable id, String blobFieldName, byte[] data)
			throws BaseCheckedException ;

	
	/**
	 * <p>�÷��������ݿ���ָ��bo��clob����ת��Ϊstring������</p>
	 * 
	 * @param boClaz           Ҫ��ѯ��BO��������
	 * @param id             Ҫ��ѯ�ļ�¼������ֵ
	 * @param clobFieldName bo�����н�Ҫȡ�õ�Clob����������
	 * 
	 * @return String      ����clobת���ɵ��ַ���
	 * @throws BaseCheckedException
	 */
	public String clobFetch(Class<?> boClaz, Serializable id, String clobFieldName)
			throws BaseCheckedException ;
	
	/**
	 * 
	 * <p>���Clob�ֶ�</p>
	 *
	 * @param boClaz           Ҫ����BO��������
	 * @param id             Ҫ���ļ�¼������ֵ
	 * @param clobFieldName  bo�����н�Ҫ����Clob����������
	 * @param clobContent    ��������
	 * @throws BaseCheckedException
	 */
	public void clobFill(Class<?> boClaz, Serializable id, String clobFieldName,
			String clobContent) throws BaseCheckedException;

	
	//--------------------------------------------------------------------------��07�ࣺ�洢���̲���

	/**
	 * 
	 * <p>ִ�����ݿ�����storedProcName�����Ĵ洢����</p>
	 *
	 * @param storedProcName   �洢���̵����ơ�
	 * @param params           ��װ�˴洢���̵��������롢���������ÿ��������StoredProcParamObj����װ��
	 * @return                 ��װ��ִ�н��
	 * 
	 * @throws BaseCheckedException
	 */
	public List<Object> storedProcedureInvoke(String storedProcName, List<StoredProcParamObj> params) 
				throws BaseCheckedException;

	
	//--------------------------------------------------------------------------��08�ࣺ���ù��ߺ���

	/**
	 * ȡ�����ݿ��ʱ�䣬��Ϊϵͳ��ǰʱ��
	 * 
	 * @return ��Calendar����ʽ��ʾ�����ݿ⵱ǰʱ��
	 */
	public Calendar getDBTime() throws BaseCheckedException;


	/**
	 * 
	 * <p>����clob����</p>
	 *
	 * @return Clob
	 */
	public Clob createClob(String content);
	
	/**
	 * 
	 * <p>����blob����</p>
	 *
	 * @return Blob
	 */
	public Blob createBlob(byte[] bytes);


	/**
	 * 
	 * <p>�������֣��������кţ��������ݼ�¼������</p>
	 *
	 * @param name  ���к�����
	 * 
	 * @return  �������кţ�����������long��ʾ
	 * @throws BaseCheckedException
	 */
	public long getSequence(String name) throws BaseCheckedException;
	
	/**
	 * 
	 * <p>�������֣��������кţ��������ݼ�¼������</p>
	 *
	 * @param name  ���к�����
	 * @param num   һ����ȡ���кŵ�����
	 * 
	 * @return  �������кţ�����������long[]��ʾ
	 * @throws BaseCheckedException
	 */
	public long[] getSequence(String name, int num) throws BaseCheckedException;
	
	
}
