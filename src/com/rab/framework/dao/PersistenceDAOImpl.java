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
 * <P>程序说明：持久层对象</P>
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
	 * 持久层的Session, 客户可以直接拿来使用
	 */
	private Session session;

	/**
	 * DAO对象对应的数据源名称
	 */
	private String sessionFactoryName;
	
	/**
	 * domainSession回调
	 */
	private DomainSession domainSession;

	/**
	 * Hibernate事务管理对象
	 */
	private Transaction transaction;
	
	/**
	 * 构造器
	 * 
	 * @param dataSourceName 数据源名称
	 */
	public PersistenceDAOImpl(String sessionFactoryName){
		this.sessionFactoryName = sessionFactoryName;
	}
	
	//--------------------------------------------------------------------------第01类：环境管理接口
	
	/**
	 * 获得当前事务相关默认数据库连接
	 * 
	 * @return
	 */
	public Connection getConnection(){
		SessionImpl sessionImpl = (SessionImpl)session;
		return sessionImpl.getJDBCContext().borrowConnection();
		
	}

	/**
	 * 获得当前交易的Hibernate Session
	 * 
	 * @return org.hibernate.Session实例引用
	 */
	public Session getSession(){
		return this.session;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * 获得当前交易的DomainSession
	 * 
	 * @return com.rab.framework.domain.session.DomainSession 实例引用
	 */
	public DomainSession getDomainSession(){
		return this.domainSession;
	}
	
	public void setDomainSession(DomainSession domainSession) {
		this.domainSession = domainSession;
	}


	/**
	 * <p>关闭持久层会话</p>
	 *
	 */
	public void close(){
		this.session.close();
	}

	/**
	 * <p>启动事务</p>
	 *
	 */
	public void beginTransaction(){
		this.transaction = session.beginTransaction();
	}
	

	/**
	 * <p>回滚事务</p>
	 *
	 */
	public void rollbackTransaction(){
		if(this.transaction != null){
			this.transaction.rollback();
		}
	}

	/**
	 * 
	 * <p>提交事务</p>
	 *
	 */
	public void commitTransaction(){
		if(this.transaction != null){
			this.transaction.commit();
		}
	}

	//--------------------------------------------------------------------------第02类：insert操作接口

	/**
	 * <p>单表的insert操作，每次生成一条记录</p>
	 * <p>规则及要求：</p>
	 * 
	 * <p>1. 该方法在执行Insert动作的时候，不会对BO对应的级联属性做任何操作，
	 * 比如该BO对象中存在一个级联的子表对象，那么该方法不会对子表进行任何意义上的操作。</p>
	 * <p>2. 在建立HBM映射文件的时候，多表关联的cascade属性一律设置成为none</p>
	 * 
	 * @param bo 业务对象实例，内部包含有创建本条记录的相关数据
	 * 
	 * @return   返回新创建记录的主键键值 
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public Serializable insertSingleRow(PersistenceObject bo) throws BaseCheckedException{
		
		Serializable ret = null;
		
		if(bo == null){
			logger.error("00000402: 待执行 insertSingleRow(...) 操作的业务对象为null！");
			return ret;
		}
		
		registerBOforDebug(bo.getClass()); // 取得数据库连接
		
		if (hasLrsj(bo)) {
			bo.setLrsj(Calendar.getInstance());
		}
		
		try {
			ret = session.save(bo);
			
			session.flush();
		} catch (HibernateException e) {
			logger.error("00000402: 执行 insertSingleRow(...) 操作时发生异常！", e);
			throw new BaseCheckedException("00000402", e);
		} 
		finally {
		
		}
		
		return ret;
	}

	/**
	 * 
	 * <p>批量的insert操作</p>
	 * <p>规则及要求：</p>
	 * 
	 * <p>1. 该方法在执行Insert动作的时候，不会对BO对应的级联属性做任何操作，
	 * 比如该BO对象中存在一个级联的子表对象，那么该方法不会对子表进行任何意义上的操作。</p>
	 * <p>2. 在建立HBM映射文件的时候，多表关联的cascade属性一律设置成为none</p>
	 *
	 * @param bos 业务对象实例，内部包含有创建本条记录的相关数据
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public void insertBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException{
		if(listBO == null || listBO.size()==0){
			return;
		}
		
		Class<?> claz = listBO.get(0).getClass();
		registerBOforDebug(claz); // 取得数据库连接
		
		try {
			for(PersistenceObject bo : listBO){
				if (hasLrsj(bo)) {
					bo.setLrsj(Calendar.getInstance());
				}
				
				session.save(bo);
			}
			session.flush(); // 刷新数据库
		} catch (HibernateException e) {
			logger.error("00000403: 执行 insertBatchRow(...) 操作时发生异常！", e);
			throw new BaseCheckedException("00000403", e);
		} finally {
		}
	}
	

	//--------------------------------------------------------------------------第03类：update操作接口

	/**
	 * <p>单表的update操作，每次更新一条记录</p>
	 * <p>规则及要求：</p>
	 * 
	 * <p>1.如果属性没有使用对应的setter方法进行更新，则不会被执行更新动作；</p>
	 * <p>2.如果该BO任意一个属性的setter方法都没有被调用，则不执行update操作；</p>
	 * <p>3.如果一个BO的属性被执行了两次setter方法，则更新动作使用后者；</p>
	 * 
	 * @param bo 业务对象实例，内部包含有创建本条记录的相关数据
	 * 
	 * @return int 更新了多少条记录                        
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public int updateSingleRow(PersistenceObject bo) throws BaseCheckedException{
		
		int result = 0;
		try {
			registerBOforDebug(bo.getClass());
			
			if (hasXgsj(bo)) {
				bo.setXgsj(Calendar.getInstance());
			}

			// BO的属性没有更新，不执行更新动作
			if (bo.getStatus().isEmpty()) {
				logger.info("BO:" + bo.getClass().getName() + "属性没有改变，将不执行update动作");
				return 0;
			}
			
			Map<String, Object> columns = HibernateMetadataUtil.getIdInfo(this, bo);
			// 创建本次更新的HQL语句
			StringBuffer updateHQL = singleUpdateHQLBuilder(bo, session, columns.keySet());
			// 创建Query
			Query query = session.createQuery(updateHQL.toString());
			// 设置query对应的参数信息
			prepareQueryParams(query, bo.getStatus(), bo.getClass());
			// 设置主键的查询值
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
			logger.error("00000405: 执行数据单值更新时发生异常！", e);
			throw new BaseCheckedException("00000405", e);
		}

		return result;
	}
	
	/**
	 * <p>单表的update操作，每次更新多条记录</p>
	 * <p>规则及要求：</p>
	 * 
	 * <p>1.如果属性没有使用对应的setter方法进行更新，则不会被执行更新动作；</p>
	 * <p>2.如果该BO任意一个属性的setter方法都没有被调用，则不执行update操作；</p>
	 * <p>3.如果一个BO的属性被执行了两次setter方法，则更新动作使用后者；</p>
	 * 
	 * @param bo 业务对象实例，内部包含有创建本条记录的相关数据
	 * 
	 * @return int 更新了多少条记录                        
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
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
	 * <p>根据指定的sql更新数据库记录</p>
	 *
	 * @param sql     执行更新操作的sql语句
	 * @param params  执行更新操作的参数
	 * @return        更新了多少条记录
	 * 
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public int updateBatchRowBySQL(String sql, List<Object> params) throws BaseCheckedException{
		PersistenceUtils.doSqlLog(sql);
		
		PreparedStatement ps = null;
		int count;

		try {
			// 由Connection创建 PreparedStateme
			ps = this.getConnection().prepareStatement(sql);
			// 设置sql语句中参数
			ps = PersistenceUtils.prepareSqlParams(ps, params);
			// 执行删除操作
			count = ps.executeUpdate();
		} 
		catch (SQLException e) {
			logger.error("00000407: 执行数据批量条件更新时发生异常！", e);
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
	 * <p>根据指定的sqlKey更新数据库记录</p>
	 *
	 * @param key     执行更新操作的sql语句的key
	 * @param params  执行更新操作的参数
	 * @return        更新了多少条记录
	 * 
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public int updateBatchRowByKey(String key, List<Object> params) throws BaseCheckedException{
		String sql = getSqlByKey(key);
		return updateBatchRowBySQL(sql, params);
	}
	
//	/**
//	 * 
//	 * <p>根据指定where条件来更新数据表</p>
//	 * 
//	 * @param bo 被更新的BO对象的值，该类型的BO对象都被更新为该BO参数对应的属性值；
//	 * @param sqlWhere 该条件是以SQL形式所产生的where条件，例如 name = ?
//	 * @param params   填充where条件中的参数值
//	 *                        
//	 * @return int 更新了多少条记录                        
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
//	 */
//	public int updateBatchRowByConditionBySQL(BusinessObject bo,
//			String sqlWhere, List<Object> params) throws VHBaseCheckedException{
//		
//		return updateBatchRowByConditionBySQL(bo, null, sqlWhere, params);
//	}

//	/**
//	 * 
//	 * <p>根据指定where条件来更新数据表</p>
//	 * <p>sqlValue条件可以设置一些复杂的更新值，比如: age ＝ age ＋ 1</p>
//	 *
//	 * @param bo        被更新的BO对象的值，该类型的BO对象都被更新为该BO参数对应的属性值；
//	 * @param setValue  String 复杂的更新值表达式
//	 * @param sqlWhere  该条件是以SQL形式所产生的where条件，例如 name = ?
//	 * @param params    填充where条件中的参数值
//	 * 
//	 * @return int 更新了多少条记录                        
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
//	 */
//	public int updateBatchRowByConditionBySQL(BusinessObject bo,
//			String setValue, String sqlWhere, List<Object> params) throws VHBaseCheckedException{
//		if (bo.getStatus().isEmpty()) {
//			logger.info("BO:" + bo.getClass().getName() + "属性没有改变，将不执行update动作");
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
//		// 合并set的参数和where的参数
//		List<Object> setAndWhereParams = buildUpdateSetAndWhereParams(bo, params);
//		// 构建sql更新语句的框架
//		String sqlStr = updateSqlBuilder(bo, setValue, sqlWhere);
//		// 更新过程
//		PreparedStatement ps = null;
//		int result;
//
//		try {
//			// 由Connection创建 PreparedStateme
//			ps = this.getConnection().prepareStatement(sqlStr);
//			// 设置sql语句中参数
//			ps = PersistenceUtils.prepareSqlParams(ps, setAndWhereParams);
//			// 执行更新操作
//			result = ps.executeUpdate();
//		} catch (SQLException e) {
//			logger.error("00000407: 执行数据批量条件更新时发生异常！", e);
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
	//--------------------------------------------------------------------------第04类：delete操作接口

	/**
	 * <p>单表的delete操作，每次删除一条记录</p>
	 * <p>规则及要求：</p>
	 * 
	 * <p>1. 从数据库中删除该BO所对应的记录，该方法都是根据BO的主键来进行匹配删除</p>
	 * <p>2. 不支持联合主键。</p>
	 *
	 * @param bo 业务对象实例，内部包含有创建本条记录的相关数据
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public void deleteSingleRow(PersistenceObject bo) throws BaseCheckedException{
		if(bo == null){
			return ;
		}
		
		registerBOforDebug(bo.getClass());
		
		//1. 取数据表名称
		String tableName = HibernateMetadataUtil.getTableNameByClassName(this, bo.getClass().getName());

		//2. 取主键字段及其值信息
		Map<String, Object> idInfo = HibernateMetadataUtil.getIdInfo(this, bo);
		List<String> keys = new ArrayList<String>();
		List<Object> params = new ArrayList<Object>();
		
		Iterator<String> iter = idInfo.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			keys.add(key);
			params.add(idInfo.get(key));
		}

		//3.取主键对应的字段名
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
			logger.debug("删除单个BO数据：sql = " + sql + ", 条件参数：" + params);
			PreparedStatement pstmt = this.getConnection().prepareStatement(sql);
			pstmt = PersistenceUtils.prepareSqlParams(pstmt, params);
			
			pstmt.execute();
			
		} catch (SQLException e) {
			logger.error("00000408: 对BO执行删除操作时发生异常", e);
			throw new BaseCheckedException("00000408", e);
		}
	}
	
	/**
	 * 
	 * <p>批量的delete操作</p>
	 *
	 * @param lstBO 业务对象实例列表，内部包含有创建本条记录的相关数据
	 * 
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public void deleteBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException{
		if(listBO == null || listBO.size() == 0){
			return ;
		}
		
		registerBOforDebug(listBO.get(0).getClass());

		//1. 取数据表名称
		String tableName = HibernateMetadataUtil.getTableNameByClassName(this, listBO.get(0).getClass().getName());

		//2. 取主键字段及其值信息
		Map<String, Object> idMeta = HibernateMetadataUtil.getIdInfo(this, listBO.get(0));
		List<String> keys = new ArrayList<String>();
		Iterator<String> iter = idMeta.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			keys.add(key);
		}

		//3.取主键对应的字段名
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
			logger.debug("批量删除BO数据：sql = " + sql);
			PreparedStatement pstmt = this.getConnection().prepareStatement(sql);

			for(PersistenceObject bo : listBO){
				List<Object> params = new ArrayList<Object>();
				Map<String, Object> idInfo = HibernateMetadataUtil.getIdInfo(this, bo);
				for(String key : keys){
					params.add(idInfo.get(key));
				}
				logger.debug("批量删除BO数据：参数列表：" + params);

				pstmt = PersistenceUtils.prepareSqlParams(pstmt, params);
				
				pstmt.execute();
			}
		} catch (SQLException e) {
			logger.error("00000409: 对BO执行批量删除操作时发生异常");
			throw new BaseCheckedException("00000409", e);
		}
	}
	
	/**
	 * 
	 * <p>根据指定的sql语句来删除数据</p>
	 *
	 * @param sql     删除sql语句
	 * @param params  删除sql语句的条件参数列表
	 *                       
	 * @return  删除了多少条记录
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public int deleteBatchRowBySQL(String sql, List<Object> params) throws BaseCheckedException{

		PersistenceUtils.doSqlLog(sql);
		
		PreparedStatement ps = null;
		int count;

		try {
			// 由Connection创建 PreparedStateme
			ps = this.getConnection().prepareStatement(sql);
			// 设置sql语句中参数
			ps = PersistenceUtils.prepareSqlParams(ps, params);
			// 执行删除操作
			count = ps.executeUpdate();
		} 
		catch (SQLException ex) {
			logger.error("00000410: 对BO执行批量条件删除操作时发生异常");
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
	 * <p>根据指定的sqlkey来删除数据</p>
	 *
	 * @param key     删除sql语句的sqlkey
	 * @param params  删除sql语句的条件参数列表
	 *                       
	 * @return  删除了多少条记录
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public int deleteBatchRowByKey(String key, List<Object> params) throws BaseCheckedException{
		String sql = this.getSqlByKey(key);
		
		return deleteBatchRowBySQL(sql, params);
	}
	

	//--------------------------------------------------------------------------第05类：查询接口

//	/**
//	 * 
//	 * <p>返回全部查询结果，数据以BO List清单方式返回</p>
//	 *
//	 * @param boClaz 业务对象类，用作返回数据对象的原型class
//	 * @param sql    查询sql语句
//	 * @param params 查询参数
//	 * 
//	 * @return  返回 BO List
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
//	 */
//	public List<BusinessObject> queryToBOListBySQL(Class<?> boClaz,
//			String sql, List<?> params) throws VHBaseCheckedException{
//		
//		return queryPageToBOListBySQL(boClaz, sql, params, -1, -1);
//	}
//
//	/**
//	 * 
//	 * <p>返回全部查询结果，数据以BO List清单方式返回</p>
//	 *
//	 * @param boClaz 业务对象类，用作返回数据对象的原型class
//	 * @param key    待查询sql语句的key，依据该key从配置文件中读取sql语句
//	 * @param params 查询参数
//	 * 
//	 * @return  返回 BO List
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
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
	 * <p>返回全部查询结果，数据以CachedRowSet方式返回</p>
	 *
	 * @param sql    查询sql语句
	 * @param params 查询参数
	 * 
	 * @return  返回CachedRowSet
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public CachedRowSet queryToCachedRowSetBySQL(String sql, List<Object> params) 
		throws BaseCheckedException{
		
		Connection con = this.getConnection();
		try {
			PersistenceUtils.doSqlLog(sql, params); //输出日志
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt = PersistenceUtils.prepareSqlParams(pstmt, params);
			ResultSet rs = pstmt.executeQuery();
			CachedRowSet rowSet = new CachedRowSet(); 
			rowSet.populate(rs); 
			return rowSet;
		} catch (SQLException ex) {
			logger.error("00000435: 执行数据库普通查询时出现异常");
			throw new BaseCheckedException("00000435", ex);
		}
	}
	
	/**
	 * 
	 * <p>返回全部查询结果，数据以CachedRowSet方式返回</p>
	 *
	 * @param key    待查询sql语句的key，依据该key从配置文件中读取sql语句
	 * @param params 查询参数
	 * 
	 * @return  返回CachedRowSet
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public CachedRowSet queryToCachedRowSetByKey(String key, List<Object> params) 
		throws BaseCheckedException{
		
		String sql = getSqlByKey(key);
		
		return queryToCachedRowSetBySQL(sql, params);

	}
	
//	/**
//	 * 
//	 * <p>指定分页参数，以分页形式返回查询结果，数据以BO List清单方式返回</p>
//	 *
//	 * @param boClaz 业务对象类，用作返回数据对象的原型class
//	 * @param sql    查询sql语句
//	 * @param params 查询参数
//	 * @param rowsPerPage  每页数据行数
//	 * @param pageIndex    当前需要显示的页数
//	 * 
//	 * @return  返回 BOList
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
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
//	 * <p>指定分页参数，以分页形式返回查询结果，数据以BO List清单方式返回</p>
//	 *
//	 * @param boClaz 业务对象类，用作返回数据对象的原型class
//	 * @param key    待查询sql语句的key，依据该key从配置文件中读取sql语句
//	 * @param params 查询参数
//	 * @param rowsPerPage  每页数据行数
//	 * @param pageIndex 当前需要显示的页数
//	 * 
//	 * @return  返回 BOList
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
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
//	 * <p>指定分页参数，以分页形式返回查询结果，数据以BO List清单方式返回</p>
//	 *
//	 * @param boClaz 业务对象类，用作返回数据对象的原型class
//	 * @param sql    查询sql语句
//	 * @param params 查询参数
//	 * @param metaData  查询分页描述对象 
//	 * 
//	 * @return  返回 BOList
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
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
//	 * <p>指定分页参数，以分页形式返回查询结果，数据以BO List清单方式返回</p>
//	 *
//	 * @param boClaz 业务对象类，用作返回数据对象的原型class
//	 * @param key    待查询sql语句的key，依据该key从配置文件中读取sql语句
//	 * @param params 查询参数
//	 * @param metaData  查询分页描述对象 
//	 * 
//	 * @return  返回 BOList
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
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
	 * <p>用于前台分页查询，在应用时，应和前台分页应用一起考虑</p>
	 *
	 * @param sql       查询sql语句
	 * @param params    查询参数
	 * @param metaData  查询分页描述对象
	 * 
	 * @return   返回数据集
	 * @throws BaseCheckedException
	 */
	public CachedRowSet queryPageToCachedRowSetBySQL(String sql, List<Object> params, 
			PaginationMetaData metaData) throws BaseCheckedException{
		
//		int rowsPerPage = metaData.getRowsPerPage();
		int pageIndex = metaData.getPageIndex();
		if(pageIndex == 1){
			//首次查询，需返回总行数
			int totalRowNum = getTotalRecorder(sql, params);
			metaData.setTotalRowNum(totalRowNum);
		}
		
		logger.debug("本次请求的页数为第" + pageIndex + "页，数据总数是：" + metaData.getTotalRowNum() + "条。");

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
	 * <p>用于前台分页查询，在应用时，应和前台分页应用一起考虑</p>
	 *
	 * @param key       待查询sql语句的key，依据该key从配置文件中读取sql语句
	 * @param params    查询参数
	 * @param metaData  查询分页描述对象
	 * 
	 * @return   返回数据集
	 * @throws BaseCheckedException
	 */
	public CachedRowSet queryPageToCachedRowSetByKey(String key, List<Object> params, 
			PaginationMetaData metaData) throws BaseCheckedException{
		
		String sql = getSqlByKey(key);
		
		return queryPageToCachedRowSetBySQL(sql, params, metaData);
	}
	
	
//	/**
//	 * 
//	 * <p>指定分页参数，以分页形式返回查询结果，数据以CachedRowSet方式返回</p>
//	 *
//	 * @param sql    查询sql语句
//	 * @param params 查询参数
//	 * @param rowsPerPage  每页数据行数
//	 * @param pageIndex 当前需要显示的页数
//	 * 
//	 * @return  返回 CachedRowSet
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
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
//	 * <p>指定分页参数，以分页形式返回查询结果，数据以CachedRowSet方式返回</p>
//	 *
//	 * @param key    待查询sql语句的key，依据该key从配置文件中读取sql语句
//	 * @param params 查询参数
//	 * @param rowsPerPage  每页数据行数
//	 * @param pageIndex 当前需要显示的页数
//	 * 
//	 * @return  返回 CachedRowSet
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
//	 */
//	public CachedRowSet queryPageToCachedRowSetByKey(String key, List<Object> params, 
//			int rowsPerPage, int pageIndex) throws VHBaseCheckedException{
//	
//		String sql = getSqlByKey(key);
//		
//		return queryPageToCachedRowSetBySQL(sql, params, rowsPerPage, pageIndex);
//	}


	//--------------------------------------------------------------------------第06类：LOB对象操作

	/**
	 * 
	 * <p>将数据库中指定bo的blob数据以字节数组的形式返回</p>
	 *
	 * @param claz           要查询的BO对象类名
	 * @param id             要查询的记录的主键值
	 * @param blobFieldName  bo对象中将要取得的blob的属性名称
	 * 
	 * @return               包含BLOB数据的字节数组
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
	 * <p>将数据库中指定bo的blob数据写入指定的输出流中</p>
	 *
	 * @param claz           要查询的BO对象类名
	 * @param id             要查询的记录的主键值
	 * @param blobFieldName bo对象中将要取得的blob的属性名称
	 * @param os            将blob中数据写入此输出流中
	 * 
	 * @throws BaseCheckedException
	 */
	public void blobFetch(Class<?> boClaz, Serializable id, String blobFieldName,
			OutputStream os) throws BaseCheckedException {

		try {
			registerBOforDebug(boClaz);

			PersistenceObject bo = (PersistenceObject)session.load(boClaz, id);
			
			// 得到Blob字段的GET方法名
			StringBuffer methodName = new StringBuffer("get");
			methodName.append(blobFieldName.substring(0, 1).toUpperCase());
			methodName.append(blobFieldName.substring(1, blobFieldName.length()));

			Class<?>[] params = null;
			Method method = boClaz.getMethod(methodName.toString(), params);

			Object[] params2 = null;
			Blob blob = (Blob) method.invoke(bo, params2);

			if (blob == null) {
				logger.debug("in fetchBlob：bo"
						+ bo.getClass().getName() + "的blob属性:"
						+ blobFieldName + " 为空。");
				return;
			}

			InputStream is = blob.getBinaryStream();

			byte[] buf = new byte[10240];// 定义10k的缓存
			int len = is.read(buf);
			while (len > 0) {
				os.write(buf, 0, len);
				len = is.read(buf);
			}
			is.close();
			os.close();
			logger.debug("in fetchBlob：获取bo" + bo.getClass().getName() + "的blob:" + blobFieldName + " 数据完毕。");
		} catch (Exception ex) {
			logger.error("00000427: 读取BLOB数据(" + boClaz + ":" + blobFieldName + ":" + id + ")时出现异常", ex);
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
	 * <p>填充Blob字段</p>
	 *
	 * @param claz           要填充的BO对象类名
	 * @param id             要填充的记录的主键值
	 * @param blobFieldName bo对象中将要取得的blob的属性名称
	 * @param is            待写入的数据流
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
			
			// 得到Clob字段的SET方法名
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
			logger.error("00000426: 保存BLOB数据(" + boClaz + ":" + blobFieldName + ":" + id + ")时出现异常", ex);
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
	 * <p>该方法将数据库中指定bo的clob数据转换为string并返回</p>
	 * 
	 * @param claz           要查询的BO对象类名
	 * @param id             要查询的记录的主键值
	 * @param blobFieldName bo对象中将要取得的Clob的属性名称
	 * 
	 * @return String      返回clob转换成的字符串
	 * @throws BaseCheckedException
	 */
	public String clobFetch(Class<?> boClaz, Serializable id, String clobFieldName)
			throws BaseCheckedException {
		try {
			registerBOforDebug(boClaz);
			
			PersistenceObject bo = (PersistenceObject)session.load(boClaz, id);
			
			// 得到Clob字段的GET方法名
			StringBuffer methodName = new StringBuffer("get");
			methodName.append(clobFieldName.substring(0, 1).toUpperCase());
			methodName.append(clobFieldName.substring(1, clobFieldName.length()));


			Class<?>[] params = null;
			Method method = boClaz.getMethod(methodName.toString(), params);

			Object[] params2 = null;
			Clob clob = (Clob) method.invoke(bo, params2);

			if (clob == null) {
				logger.debug("in fetchClob：bo"
						+ boClaz + "的clob属性:"
						+ clobFieldName + " 为空,返回null.");
				return null;
			}

			Reader readerForClob = clob.getCharacterStream();

			// 获得clob的字符串
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
			logger.error("00000425: 读取CLOB数据(" + boClaz + ":" + clobFieldName + ":" + id + ")时出现异常", ex);
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
	 * <p>填充Clob字段</p>
	 *
	 * @param claz           要填充的BO对象类名
	 * @param id             要填充的记录的主键值
	 * @param clobFieldName  bo对象中将要填充的Clob的属性名称
	 * @param clobContent    填充的内容
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
			
			// 得到Clob字段的SET方法名
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
			logger.error("00000424: 保存CLOB数据(" + boClaz + ":" + clobFieldName + ":" + id + ")时出现异常", ex);
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

	
	//--------------------------------------------------------------------------第07类：存储过程操作
	

	/**
	 * 
	 * <p>执行数据库中以storedProcedure命名的存储过程</p>
	 *
	 * @param storedProcName   存储过程的名称。
	 * @param params           封装了存储过程的所有输入、输出参数，每个参数用StoredProcParamObj来封装。
	 * @return                 封装的执行结果
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
            prepareParams(cs, params); //设置参数
            cs.execute(); //执行存储过程
            List<Object> result = setResult(cs, params); //生成存储过程的结果
            
            return result;
        }
        catch (SQLException ex) {
			logger.error("00000417: 在执行存储过程时出现异常", ex);
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
	
	//--------------------------------------------------------------------------第08类：公用工具函数

	
	/**
	 * 取得数据库的时间，作为系统当前时间
	 * 
	 * @return 以Calendar的形式表示的数据库当前时间
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
			logger.error("00000404: 取得数据库标准时间发生异常！");
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
	 * <p>创建clob对象</p>
	 *
	 * @return
	 */
	public Clob createClob(String content){
		return Hibernate.createClob(content, session);
	}

	/**
	 * 
	 * <p>创建blob对象</p>
	 *
	 * @return
	 */
	public Blob createBlob(byte[] bytes){
		return Hibernate.createBlob(bytes, session);
	}

	/**
	 * 
	 * <p>根据名字，生成序列号，用于数据记录的主键</p>
	 *
	 * @param name  序列号名称
	 * 
	 * @return  返回序列号，用数据类型long表示
	 * @throws BaseCheckedException
	 */
	public long getSequence(String name) throws BaseCheckedException{
		long[] ret = null;

		ret = getSequence(name, 1);
		
		return ret[0];
	}

	/**
	 * 
	 * <p>根据名字，生成序列号，用于数据记录的主键</p>
	 *
	 * @param name  序列号名称
	 * @param num   一次性取序列号的数量
	 * 
	 * @return  返回序列号，用数据类型long[]表示
	 * @throws BaseCheckedException
	 */
	public long[] getSequence(String name, int num) throws BaseCheckedException{
		
		PersistenceDAO sysDao = this.domainSession.getSystemDaoWithoutTx();
		
		Dialect dialect = DialectFactory.createDialect(sysDao);

		logger.debug("取序列号 name = " + name + "\tnum = " + num);
		
		long[] ret = dialect.getSequence(name, num, sysDao);
		
		return ret;
	}

//	/**
//	 * 
//	 * <p>取系统通用的默认数据库连接，连接的名称是sys-without-tx，本连接不参与全局事务管理</p>
//	 * <p>注意：本连接在使用时，要注意自己控制事务和关闭连接！</p>
//	 *
//	 * @return 返回：数据库连接
//	 */
//	public Connection getSysConWithoutTx() throws VHBaseCheckedException{
//		
//		PersistenceDAO sysDao = this.domainSession.getSystemDaoWithoutTx();
//		
//		logger.info("创建不参与全局事务管理的数据库连接：sys-without-tx");
//		return sysDao.getConnection();
//	}

	public List<String> getMappings(){
		
		PersistenceManager pm = this.domainSession.getContext().getCtxPersistenceManagers().get(sessionFactoryName);
		
		List<String> list = pm.getLoadedORmappings();
		
		return list;
	}
	
	
	//私有方法===============================================================================================

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
				logger.error("00000401:持久层映射文件 " + className + " 没有在配置文件中注册");
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
//	 * <p>构建set和where的参数集合</p>
//	 *
//	 * @param bo
//	 * @param whereParams
//	 * @return
//	 */
//	private List<Object> buildUpdateSetAndWhereParams(BusinessObject bo, List<Object> whereParams) {
//		Iterator<String> ii = bo.getStatus().keySet().iterator();
//		int i = 0;
//		while (ii.hasNext()) {
//			// 得到要更新的属性名
//			String name = (String) ii.next();
//			// 将参数set和where合并
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
//	 * update方法的SQL语句生成方法，用来产生update的SQL语句。<br>
//	 * 该方法会根据sqlWhere和tableName属性生成SQL语句；<br>
//	 * tableName属性表示from；sqlWhere表示where，如果sqlWhere为<br>
//	 * null，则更新表中所有数据记录； SQL语句大体应该是这样生成的：<br>
//	 * UPDATE SET tableName name1=value1,name2=value2,..... WHERE sqlWhere
//	 * 
//	 * @return String 返回生成的SQL语句。
//	 */
//	private String updateSqlBuilder(BusinessObject bo, String setValues, String sqlWhere) {
//		StringBuffer sqlStr = new StringBuffer("update ");
//		String className = bo.getClass().getName();
//		// 获得当前要操作的table
//		sqlStr.append(HibernateMetadataUtil.getTableNameByClassName(this, className));
//		sqlStr.append(" set ");
//
//		// 获得bo要更新属性名的枚举
//		Iterator<String> ite = bo.getStatus().keySet().iterator();
//
//		while (ite.hasNext()) {
//			String propertyName = ite.next();
//			// 从bo属性名找到对应的表中列名
//			String columnName = HibernateMetadataUtil
//					.getColumnNameByPropertyName(this, className,
//							propertyName);
//			sqlStr.append(columnName).append("=?");
//			if (ite.hasNext()) {
//				sqlStr.append(" , ");
//			}
//		}
//
//		// 拼装其它SQL 的set条件
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
//	 * delete方法的SQL语句生成方法，用来产生delete的SQL语句。<br>
//	 * 该方法会根据sqlWhere和tableName属性生成SQL语句；<br>
//	 * tableName属性表示from；sqlWhere表示where，如果sqlWhere为<br>
//	 * null，则删除表中所有数据记录； SQL语句大体应该是这样生成的：<br>
//	 * DELETE FROM tableName WHERE sqlWhere
//	 * 
//	 * @return String 返回生成的SQL语句。
//	 */
//	private String deleteSqlBuilder(Class<?> boClass, String sqlWhere){
//		StringBuffer sqlStr = new StringBuffer("delete from ");
//
//		// 获得当前要操作的table
//		sqlStr.append(HibernateMetadataUtil.getTableNameByClassName(
//				this, boClass.getName()));
//
//		// 如果sql为null则删除所有记录
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
	 * <p>判断是否存在“录入时间”字段</p>
	 *
	 * @param bo BO对象
	 * @return  如果存在，返回true，否则，返回false
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
	 * <p>判断是否存在“修改时间”字段</p>
	 *
	 * @param bo BO对象
	 * @return  如果存在，返回true，否则，返回false
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
     * 根据参数的类型进行参数的注册（输出参数）和赋值（输入参数）。
     *
     * @param cs:CallableStatement 被设置参数的CallableStatement实例。
     * @param params:ArrayList     - 封装了存储过程的所有输入、输出参数，每个
     *                             参数用StoredProcParamObj来封装。
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
     * 根据存储过程的执行结果，生成一个Map类型，用来封装存储过程的执行<br>
     * 结果。结果被封装在一个Collection类型的实例中，按照结果的顺序依<br>
     * 次放到Collection 实例中。
     *
     * @param cs:CallableStatement 执行存储过程后得到的CallableStatement。
     * @return List 封装的执行结果
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
//     * <p>格式化输出sql日志</p>
//     *
//     * @param sql 待输出的sql语句
//     */
//    private void doSqlLog(String sql){
//    	String sql_log = (String) ApplicationContext.singleton().getValueByKey("sql-log");
//    	if("true".equalsIgnoreCase(sql_log)){
//    		logger.debug(sql);
//    	}
//    }
    
    /**
     * 
     * <p>根据key值，从配置文件sql-*.sql中读取sql语句</p>
     *
     * @param key
     * @return
     * @throws BaseCheckedException
     */
    private String getSqlByKey(String key) throws BaseCheckedException{
    	String sql = ApplicationContext.singleton().getSqlByKey(key);
    	if(sql == null || sql.trim().length()==0){
    		logger.error("00000418: 根据key[" + key + "]查询不到相应的sql语句，请核查配置文件中的定义！");
    		List<String> params = new ArrayList<String>();
    		params.add(key);
    		throw new BaseCheckedException("00000418", params);
    	}

    	return sql;
    }
    
	/**
	 * 
	 * <p>创建单表更新的HQL语句</p>
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
	 * <p>创建单表更新的HQL语句</p>
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
		StringBuffer updateHQL = new StringBuffer("update "); // 用于更新的HQL
		updateHQL.append(bo.getClass().getName()).append(" set ");
		Iterator<String> ite = bo.getStatus().keySet().iterator();
		while (ite.hasNext()) { // 分别拼凑要更新的属性
			String name = (String) ite.next();
			updateHQL.append(name).append(" = :").append(createParamName(name));
			if (ite.hasNext()) {
				updateHQL.append(" , ");
			}
		}

		updateHQL.append(" where ");
		if (null == hqlWhere || "".equals(hqlWhere.trim())) { // 当没有确定的where条件时，
			// 使用ID作为更新条件
			if (columns == null || columns.isEmpty()) {
				return updateHQL;
			}
			Iterator<String> ii = columns.iterator();
			while (ii.hasNext()) {
				// 取得主键的属性名称
				String id = ii.next();
				// 以主键作为条件进行更新

				updateHQL.append(id).append(" = :old").append(id);
				if (ii.hasNext()) {
					updateHQL.append(" and ");
				}
			}
		} else {
			updateHQL.append(hqlWhere);
		}
		logger.debug("当前将要执行的HQL为：" + updateHQL);
		return updateHQL;
	}

	private String createParamName(String paramName) {
		return "new" + paramName;

	}    
	
	/**
	 * 
	 * <p>为Query设置对应的参数</p>
	 *
	 * @param query
	 * @param params
	 * @param boClass
	 */
	private void prepareQueryParams(Query query, Map<String, Object> params, Class<?> boClass) throws BaseCheckedException {
		Iterator<String> ii = params.keySet().iterator();
		// 取得该BO的描述信息
		while (ii.hasNext()) {
			String name = ii.next(); // 取得参数名称
			Object value = params.get(name); // 取得参数值
			
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
			
			logger.debug("设置参数：" + "属性：" + name + "---" + value);
			
			query.setParameter("new" + name, value, PersistenceUtils.judgeType(type));
		}
	}
}
