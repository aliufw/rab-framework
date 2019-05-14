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
 * <P>程序说明：数据库访问接口</P>
 * 
 * <P>数据操作方法命名规则：</P>
 * <li>增：函数名以insert开头</li>
 * <li>删：函数名以delete开头</li>
 * <li>改：函数名以upate开头</li>
 * <li>查：函数名以query开头</li>
 * 
 * <li>批处理：函数名包含Batch字符串</li>
 * 
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-12</P>
 *
 */
public interface PersistenceDAO {

	//--------------------------------------------------------------------------第01类：环境管理接口
	
	/**
	 * 获得当前事务相关默认数据库连接
	 * 
	 * @return Connection
	 */
	public Connection getConnection();

	/**
	 * 获得当前交易的DomainSession
	 * 
	 * @return com.rab.framework.domain.session.DomainSession 实例引用
	 */
	public DomainSession getDomainSession();
	
	
//	/**
//	 * <p>启动事务</p>
//	 *
//	 */
//	public void beginTransaction();
//	
//	/**
//	 * <p>回滚事务</p>
//	 *
//	 */
//	public void rollbackTransaction();
//	
//	/**
//	 * 
//	 * <p>提交事务</p>
//	 *
//	 */
//	public void commitTransaction();
	
//	/**
//	 * <p>关闭持久层接口</p>
//	 *
//	 */
//	public void close();
	
	
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
	public Serializable insertSingleRow(PersistenceObject bo) throws BaseCheckedException;

	/**
	 * 
	 * <p>批量的insert操作</p>
	 * <p>规则及要求：</p>
	 * 
	 * <p>1. 该方法在执行Insert动作的时候，不会对BO对应的级联属性做任何操作，
	 * 比如该BO对象中存在一个级联的子表对象，那么该方法不会对子表进行任何意义上的操作。</p>
	 * <p>2. 在建立HBM映射文件的时候，多表关联的cascade属性一律设置成为none</p>
	 *
	 * @param listBO 业务对象实例，内部包含有创建本条记录的相关数据
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public void insertBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException;
	

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
	public int updateSingleRow(PersistenceObject bo) throws BaseCheckedException;

	/**
	 * <p>单表的update操作，每次更新多条记录</p>
	 * <p>规则及要求：</p>
	 * 
	 * <p>1.如果属性没有使用对应的setter方法进行更新，则不会被执行更新动作；</p>
	 * <p>2.如果该BO任意一个属性的setter方法都没有被调用，则不执行update操作；</p>
	 * <p>3.如果一个BO的属性被执行了两次setter方法，则更新动作使用后者；</p>
	 * 
	 * @param listBO 业务对象实例，内部包含有创建本条记录的相关数据
	 * 
	 * @return int 更新了多少条记录                        
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public int updateBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException;

	
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
	public int updateBatchRowBySQL(String sql, List<Object> params) throws BaseCheckedException;

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
	public int updateBatchRowByKey(String key, List<Object> params) throws BaseCheckedException;
	
	
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
//			String sqlWhere, List<Object> params) throws VHBaseCheckedException;

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
//			String setValue, String sqlWhere, List<Object> params) throws VHBaseCheckedException;
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
	public void deleteSingleRow(PersistenceObject bo) throws BaseCheckedException;
	
	/**
	 * 
	 * <p>批量的delete操作</p>
	 *
	 * @param listBO 业务对象实例列表，内部包含有创建本条记录的相关数据
	 * @throws BaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
	 */
	public void deleteBatchRow(List<? extends PersistenceObject> listBO) throws BaseCheckedException;
	
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
	public int deleteBatchRowBySQL(String sql, List<Object> params) throws BaseCheckedException;
	
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
	public int deleteBatchRowByKey(String key, List<Object> params) throws BaseCheckedException;
	

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
//			String sql, List<?> params) throws VHBaseCheckedException;
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
//			String key, List<?> params) throws VHBaseCheckedException;

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
		throws BaseCheckedException;

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
		throws BaseCheckedException;

//	/**
//	 * 
//	 * <p>指定分页参数，以分页形式返回查询结果，数据以BO List清单方式返回</p>
//	 *
//	 * @param boClaz 业务对象类，用作返回数据对象的原型class
//	 * @param sql    查询sql语句
//	 * @param params 查询参数
//	 * @param rowsPerPage  每页数据行数
//	 * @param pageIndex 当前需要显示的页数
//	 * 
//	 * @return  返回 BOList
//	 * @throws VHBaseCheckedException 当出现异常时，抛出VHBaseCheckedException异常。
//	 */
//	public List<BusinessObject> queryPageToBOListBySQL(Class<?> boClaz,
//			String sql, List<?> params, int rowsPerPage, int pageIndex) 
//				throws VHBaseCheckedException;
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
//				throws VHBaseCheckedException;
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
//				throws VHBaseCheckedException;
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
//				throws VHBaseCheckedException;

	
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
			PaginationMetaData metaData) throws BaseCheckedException;

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
			PaginationMetaData metaData) throws BaseCheckedException;


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
//			int rowsPerPage, int pageIndex) throws VHBaseCheckedException;

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
//			int rowsPerPage, int pageIndex) throws VHBaseCheckedException;



	//--------------------------------------------------------------------------第06类：LOB对象操作
	
	/**
	 * 
	 * <p>将数据库中指定bo的blob数据以字节数组的形式返回</p>
	 *
	 * @param boClaz           要查询的BO对象类名
	 * @param id             要查询的记录的主键值
	 * @param blobFieldName  bo对象中将要取得的blob的属性名称
	 * 
	 * @return               包含BLOB数据的字节数组
	 * @throws BaseCheckedException
	 */
	public byte[] blobFetch(Class<?> boClaz, Serializable id, String blobFieldName)
			throws BaseCheckedException ;

	/**
	 * 
	 * <p>将数据库中指定bo的blob数据写入指定的输出流中</p>
	 *
	 * @param boClaz           要查询的BO对象类名
	 * @param id             要查询的记录的主键值
	 * @param blobFieldName bo对象中将要取得的blob的属性名称
	 * @param os            将blob中数据写入此输出流中
	 * 
	 * @throws BaseCheckedException
	 */
	public void blobFetch(Class<?> boClaz, Serializable id, String blobFieldName,
			OutputStream os) throws BaseCheckedException ;

	
	/**
	 * 
	 * <p>填充Blob字段</p>
	 *
	 * @param boClaz           要填充的BO对象类名
	 * @param id             要填充的记录的主键值
	 * @param blobFieldName bo对象中将要取得的blob的属性名称
	 * @param data            待写入的数据流
	 * @throws BaseCheckedException
	 */
	public void blobFill(Class<?> boClaz, Serializable id, String blobFieldName, byte[] data)
			throws BaseCheckedException ;

	
	/**
	 * <p>该方法将数据库中指定bo的clob数据转换为string并返回</p>
	 * 
	 * @param boClaz           要查询的BO对象类名
	 * @param id             要查询的记录的主键值
	 * @param clobFieldName bo对象中将要取得的Clob的属性名称
	 * 
	 * @return String      返回clob转换成的字符串
	 * @throws BaseCheckedException
	 */
	public String clobFetch(Class<?> boClaz, Serializable id, String clobFieldName)
			throws BaseCheckedException ;
	
	/**
	 * 
	 * <p>填充Clob字段</p>
	 *
	 * @param boClaz           要填充的BO对象类名
	 * @param id             要填充的记录的主键值
	 * @param clobFieldName  bo对象中将要填充的Clob的属性名称
	 * @param clobContent    填充的内容
	 * @throws BaseCheckedException
	 */
	public void clobFill(Class<?> boClaz, Serializable id, String clobFieldName,
			String clobContent) throws BaseCheckedException;

	
	//--------------------------------------------------------------------------第07类：存储过程操作

	/**
	 * 
	 * <p>执行数据库中以storedProcName命名的存储过程</p>
	 *
	 * @param storedProcName   存储过程的名称。
	 * @param params           封装了存储过程的所有输入、输出参数，每个参数用StoredProcParamObj来封装。
	 * @return                 封装的执行结果
	 * 
	 * @throws BaseCheckedException
	 */
	public List<Object> storedProcedureInvoke(String storedProcName, List<StoredProcParamObj> params) 
				throws BaseCheckedException;

	
	//--------------------------------------------------------------------------第08类：公用工具函数

	/**
	 * 取得数据库的时间，作为系统当前时间
	 * 
	 * @return 以Calendar的形式表示的数据库当前时间
	 */
	public Calendar getDBTime() throws BaseCheckedException;


	/**
	 * 
	 * <p>创建clob对象</p>
	 *
	 * @return Clob
	 */
	public Clob createClob(String content);
	
	/**
	 * 
	 * <p>创建blob对象</p>
	 *
	 * @return Blob
	 */
	public Blob createBlob(byte[] bytes);


	/**
	 * 
	 * <p>根据名字，生成序列号，用于数据记录的主键</p>
	 *
	 * @param name  序列号名称
	 * 
	 * @return  返回序列号，用数据类型long表示
	 * @throws BaseCheckedException
	 */
	public long getSequence(String name) throws BaseCheckedException;
	
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
	public long[] getSequence(String name, int num) throws BaseCheckedException;
	
	
}
