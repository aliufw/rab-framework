package com.rab.framework.dao;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.impl.SessionImpl;
import org.hibernate.type.Type;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.dao.dialect.Dialect;
import com.rab.framework.dao.dialect.DialectFactory;
import com.rab.framework.domain.domainconfig.ModelConfig;
import com.rab.framework.domain.domainconfig.PersistenceDomainConfig;
import com.rab.framework.domain.po.PersistenceObject;
import com.rab.framework.domain.server.CoreAppServer;
import com.rab.framework.domain.session.DomainSession;

/**
 * 
 * <P>Title: HibernateDAO</P>
 * <P>Description: </P>
 * <P>����˵�����־ò����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class PersistenceDAOImpl implements PersistenceDAO{
	private static final LogWritter logger = LogFactory.getLogger(PersistenceDAOImpl.class);
	
	/**
	 * �־ò��Session, �ͻ�����ֱ������ʹ��
	 */
	private Session session;

	/**
	 * DAO�����Ӧ������Դ����
	 */
	private String sessionFactoryName;
	
	/**
	 * domainSession�ص�
	 */
	private DomainSession domainSession;

	/**
	 * Hibernate����������
	 */
	private Transaction transaction;
	
	/**
	 * ������
	 * 
	 * @param dataSourceName ����Դ����
	 */
	public PersistenceDAOImpl(String sessionFactoryName){
		this.sessionFactoryName = sessionFactoryName;
	}
	
	//--------------------------------------------------------------------------��01�ࣺ��������ӿ�
	
	/**
	 * ��õ�ǰ�������Ĭ�����ݿ�����
	 * 
	 * @return
	 */
	public Connection getConnection(){
		SessionImpl sessionImpl = (SessionImpl)session;
		return sessionImpl.getJDBCContext().borrowConnection();
		
	}

	/**
	 * ��õ�ǰ���׵�Hibernate Session
	 * 
	 * @return org.hibernate.Sessionʵ������
	 */
	public Session getSession(){
		return this.session;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * ��õ�ǰ���׵�DomainSession
	 * 
	 * @return com.rab.framework.domain.session.DomainSession ʵ������
	 */
	public DomainSession getDomainSession(){
		return this.domainSession;
	}
	
	public void setDomainSession(DomainSession domainSession) {
		this.domainSession = domainSession;
	}


	/**
	 * <p>�رճ־ò�Ự</p>
	 *
	 */
	public void close(){
		this.session.close();
	}

	/**
	 * <p>��������</p>
	 *
	 */
	public void beginTransaction(){
		this.transaction = session.beginTransaction();
	}
	

	/**
	 * <p>�ع�����</p>
	 *
	 */
	public void rollbackTransaction(){
		if(this.transaction != null){
			this.transaction.rollback();
		}
	}

	/**
	 * 
	 * <p>�ύ����</p>
	 *
	 */
	public void commitTransaction(){
		if(this.transaction != null){
			this.transaction.commit();
		}
	}

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
	public Serializable insertSingleRow(PersistenceObject bo) throws BaseCheckedException{
		
		Serializable ret = null;
		
		if(bo == null){
			logger.error("00000402: ��ִ�� insertSingleRow(...) ������ҵ�����Ϊnull��");
			return ret;
		}
		
		registerBOforDebug(bo.getClass()); // ȡ�����ݿ�����
		
		if (hasLrsj(bo)) {
			bo.setLrsj(Calendar.getInstance());
		}
		
		try {
			ret = session.save(bo);
			
			session.flush();
		} catch (HibernateException e) {
			logger.error("00000402: ִ�� insertSingleRow(...) ����ʱ�����쳣��", e);
			throw new BaseCheckedException("00000402", e);
		} 
		finally {
		
		}
		
		return ret;
	}

	/**
	 * 
	 * <p>������insert����</p>
	 * <p>����Ҫ��</p>
	 * 
	 * <p>1. �÷�����ִ��Insert������ʱ�򣬲����BO��Ӧ�ļ����������κβ�����
	 * �����BO�����д���һ���������ӱ������ô�÷���������ӱ�����κ������ϵĲ�����</p>
	 * <p>2. �ڽ���HBMӳ���ļ���ʱ�򣬶�������cascade����һ�����ó�Ϊnone</p>
	 *
	 * @param bos ҵ�����ʵ�����ڲ������д���������¼���������
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public void insertBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException{
		if(listBO == null || listBO.size()==0){
			return;
		}
		
		Class<?> claz = listBO.get(0).getClass();
		registerBOforDebug(claz); // ȡ�����ݿ�����
		
		try {
			for(PersistenceObject bo : listBO){
				if (hasLrsj(bo)) {
					bo.setLrsj(Calendar.getInstance());
				}
				
				session.save(bo);
			}
			session.flush(); // ˢ�����ݿ�
		} catch (HibernateException e) {
			logger.error("00000403: ִ�� insertBatchRow(...) ����ʱ�����쳣��", e);
			throw new BaseCheckedException("00000403", e);
		} finally {
		}
	}
	

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
	public int updateSingleRow(PersistenceObject bo) throws BaseCheckedException{
		
		int result = 0;
		try {
			registerBOforDebug(bo.getClass());
			
			if (hasXgsj(bo)) {
				bo.setXgsj(Calendar.getInstance());
			}

			// BO������û�и��£���ִ�и��¶���
			if (bo.getStatus().isEmpty()) {
				logger.info("BO:" + bo.getClass().getName() + "����û�иı䣬����ִ��update����");
				return 0;
			}
			
			Map<String, Object> columns = HibernateMetadataUtil.getIdInfo(this, bo);
			// �������θ��µ�HQL���
			StringBuffer updateHQL = singleUpdateHQLBuilder(bo, session, columns.keySet());
			// ����Query
			Query query = session.createQuery(updateHQL.toString());
			// ����query��Ӧ�Ĳ�����Ϣ
			prepareQueryParams(query, bo.getStatus(), bo.getClass());
			// ���������Ĳ�ѯֵ
			Iterator<String> ite = columns.keySet().iterator();
			while (ite.hasNext()) {
				String column = ite.next();
				Object columnInfo = columns.get(column);
				
				Class<?> columnType = columnInfo.getClass();
				Type type = PersistenceUtils.judgeType(columnType);
				
				query.setParameter("old" + column, columnInfo, type); 
			}
			result = query.executeUpdate();
			session.flush();
		} 
		catch (BaseCheckedException e) {
			throw e;
		}
		catch (HibernateException e) {
			logger.error("00000405: ִ�����ݵ�ֵ����ʱ�����쳣��", e);
			throw new BaseCheckedException("00000405", e);
		}

		return result;
	}
	
	/**
	 * <p>�����update������ÿ�θ��¶�����¼</p>
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
	public int updateBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException{
		if(listBO == null){
			return 0;
		}
		
		for(PersistenceObject bo : listBO){
			updateSingleRow(bo);
		}
		
		return listBO.size();
	}
	
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
	public int updateBatchRowBySQL(String sql, List<Object> params) throws BaseCheckedException{
		PersistenceUtils.doSqlLog(sql);
		
		PreparedStatement ps = null;
		int count;

		try {
			// ��Connection���� PreparedStateme
			ps = this.getConnection().prepareStatement(sql);
			// ����sql����в���
			ps = PersistenceUtils.prepareSqlParams(ps, params);
			// ִ��ɾ������
			count = ps.executeUpdate();
		} 
		catch (SQLException e) {
			logger.error("00000407: ִ������������������ʱ�����쳣��", e);
			throw new BaseCheckedException("00000407", e);
		} 
		finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex1) {
			}
		}
		return count;
	}
	
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
	public int updateBatchRowByKey(String key, List<Object> params) throws BaseCheckedException{
		String sql = getSqlByKey(key);
		return updateBatchRowBySQL(sql, params);
	}
	
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
//			String sqlWhere, List<Object> params) throws VHBaseCheckedException{
//		
//		return updateBatchRowByConditionBySQL(bo, null, sqlWhere, params);
//	}

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
//			String setValue, String sqlWhere, List<Object> params) throws VHBaseCheckedException{
//		if (bo.getStatus().isEmpty()) {
//			logger.info("BO:" + bo.getClass().getName() + "����û�иı䣬����ִ��update����");
//			return 0;
//		}
//		
//		if (hasXgsj(bo)) {
//			bo.setXgsj(Calendar.getInstance());
//		}
//		
//		logger.debug("the bo status size :" + bo.getStatus().size());
//
//		registerBOforDebug(bo.getClass());
//
//		// �ϲ�set�Ĳ�����where�Ĳ���
//		List<Object> setAndWhereParams = buildUpdateSetAndWhereParams(bo, params);
//		// ����sql�������Ŀ��
//		String sqlStr = updateSqlBuilder(bo, setValue, sqlWhere);
//		// ���¹���
//		PreparedStatement ps = null;
//		int result;
//
//		try {
//			// ��Connection���� PreparedStateme
//			ps = this.getConnection().prepareStatement(sqlStr);
//			// ����sql����в���
//			ps = PersistenceUtils.prepareSqlParams(ps, setAndWhereParams);
//			// ִ�и��²���
//			result = ps.executeUpdate();
//		} catch (SQLException e) {
//			logger.error("00000407: ִ������������������ʱ�����쳣��", e);
//			throw new VHBaseCheckedException("00000407", e);
//		} finally {
//			try {
//				if (ps != null) {
//					ps.close();
//				}
//			} catch (SQLException ex1) {
//			}
//		}
//		return result;
//	}
//	
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
	public void deleteSingleRow(PersistenceObject bo) throws BaseCheckedException{
		if(bo == null){
			return ;
		}
		
		registerBOforDebug(bo.getClass());
		
		//1. ȡ���ݱ�����
		String tableName = HibernateMetadataUtil.getTableNameByClassName(this, bo.getClass().getName());

		//2. ȡ�����ֶμ���ֵ��Ϣ
		Map<String, Object> idInfo = HibernateMetadataUtil.getIdInfo(this, bo);
		List<String> keys = new ArrayList<String>();
		List<Object> params = new ArrayList<Object>();
		
		Iterator<String> iter = idInfo.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			keys.add(key);
			params.add(idInfo.get(key));
		}

		//3.ȡ������Ӧ���ֶ���
		List<String> keyFields = new ArrayList<String>();
		for(String key : keys){
			String field = HibernateMetadataUtil.getColumnNameByPropertyName(this, bo.getClass().getName(), key);
			keyFields.add(field);
		}
		
		String sql = "delete from " + tableName + " where ";
		sql += keyFields.get(0) + "=?";
		for(int i=1; i<keyFields.size(); i++){
			sql += " and " + keyFields.get(i) + "=? ";
		}
		
		try {
			logger.debug("ɾ������BO���ݣ�sql = " + sql + ", ����������" + params);
			PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
			pstmt = PersistenceUtils.prepareSqlParams(pstmt, params);
			
			pstmt.execute();
			
		} catch (SQLException e) {
			logger.error("00000408: ��BOִ��ɾ������ʱ�����쳣", e);
			throw new BaseCheckedException("00000408", e);
		}
	}
	
	/**
	 * 
	 * <p>������delete����</p>
	 *
	 * @param lstBO ҵ�����ʵ���б��ڲ������д���������¼���������
	 * 
	 * @throws BaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
	 */
	public void deleteBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException{
		if(listBO == null || listBO.size() == 0){
			return ;
		}
		
		registerBOforDebug(listBO.get(0).getClass());

		//1. ȡ���ݱ�����
		String tableName = HibernateMetadataUtil.getTableNameByClassName(this, listBO.get(0).getClass().getName());

		//2. ȡ�����ֶμ���ֵ��Ϣ
		Map<String, Object> idMeta = HibernateMetadataUtil.getIdInfo(this, listBO.get(0));
		List<String> keys = new ArrayList<String>();
		Iterator<String> iter = idMeta.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			keys.add(key);
		}

		//3.ȡ������Ӧ���ֶ���
		List<String> keyFields = new ArrayList<String>();
		for(String key : keys){
			String field = HibernateMetadataUtil.getColumnNameByPropertyName(this, listBO.get(0).getClass().getName(), key);
			keyFields.add(field);
		}
		
		String sql = "delete from " + tableName + " where ";
		sql += keyFields.get(0) + "=?";
		for(int i=1; i<keyFields.size(); i++){
			sql += " and " + keyFields.get(i) + "=? ";
		}
		
		try {
			logger.debug("����ɾ��BO���ݣ�sql = " + sql);
			PreparedStatement pstmt = this.getConnection().prepareStatement(sql);

			for(PersistenceObject bo : listBO){
				List<Object> params = new ArrayList<Object>();
				Map<String, Object> idInfo = HibernateMetadataUtil.getIdInfo(this, bo);
				for(String key : keys){
					params.add(idInfo.get(key));
				}
				logger.debug("����ɾ��BO���ݣ������б�" + params);

				pstmt = PersistenceUtils.prepareSqlParams(pstmt, params);
				
				pstmt.execute();
			}
		} catch (SQLException e) {
			logger.error("00000409: ��BOִ������ɾ������ʱ�����쳣");
			throw new BaseCheckedException("00000409", e);
		}
	}
	
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
	public int deleteBatchRowBySQL(String sql, List<Object> params) throws BaseCheckedException{

		PersistenceUtils.doSqlLog(sql);
		
		PreparedStatement ps = null;
		int count;

		try {
			// ��Connection���� PreparedStateme
			ps = this.getConnection().prepareStatement(sql);
			// ����sql����в���
			ps = PersistenceUtils.prepareSqlParams(ps, params);
			// ִ��ɾ������
			count = ps.executeUpdate();
		} 
		catch (SQLException ex) {
			logger.error("00000410: ��BOִ����������ɾ������ʱ�����쳣");
			throw new BaseCheckedException("00000410", ex);
		} 
		finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex1) {
			}
		}
		return count;
	}
	
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
	public int deleteBatchRowByKey(String key, List<Object> params) throws BaseCheckedException{
		String sql = this.getSqlByKey(key);
		
		return deleteBatchRowBySQL(sql, params);
	}
	

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
//			String sql, List<?> params) throws VHBaseCheckedException{
//		
//		return queryPageToBOListBySQL(boClaz, sql, params, -1, -1);
//	}
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
//			String key, List<?> params) throws VHBaseCheckedException{
//		
//		String sql = getSqlByKey(key);
//			
//		return queryToBOListBySQL(boClaz, sql, params);
//	}

	
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
		throws BaseCheckedException{
		
		Connection con = this.getConnection();
		try {
			PersistenceUtils.doSqlLog(sql, params); //�����־
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt = PersistenceUtils.prepareSqlParams(pstmt, params);
			ResultSet rs = pstmt.executeQuery();
			CachedRowSet rowSet = new CachedRowSet(); 
			rowSet.populate(rs); 
			return rowSet;
		} catch (SQLException ex) {
			logger.error("00000435: ִ�����ݿ���ͨ��ѯʱ�����쳣");
			throw new BaseCheckedException("00000435", ex);
		}
	}
	
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
		throws BaseCheckedException{
		
		String sql = getSqlByKey(key);
		
		return queryToCachedRowSetBySQL(sql, params);

	}
	
//	/**
//	 * 
//	 * <p>ָ����ҳ�������Է�ҳ��ʽ���ز�ѯ�����������BO List�嵥��ʽ����</p>
//	 *
//	 * @param boClaz ҵ������࣬�����������ݶ����ԭ��class
//	 * @param sql    ��ѯsql���
//	 * @param params ��ѯ����
//	 * @param rowsPerPage  ÿҳ��������
//	 * @param pageIndex    ��ǰ��Ҫ��ʾ��ҳ��
//	 * 
//	 * @return  ���� BOList
//	 * @throws VHBaseCheckedException �������쳣ʱ���׳�VHBaseCheckedException�쳣��
//	 */
//	public List<BusinessObject> queryPageToBOListBySQL(Class<?> boClaz,
//			String sql, List<?> params, int rowsPerPage, int pageIndex) 
//				throws VHBaseCheckedException{
//		
//		registerBOforDebug(boClaz);
//		
//		CachedRowSet rowset = queryPageToCachedRowSetBySQL(sql, params, rowsPerPage, pageIndex);
//		
//		return rowset2VOList(boClaz, rowset);
//	}
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
//				throws VHBaseCheckedException{
//		
//		registerBOforDebug(boClaz);
//		
//		String sql = getSqlByKey(key);
//		return queryPageToBOListBySQL(boClaz, sql, params, rowsPerPage, pageIndex);
//	}
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
//				throws VHBaseCheckedException{
//		registerBOforDebug(boClaz);
//		
//		CachedRowSet rowset = queryPageToCachedRowSetBySQL(sql, params, metaData);
//		
//		return rowset2VOList(boClaz, rowset);
//
//	}
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
//				throws VHBaseCheckedException{
//		
//		registerBOforDebug(boClaz);
//		
//		String sql = getSqlByKey(key);
//		CachedRowSet rowset = queryPageToCachedRowSetBySQL(sql, params, metaData);
//		
//		return rowset2VOList(boClaz, rowset);
//
//	}

	
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
			PaginationMetaData metaData) throws BaseCheckedException{
		
//		int rowsPerPage = metaData.getRowsPerPage();
		int pageIndex = metaData.getPageIndex();
		if(pageIndex == 1){
			//�״β�ѯ���践��������
			int totalRowNum = getTotalRecorder(sql, params);
			metaData.setTotalRowNum(totalRowNum);
		}
		
		logger.debug("���������ҳ��Ϊ��" + pageIndex + "ҳ�����������ǣ�" + metaData.getTotalRowNum() + "����");

//		String sortFlag = metaData.getSortFlag();
//		String sortFieldName = metaData.getSortFieldName();
//		if (sortFlag!=null && sortFieldName != null && sortFieldName.trim().length()>0) {
//			sql = getSortSql(sql, sortFlag, sortFieldName);
//		}
		
//		return queryPageToCachedRowSetBySQL(sql, params, rowsPerPage, pageIndex);
		
		Dialect dia = DialectFactory.createDialect(this);
		
		return dia.queryPageBySQL(sql, params, metaData);

	}

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
			PaginationMetaData metaData) throws BaseCheckedException{
		
		String sql = getSqlByKey(key);
		
		return queryPageToCachedRowSetBySQL(sql, params, metaData);
	}
	
	
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
//			int rowsPerPage, int pageIndex) throws VHBaseCheckedException{
//		
//		PersistenceUtils.doSqlLog(sql);
//		
//		Dialect dia = DialectFactory.createDialect(this);
//
//		return dia.queryPageBySQL(sql, params, rowsPerPage, pageIndex);
//	}

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
//			int rowsPerPage, int pageIndex) throws VHBaseCheckedException{
//	
//		String sql = getSqlByKey(key);
//		
//		return queryPageToCachedRowSetBySQL(sql, params, rowsPerPage, pageIndex);
//	}


	//--------------------------------------------------------------------------��06�ࣺLOB�������

	/**
	 * 
	 * <p>�����ݿ���ָ��bo��blob�������ֽ��������ʽ����</p>
	 *
	 * @param claz           Ҫ��ѯ��BO��������
	 * @param id             Ҫ��ѯ�ļ�¼������ֵ
	 * @param blobFieldName  bo�����н�Ҫȡ�õ�blob����������
	 * 
	 * @return               ����BLOB���ݵ��ֽ�����
	 * @throws BaseCheckedException
	 */
	public byte[] blobFetch(Class<?> boClaz, Serializable id, String blobFieldName)
			throws BaseCheckedException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		blobFetch(boClaz, id, blobFieldName, baos);
		return baos.toByteArray();
	}
	
	/**
	 * 
	 * <p>�����ݿ���ָ��bo��blob����д��ָ�����������</p>
	 *
	 * @param claz           Ҫ��ѯ��BO��������
	 * @param id             Ҫ��ѯ�ļ�¼������ֵ
	 * @param blobFieldName bo�����н�Ҫȡ�õ�blob����������
	 * @param os            ��blob������д����������
	 * 
	 * @throws BaseCheckedException
	 */
	public void blobFetch(Class<?> boClaz, Serializable id, String blobFieldName,
			OutputStream os) throws BaseCheckedException {

		try {
			registerBOforDebug(boClaz);

			PersistenceObject bo = (PersistenceObject)session.load(boClaz, id);
			
			// �õ�Blob�ֶε�GET������
			StringBuffer methodName = new StringBuffer("get");
			methodName.append(blobFieldName.substring(0, 1).toUpperCase());
			methodName.append(blobFieldName.substring(1, blobFieldName.length()));

			Class<?>[] params = null;
			Method method = boClaz.getMethod(methodName.toString(), params);

			Object[] params2 = null;
			Blob blob = (Blob) method.invoke(bo, params2);

			if (blob == null) {
				logger.debug("in fetchBlob��bo"
						+ bo.getClass().getName() + "��blob����:"
						+ blobFieldName + " Ϊ�ա�");
				return;
			}

			InputStream is = blob.getBinaryStream();

			byte[] buf = new byte[10240];// ����10k�Ļ���
			int len = is.read(buf);
			while (len > 0) {
				os.write(buf, 0, len);
				len = is.read(buf);
			}
			is.close();
			os.close();
			logger.debug("in fetchBlob����ȡbo" + bo.getClass().getName() + "��blob:" + blobFieldName + " ������ϡ�");
		} catch (Exception ex) {
			logger.error("00000427: ��ȡBLOB����(" + boClaz + ":" + blobFieldName + ":" + id + ")ʱ�����쳣", ex);
			List<String> params = new ArrayList<String>();
			params.add(boClaz.getName());
			params.add(blobFieldName);
			params.add("" + id);
			throw new BaseCheckedException("00000427", params, ex);
		} finally {
			;
		}
	}

	
	/**
	 * 
	 * <p>���Blob�ֶ�</p>
	 *
	 * @param claz           Ҫ����BO��������
	 * @param id             Ҫ���ļ�¼������ֵ
	 * @param blobFieldName bo�����н�Ҫȡ�õ�blob����������
	 * @param is            ��д���������
	 * @throws BaseCheckedException
	 */
	public void blobFill(Class<?> boClaz, Serializable id, String blobFieldName, byte[] data)
			throws BaseCheckedException {

		if(data == null){
			data = new byte[0];
		}
		
		try {
			registerBOforDebug(boClaz);

			PersistenceObject bo = (PersistenceObject)session.load(boClaz, id);
			
			// �õ�Clob�ֶε�SET������
			StringBuffer methodNameSet = new StringBuffer("set");
			methodNameSet.append(blobFieldName.substring(0, 1).toUpperCase());
			methodNameSet.append(blobFieldName.substring(1, blobFieldName.length()));

			Class<?> cls = bo.getClass();

			Class<?>[] blobClassArray = { Blob.class };
			Method methodSet = cls.getMethod(methodNameSet.toString(),blobClassArray);

			Object[] content = { Hibernate.createBlob(data,session)};
			methodSet.invoke(bo, content);

			if (hasXgsj(bo)) {
				bo.setXgsj(Calendar.getInstance());
			}
			
			session.update(bo);
			session.flush();
		} 
		catch (Exception ex) {
			logger.error("00000426: ����BLOB����(" + boClaz + ":" + blobFieldName + ":" + id + ")ʱ�����쳣", ex);
			List<String> params = new ArrayList<String>();
			params.add(boClaz.getName());
			params.add(blobFieldName);
			params.add("" + id);
			throw new BaseCheckedException("00000426", params, ex);
		} 
		finally {
			;
		}
	}
	
	
	/**
	 * <p>�÷��������ݿ���ָ��bo��clob����ת��Ϊstring������</p>
	 * 
	 * @param claz           Ҫ��ѯ��BO��������
	 * @param id             Ҫ��ѯ�ļ�¼������ֵ
	 * @param blobFieldName bo�����н�Ҫȡ�õ�Clob����������
	 * 
	 * @return String      ����clobת���ɵ��ַ���
	 * @throws BaseCheckedException
	 */
	public String clobFetch(Class<?> boClaz, Serializable id, String clobFieldName)
			throws BaseCheckedException {
		try {
			registerBOforDebug(boClaz);
			
			PersistenceObject bo = (PersistenceObject)session.load(boClaz, id);
			
			// �õ�Clob�ֶε�GET������
			StringBuffer methodName = new StringBuffer("get");
			methodName.append(clobFieldName.substring(0, 1).toUpperCase());
			methodName.append(clobFieldName.substring(1, clobFieldName.length()));


			Class<?>[] params = null;
			Method method = boClaz.getMethod(methodName.toString(), params);

			Object[] params2 = null;
			Clob clob = (Clob) method.invoke(bo, params2);

			if (clob == null) {
				logger.debug("in fetchClob��bo"
						+ boClaz + "��clob����:"
						+ clobFieldName + " Ϊ��,����null.");
				return null;
			}

			Reader readerForClob = clob.getCharacterStream();

			// ���clob���ַ���
			BufferedReader bufReader = new BufferedReader(readerForClob);

			StringBuffer clobContent = new StringBuffer();

			String line = bufReader.readLine();
			while (line != null) {
				clobContent.append(line + "\r\n");
				line = bufReader.readLine();
			}

			bufReader.close();
			readerForClob.close();

			return clobContent.toString();

		} catch (Exception ex) {
			logger.error("00000425: ��ȡCLOB����(" + boClaz + ":" + clobFieldName + ":" + id + ")ʱ�����쳣", ex);
			List<String> params = new ArrayList<String>();
			params.add(boClaz.getName());
			params.add(clobFieldName);
			params.add("" + id);
			throw new BaseCheckedException("00000425", params, ex);
		} finally {
			;
		}
	}


	/**
	 * 
	 * <p>���Clob�ֶ�</p>
	 *
	 * @param claz           Ҫ����BO��������
	 * @param id             Ҫ���ļ�¼������ֵ
	 * @param clobFieldName  bo�����н�Ҫ����Clob����������
	 * @param clobContent    ��������
	 * @throws BaseCheckedException
	 */
	public void clobFill(Class<?> boClaz, Serializable id, String clobFieldName,
			String clobContent) throws BaseCheckedException {

		if(clobContent == null){
			clobContent = "";
		}
		
		try {
			registerBOforDebug(boClaz);

			PersistenceObject bo = (PersistenceObject)session.load(boClaz, id);
			
			// �õ�Clob�ֶε�SET������
			StringBuffer methodNameSet = new StringBuffer("set");
			methodNameSet.append(clobFieldName.substring(0, 1).toUpperCase());
			methodNameSet.append(clobFieldName.substring(1, clobFieldName.length()));

			Class<?> cls = bo.getClass();

			Class<?>[] clobClassArray = { Clob.class };
			Method methodSet = cls.getMethod(methodNameSet.toString(),clobClassArray);

			Object[] content = { Hibernate.createClob(clobContent.toString(),session)};
			methodSet.invoke(bo, content);

			if (hasXgsj(bo)) {
				bo.setXgsj(Calendar.getInstance());
			}
			
			session.update(bo);
			session.flush();
		} 
		catch (Exception ex) {
			logger.error("00000424: ����CLOB����(" + boClaz + ":" + clobFieldName + ":" + id + ")ʱ�����쳣", ex);
			List<String> params = new ArrayList<String>();
			params.add(boClaz.getName());
			params.add(clobFieldName);
			params.add("" + id);
			throw new BaseCheckedException("00000424", params, ex);
		} 
		finally {
			;
		}
	}

	
	//--------------------------------------------------------------------------��07�ࣺ�洢���̲���
	

	/**
	 * 
	 * <p>ִ�����ݿ�����storedProcedure�����Ĵ洢����</p>
	 *
	 * @param storedProcName   �洢���̵����ơ�
	 * @param params           ��װ�˴洢���̵��������롢���������ÿ��������StoredProcParamObj����װ��
	 * @return                 ��װ��ִ�н��
	 * 
	 * @throws BaseCheckedException
	 */
	public List<Object> storedProcedureInvoke(String storedProcName, List<StoredProcParamObj> params) 
				throws BaseCheckedException{
        CallableStatement cs = null;
        try {
        	String spSQL = "{call " + storedProcName;
        	if(params != null && params.size()>0){
        		spSQL += " (";
        		for(int i=0; i<params.size()-1; i++){
        			spSQL += "?, ";
        		}
        		spSQL += "?)";
        	}
        	spSQL += "}";
        	
            Connection con = this.getConnection();
            cs = con.prepareCall(spSQL);
            prepareParams(cs, params); //���ò���
            cs.execute(); //ִ�д洢����
            List<Object> result = setResult(cs, params); //���ɴ洢���̵Ľ��
            
            return result;
        }
        catch (SQLException ex) {
			logger.error("00000417: ��ִ�д洢����ʱ�����쳣", ex);
			throw new BaseCheckedException("00000417", ex);
        }
        finally {
            try {
                if (cs != null) {
                    cs.close();
                }
            }
            catch (SQLException ex1) {
            }
        }
	}
	
	//--------------------------------------------------------------------------��08�ࣺ���ù��ߺ���

	
	/**
	 * ȡ�����ݿ��ʱ�䣬��Ϊϵͳ��ǰʱ��
	 * 
	 * @return ��Calendar����ʽ��ʾ�����ݿ⵱ǰʱ��
	 */
	public Calendar getDBTime() throws BaseCheckedException {
		Dialect dialect = DialectFactory.createDialect(this);
		
		String sql = dialect.getDateQuerySQL();
		
		PersistenceUtils.doSqlLog(sql);
		
		Calendar calendar = new GregorianCalendar();
		Connection con = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			con = this.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				Timestamp ts = rs.getTimestamp(1);
				calendar.setTimeInMillis(ts.getTime());
			}
		} 
		catch (Exception e) {
			logger.error("00000404: ȡ�����ݿ��׼ʱ�䷢���쳣��");
			throw new BaseCheckedException("00000404", e);
		} 
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
		}
		return calendar;
	}
	
	/**
	 * 
	 * <p>����clob����</p>
	 *
	 * @return
	 */
	public Clob createClob(String content){
		return Hibernate.createClob(content, session);
	}

	/**
	 * 
	 * <p>����blob����</p>
	 *
	 * @return
	 */
	public Blob createBlob(byte[] bytes){
		return Hibernate.createBlob(bytes, session);
	}

	/**
	 * 
	 * <p>�������֣��������кţ��������ݼ�¼������</p>
	 *
	 * @param name  ���к�����
	 * 
	 * @return  �������кţ�����������long��ʾ
	 * @throws BaseCheckedException
	 */
	public long getSequence(String name) throws BaseCheckedException{
		long[] ret = null;

		ret = getSequence(name, 1);
		
		return ret[0];
	}

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
	public long[] getSequence(String name, int num) throws BaseCheckedException{
		
		PersistenceDAO sysDao = this.domainSession.getSystemDaoWithoutTx();
		
		Dialect dialect = DialectFactory.createDialect(sysDao);

		logger.debug("ȡ���к� name = " + name + "\tnum = " + num);
		
		long[] ret = dialect.getSequence(name, num, sysDao);
		
		return ret;
	}

//	/**
//	 * 
//	 * <p>ȡϵͳͨ�õ�Ĭ�����ݿ����ӣ����ӵ�������sys-without-tx�������Ӳ�����ȫ���������</p>
//	 * <p>ע�⣺��������ʹ��ʱ��Ҫע���Լ���������͹ر����ӣ�</p>
//	 *
//	 * @return ���أ����ݿ�����
//	 */
//	public Connection getSysConWithoutTx() throws VHBaseCheckedException{
//		
//		PersistenceDAO sysDao = this.domainSession.getSystemDaoWithoutTx();
//		
//		logger.info("����������ȫ�������������ݿ����ӣ�sys-without-tx");
//		return sysDao.getConnection();
//	}

	public List<String> getMappings(){
		
		PersistenceManager pm = this.domainSession.getContext().getCtxPersistenceManagers().get(sessionFactoryName);
		
		List<String> list = pm.getLoadedORmappings();
		
		return list;
	}
	
	
	//˽�з���===============================================================================================

	/**
	 * 
	 * <p></p>
	 *
	 * @param claz
	 * @return
	 * @throws BaseCheckedException
	 */
	private void registerBOforDebug(Class<?> claz) throws BaseCheckedException{

		String runningmodel = domainSession.getServer().getRunningmodel();
		if (!CoreAppServer.RUNNING_MODEL_DEVELOP.equalsIgnoreCase(runningmodel)) {
			return ;
		} 
		else {
			ModelConfig modelConfig = this.domainSession.getContext().getModelConfig();
			CoreAppServer server = this.domainSession.getServer();
			PersistenceManager pm = server.getPersistenceManager(sessionFactoryName);
			
			String className = claz.getName();
			String resourceName = className.replace('.', '/') + ".hbm.xml";
	
			boolean isDefined = false;
			boolean isRegistered = false;
			List<PersistenceDomainConfig> persistenceDomainConfigs = modelConfig.getPersistenceConfigs();
			for(PersistenceDomainConfig persistenceDomainConfig : persistenceDomainConfigs ){
				if(persistenceDomainConfig.getSessionfactoryName().equals(this.sessionFactoryName)){
					List<String> mappingClasses = persistenceDomainConfig.getMappingClasses();
					for(String mappingClass : mappingClasses){
						if(mappingClass.equals(className)){
							isDefined = true;
							isRegistered = pm.registerORmappingByClass(className);
							
							break;
						}
					}
					
					if(isDefined == false){
						List<String> mappingResources = persistenceDomainConfig.getMappingResources();
						for(String mappingResource : mappingResources){
							if(mappingResource.equals(resourceName)){
								isRegistered = pm.registerORmappingByResource(resourceName);
								isDefined = true;
								break;
							}
						}
					}
				}
			}
			
			if(!isDefined){
				logger.error("00000401:�־ò�ӳ���ļ� " + className + " û���������ļ���ע��");
				List<String> params = new ArrayList<String>();
				params.add(className);
				throw new BaseCheckedException("00000401",params);
			}
			
			if (isRegistered) { 
				Connection con = this.getConnection();
				SessionFactory sf = pm.getSessionFactory();
				this.session = sf.openSession(con);
			}
		}
	}
	
//	/**
//	 * 
//	 * <p>����set��where�Ĳ�������</p>
//	 *
//	 * @param bo
//	 * @param whereParams
//	 * @return
//	 */
//	private List<Object> buildUpdateSetAndWhereParams(BusinessObject bo, List<Object> whereParams) {
//		Iterator<String> ii = bo.getStatus().keySet().iterator();
//		int i = 0;
//		while (ii.hasNext()) {
//			// �õ�Ҫ���µ�������
//			String name = (String) ii.next();
//			// ������set��where�ϲ�
//			Map<String, Object> map = bo.getStatus();
//			Object v = map.get(name);
//			
//			whereParams.add(i, v);
//			i++;
//		}
//		
//		return whereParams;
//	}


//	/**
//	 * update������SQL������ɷ�������������update��SQL��䡣<br>
//	 * �÷��������sqlWhere��tableName��������SQL��䣻<br>
//	 * tableName���Ա�ʾfrom��sqlWhere��ʾwhere�����sqlWhereΪ<br>
//	 * null������±����������ݼ�¼�� SQL������Ӧ�����������ɵģ�<br>
//	 * UPDATE SET tableName name1=value1,name2=value2,..... WHERE sqlWhere
//	 * 
//	 * @return String �������ɵ�SQL��䡣
//	 */
//	private String updateSqlBuilder(BusinessObject bo, String setValues, String sqlWhere) {
//		StringBuffer sqlStr = new StringBuffer("update ");
//		String className = bo.getClass().getName();
//		// ��õ�ǰҪ������table
//		sqlStr.append(HibernateMetadataUtil.getTableNameByClassName(this, className));
//		sqlStr.append(" set ");
//
//		// ���boҪ������������ö��
//		Iterator<String> ite = bo.getStatus().keySet().iterator();
//
//		while (ite.hasNext()) {
//			String propertyName = ite.next();
//			// ��bo�������ҵ���Ӧ�ı�������
//			String columnName = HibernateMetadataUtil
//					.getColumnNameByPropertyName(this, className,
//							propertyName);
//			sqlStr.append(columnName).append("=?");
//			if (ite.hasNext()) {
//				sqlStr.append(" , ");
//			}
//		}
//
//		// ƴװ����SQL ��set����
//		if (setValues != null) {
//			sqlStr.append(" , ").append(setValues);
//		}
//
//		sqlStr.append(" where ");
//		sqlStr.append(sqlWhere);
//
//		logger.debug(sqlStr.toString());
//		return sqlStr.toString();
//
//	}
	
//	/**
//	 * delete������SQL������ɷ�������������delete��SQL��䡣<br>
//	 * �÷��������sqlWhere��tableName��������SQL��䣻<br>
//	 * tableName���Ա�ʾfrom��sqlWhere��ʾwhere�����sqlWhereΪ<br>
//	 * null����ɾ�������������ݼ�¼�� SQL������Ӧ�����������ɵģ�<br>
//	 * DELETE FROM tableName WHERE sqlWhere
//	 * 
//	 * @return String �������ɵ�SQL��䡣
//	 */
//	private String deleteSqlBuilder(Class<?> boClass, String sqlWhere){
//		StringBuffer sqlStr = new StringBuffer("delete from ");
//
//		// ��õ�ǰҪ������table
//		sqlStr.append(HibernateMetadataUtil.getTableNameByClassName(
//				this, boClass.getName()));
//
//		// ���sqlΪnull��ɾ�����м�¼
//		if (sqlWhere != null) {
//			sqlStr.append(" where ");
//			sqlStr.append(sqlWhere);
//		}
//
//		logger.debug(sqlStr.toString());
//		return sqlStr.toString();
//
//	}

	/**
	 * <p>�ж��Ƿ���ڡ�¼��ʱ�䡱�ֶ�</p>
	 *
	 * @param bo BO����
	 * @return  ������ڣ�����true�����򣬷���false
	 */
	private boolean hasLrsj(PersistenceObject bo) {
		try {
			bo.getClass().getDeclaredField("lrsj");
			return true;
		} catch (Exception e) {
		} 
		
		return false;		
	}

	/**
	 * <p>�ж��Ƿ���ڡ��޸�ʱ�䡱�ֶ�</p>
	 *
	 * @param bo BO����
	 * @return  ������ڣ�����true�����򣬷���false
	 */
	private boolean hasXgsj(PersistenceObject bo) {
		
		try {
			bo.getClass().getDeclaredField("xgsj");
			return true;
		} catch (SecurityException e) {

		} catch (NoSuchFieldException e) {

		}
		return false;
	}

	private int getTotalRecorder(String sql, List<Object> params) throws BaseCheckedException{
		
		Dialect dia = DialectFactory.createDialect(this);
		
		return dia.getTotalRecorder(sql, params);
	}


    /**
     * ���ݲ��������ͽ��в�����ע�ᣨ����������͸�ֵ�������������
     *
     * @param cs:CallableStatement �����ò�����CallableStatementʵ����
     * @param params:ArrayList     - ��װ�˴洢���̵��������롢���������ÿ��
     *                             ������StoredProcParamObj����װ��
     */
    private CallableStatement prepareParams(CallableStatement cs,
			List<StoredProcParamObj> params) throws SQLException {
        Iterator<StoredProcParamObj> ii = params.iterator();
        StoredProcParamObj spp = null;
        while (ii.hasNext()) {
            spp = ii.next();
            if (spp.getParamType().equals(StoredProcParamObj.IN) ||
                    spp.getParamType().equals(StoredProcParamObj.INOUT)) {
                cs.setObject(spp.getIndex(),
                        spp.getValue(),
                        spp.getDataType());
            } else {
                cs.registerOutParameter(spp.getIndex(), spp.getDataType());
            }
        }

        return cs;
    }

    /**
     * ���ݴ洢���̵�ִ�н��������һ��Map���ͣ�������װ�洢���̵�ִ��<br>
     * ������������װ��һ��Collection���͵�ʵ���У����ս����˳����<br>
     * �ηŵ�Collection ʵ���С�
     *
     * @param cs:CallableStatement ִ�д洢���̺�õ���CallableStatement��
     * @return List ��װ��ִ�н��
     */
    private List<Object> setResult(CallableStatement cs, List<StoredProcParamObj> params)
			throws SQLException {
    	List<Object> result = new ArrayList<Object>();
        Iterator<StoredProcParamObj> ii = params.iterator();
        StoredProcParamObj spp = null;

        while (ii.hasNext()) {
            spp = ii.next();
            if (spp.getParamType().equals(StoredProcParamObj.OUT) ||
                    spp.getParamType().equals(StoredProcParamObj.INOUT)) {
            	
                Object obj = cs.getObject(spp.getIndex());
                
                if (obj != null && obj instanceof ResultSet) {
                    CachedRowSet crs = new CachedRowSet();
                    crs.populate((ResultSet) obj);
                    result.add(crs);
                } else {
                    result.add(obj);
                }

            }
        }

        return result;
    }
    
//    /**
//     * 
//     * <p>��ʽ�����sql��־</p>
//     *
//     * @param sql �������sql���
//     */
//    private void doSqlLog(String sql){
//    	String sql_log = (String) ApplicationContext.singleton().getValueByKey("sql-log");
//    	if("true".equalsIgnoreCase(sql_log)){
//    		logger.debug(sql);
//    	}
//    }
    
    /**
     * 
     * <p>����keyֵ���������ļ�sql-*.sql�ж�ȡsql���</p>
     *
     * @param key
     * @return
     * @throws BaseCheckedException
     */
    private String getSqlByKey(String key) throws BaseCheckedException{
    	String sql = ApplicationContext.singleton().getSqlByKey(key);
    	if(sql == null || sql.trim().length()==0){
    		logger.error("00000418: ����key[" + key + "]��ѯ������Ӧ��sql��䣬��˲������ļ��еĶ��壡");
    		List<String> params = new ArrayList<String>();
    		params.add(key);
    		throw new BaseCheckedException("00000418", params);
    	}

    	return sql;
    }
    
	/**
	 * 
	 * <p>����������µ�HQL���</p>
	 *
	 * @param bo
	 * @param session
	 * @param set
	 * @return
	 * @throws BaseCheckedException
	 */
	private StringBuffer singleUpdateHQLBuilder(PersistenceObject bo, Session session, 
			Set<String> set){
		
		return singleUpdateHQLBuilder(bo, session, null, set);
	}

	/**
	 * 
	 * <p>����������µ�HQL���</p>
	 *
	 * @param bo
	 * @param session
	 * @param hqlWhere
	 * @param columns
	 * @return
	 * @throws BaseCheckedException
	 */
	private StringBuffer singleUpdateHQLBuilder(PersistenceObject bo,Session session, 
			String hqlWhere, Set<String> columns) {
		StringBuffer updateHQL = new StringBuffer("update "); // ���ڸ��µ�HQL
		updateHQL.append(bo.getClass().getName()).append(" set ");
		Iterator<String> ite = bo.getStatus().keySet().iterator();
		while (ite.hasNext()) { // �ֱ�ƴ��Ҫ���µ�����
			String name = (String) ite.next();
			updateHQL.append(name).append(" = :").append(createParamName(name));
			if (ite.hasNext()) {
				updateHQL.append(" , ");
			}
		}

		updateHQL.append(" where ");
		if (null == hqlWhere || "".equals(hqlWhere.trim())) { // ��û��ȷ����where����ʱ��
			// ʹ��ID��Ϊ��������
			if (columns == null || columns.isEmpty()) {
				return updateHQL;
			}
			Iterator<String> ii = columns.iterator();
			while (ii.hasNext()) {
				// ȡ����������������
				String id = ii.next();
				// ��������Ϊ�������и���

				updateHQL.append(id).append(" = :old").append(id);
				if (ii.hasNext()) {
					updateHQL.append(" and ");
				}
			}
		} else {
			updateHQL.append(hqlWhere);
		}
		logger.debug("��ǰ��Ҫִ�е�HQLΪ��" + updateHQL);
		return updateHQL;
	}

	private String createParamName(String paramName) {
		return "new" + paramName;

	}    
	
	/**
	 * 
	 * <p>ΪQuery���ö�Ӧ�Ĳ���</p>
	 *
	 * @param query
	 * @param params
	 * @param boClass
	 */
	private void prepareQueryParams(Query query, Map<String, Object> params, Class<?> boClass) throws BaseCheckedException {
		Iterator<String> ii = params.keySet().iterator();
		// ȡ�ø�BO��������Ϣ
		while (ii.hasNext()) {
			String name = ii.next(); // ȡ�ò�������
			Object value = params.get(name); // ȡ�ò���ֵ
			
			String getMethod = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
			Method[] methods = boClass.getMethods();
			Class<?> type = null;
			for(int i=0; i<methods.length; i++){
				if(methods[i].getName().equals(getMethod)){
					Class<?>[] paramType = methods[i].getParameterTypes();
					if(paramType.length == 1){
						type = paramType[0];
					}
					
					break;
				}
			}
			methods[0].getParameterTypes();
			
			logger.debug("���ò�����" + "���ԣ�" + name + "---" + value);
			
			query.setParameter("new" + name, value, PersistenceUtils.judgeType(type));
		}
	}
}
