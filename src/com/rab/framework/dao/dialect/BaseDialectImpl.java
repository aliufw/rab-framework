package com.rab.framework.dao.dialect;

import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.RefUtils;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: BaseDialect</P>
 * <P>Description: </P>
 * <P>程序说明：跨数据库访问方言处理基类</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public abstract class BaseDialectImpl implements Dialect  {
	
	/**
	 * 日志记录器
	 */
	protected static final LogWritter logger = LogFactory.getLogger(BaseDialectImpl.class);

	/**
	 * Hibernate Session
	 */
	private PersistenceDAO dao;


//	protected void doSqlLog(String str) {
//		String flag = (String) ApplicationContext.singleton().getValueByKey("sql-log");
//		if ("false".equals(flag)) {
//			return;
//		}
//		logger.error(str);
//	}
	
//	protected void doSqlLog(String str, List<?> sqlParams) {
//		String flag = (String) ApplicationContext.singleton().getValueByKey("sql-log");
//		if ("false".equals(flag)) {
//			return;
//		}
//		
//		String parms = "";
//		if(sqlParams != null){
//			for(int i=0; i<sqlParams.size(); i++){
//				parms += "" + sqlParams.get(i) + ", ";
//			}
//			if(parms.endsWith(", ")){
//				parms = parms.substring(0, parms.length()-2);
//			}
//		}
//		
//		str += "\tparams = [" + parms + "]";
//		
//		logger.error(str);
//	}

	protected String getSortSql(String sql, String sortFlag, String sortName) {
		
		String sqlTmp = sql.toLowerCase();
		int posB = sqlTmp.lastIndexOf("order by");
		String tmp = "";
		if(posB > 0){
			tmp = sql.substring(0, posB);
		}
		else{
			tmp = sql;
		}
		
		StringBuffer sb = new StringBuffer(tmp);
		sb.append(" order by ").append(sortName).append(" ").append(sortFlag);
		return sb.toString();
	}
	
	protected Method methodOfGet(Object boInstance, String fieldName)
			throws Exception {
		StringBuffer methodName = new StringBuffer("get");
		methodName.append(fieldName.substring(0, 1).toUpperCase());
		methodName.append(fieldName.substring(1, fieldName.length()));
		Class<?> cls = boInstance.getClass();
		return cls.getMethod(methodName.toString(), (Class[])null);
	}

	protected Method methodOfSet(Object boInstance, String fieldName)
			throws Exception {
		StringBuffer methodName = new StringBuffer("set");
		methodName.append(fieldName.substring(0, 1).toUpperCase());
		methodName.append(fieldName.substring(1, fieldName.length()));
		Class<?> cls = boInstance.getClass();
		return cls.getMethod(methodName.toString(), (Class[])null);
	}

	/**
	 * 生成PreparedStatement实例，然后为PreparedStatement<br>
	 * 实例添加属性（data中传递进来的）。最后返回PreparedStatement<br>
	 * 实例。PreparedStatement由参数con生成。PreparedStatement<br>
	 * 中需要设置的参数由sqlParam给出。方法可以根据参数类型将参数转换<br>
	 * 为该类型，并调用PreparedStatement中相应的setＸＸＸ方法设置参<br>
	 * 数。
	 * 
	 * @param con
	 *            :Connection 与数据库建立的连接
	 * @param sqlStr
	 *            :String 将要执行的sql语句
	 * @param sqlParams
	 *            :ArrayList SQL语句中的参数（“?”）
	 * @return PreparedStatement 生成的PreparedStatement实例
	 * @throws SQLException
	 */
	protected PreparedStatement preparedStatementCreate(Connection con,
			String sqlStr, List<?> sqlParams) throws SQLException {
		PreparedStatement ps = con.prepareStatement(sqlStr); // 生成preparedStatement实例

		if (sqlParams == null) {
			return ps;
		}
		Iterator<?> ii = sqlParams.iterator();
		int i = 1;

		while (ii.hasNext()) {
			// 判断各个参数的类型，并调用相应的setter方法；
			Object value = ii.next();
			String type = RefUtils.getDataType(value);
			if (type == null) {
				// java.sql.Array ;
				// ps.seta
				ps.setNull(i++, Types.CHAR);
			} else if (type.equals("String")) {
				ps.setString(i++, (String) value);
			} else if (type.equals("Long")) {
				ps.setLong(i++, ((Long) value).longValue());
			} else if (type.equals("Integer")) {
				ps.setInt(i++, ((Integer) value).intValue());
			} else if (type.equals("Double")) {
				ps.setDouble(i++, ((Double) value).doubleValue());
			} else if (type.equals("Date")) {
				ps.setDate(i++, ((Date) value));
			} else if (type.equals("Boolean")) {
				ps.setBoolean(i++, ((Boolean) value).booleanValue());
			} else if (type.equals("Short")) {
				ps.setShort(i++, ((Short) value).shortValue());
			} else if (type.equals("Time")) {
				ps.setTime(i++, ((Time) value));
			} else if (type.equals("Timestamp")) {
				ps.setTimestamp(i++, ((Timestamp) value));
			} else if (type.equals("Float")) {
				ps.setFloat(i++, ((Float) value).floatValue());
			} else if (type.equals("Blob")) {
				ps.setBlob(i++, ((Blob) value));
			} else if (type.equals("Clob")) {
				ps.setClob(i++, ((Clob) value));
			} else {
				ps.setObject(i++, value);
			}

		}
		return ps;
	}
	
	protected static List<Object> rowset2VOList(Class<?> voClazz, CachedRowSet rs) 
				throws BaseCheckedException{
		List<Object> result = new ArrayList<Object>();
		// 字段名
		String columnName = "";
		// vo属性名
		String attributeName = "";
		// get方法类型
		String columnType = "";
		Method[] methods = voClazz.getMethods();
		// ArrayList keyList = new ArrayList();
		Map<String,Method> methodMap = new HashMap<String,Method>();
		// 将set方法放到map中 过滤掉get方法和其他接口
		for (int j = 0; j < methods.length; j++) {
			String value = methods[j].getName();// value full method name
			// Class type = methods[j].getClass();
			String key = value.substring(3).trim();// key bo field name
			key = key.substring(0, 1).toLowerCase() + key.substring(1);
			Class<?> declarClass = methods[j].getDeclaringClass();// 声明此方法的类
			Constructor<?>[] constructor = declarClass.getConstructors();// 声明此方法的类的构造器
			if (constructor.length == 0 || declarClass.isInterface()
					|| "lrSj".equalsIgnoreCase(key)
					|| "xgSj".equalsIgnoreCase(key)) {// TODO: 为什么录入时间修改时间不做处理
				continue;
			}
			// 存放get方法
			if (!value.startsWith("set")) {
				methodMap.put("get" + key, methods[j]);
				continue;
			}
			// 存放set方法
			methodMap.put(key, methods[j]);
			// keyList.add(key);
		}
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			// 一条记录转换成一个vo
			while (rs.next()) {
				Object vo = voClazz.newInstance();
				// 字段匹配
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					columnName = metaData.getColumnName(i);
					// attributeName = (columnName.replaceAll("_", ""))
					// .toLowerCase();
					attributeName = convertDbColToMapKey(columnName);
					if (methodMap.containsKey(attributeName)) {
						Object obj = rs.getObject(columnName);
						// columnType = metaData.getColumnTypeName(i);
						// 根据key获得方法
						Method setMethod = (Method) methodMap
								.get(attributeName);

						Method getMethod = (Method) methodMap.get("get"
								+ attributeName);
						// get方法的返回值类型
						columnType = getMethod.getReturnType().getName();
						if (obj instanceof Clob) {
							if (columnType.equals("java.lang.String")) {
								// 处理clob为string
								Clob clob = (Clob) obj;
								// obj = clob.getSubString(0, (int)
								// clob.length());//todo 这样处理 太大的时候会出错

								Reader readerForClob = clob
										.getCharacterStream();
								BufferedReader bufReader = new BufferedReader(
										readerForClob);
								String line;
								StringBuffer temp = new StringBuffer();
								while ((line = bufReader.readLine()) != null) {
									temp.append(line + "\n");
								}
								bufReader.close();
								readerForClob.close();

								obj = temp.toString();

							}
						}
						// setMethod.invoke(vo, new Object[] { obj });
						setMethod.invoke(vo, new Object[] { classCast(
								columnType, obj) });
					}
				}
				result.add(vo);
			}
		} catch (Exception e) {
			logger.error("00000413: rowset2VOList:CacheRowSet转vo错误");
			throw new BaseCheckedException("00000413", e);
		}

		return result;
	}

	/**
	 * 
	 * convertDbColToMapKey <br>
	 * 根据数据库的列字段名将其按BO定义格式转换为BO中属性定义
	 * 
	 * @param dbCol
	 *            数据库的字段
	 * @return String BO定义格式属性
	 */
	public static String convertDbColToMapKey(String dbCol) {
		String mapKey = "";
		String str = dbCol.toLowerCase();
		String[] ss = str.split("_");
		StringBuffer key = new StringBuffer();
		if (ss.length > 1) {
			key.append(ss[0]);
			for (int j = 1; j < ss.length; j++) {
				key.append(ss[j].substring(0, 1).toUpperCase()).append(
						ss[j].substring(1));
			}
		} else {
			key.append(str);
		}
		mapKey = key.toString();
		return mapKey;
	}

	/**
	 * 根据对象类型造型
	 * 
	 * @param classType
	 *            造型结果类型
	 * @param obj
	 *            要造型的对象
	 * @return Object 造型结果
	 */
	public static Object classCast(String classType, Object obj) {
		Object result = null; // 造型结果
		try {
			if (classType != null && obj != null) {
				String temp = String.valueOf(obj);
				String classname = obj.getClass().getName();
				if (classType.endsWith("String")) {
					if (classname.startsWith("[L")) {
						String[] str = (String[]) obj;
						result = str[0];
					} else {
						result = temp;
					}
				} else if (classType.endsWith("Double")) {
					if ("java.lang.Double".equals(classname)) {
						result = (obj == null || "".equals(obj)) ? null
								: (Double) obj;
					} else {
						result = (temp == null || "".equals(temp)) ? null
								: new Double(temp);
					}
				} else if (classType.endsWith("Long")) {
					if ("java.lang.Long".equals(classname)) {
						result = (obj == null || "".equals(obj)) ? null
								: (Long) obj;
					} else {
						result = (temp == null || "".equals(temp)) ? null
								: new Long(temp);
					}
				} else if (classType.endsWith("Integer")) {
					if ("java.lang.Integer".equals(classname)) {
						result = (obj == null || "".equals(obj)) ? null
								: (Integer) obj;
					} else {
						result = (temp == null || "".equals(temp)) ? null
								: new Integer(temp);
					}
				} else if (classType.endsWith("double")) {
					if ("java.lang.Double".equals(classname)) {
						result = (Double) obj;
					} else {
						result = new Double(temp);
					}
				} else if (classType.endsWith("int")) {
					if ("java.lang.Integer".equals(classname)) {
						result = (Integer) obj;
					} else {
						result = new Integer(temp);
					}
				} else if (classType.endsWith("long")) {
					if ("java.lang.Long".equals(classname)) {
						result = (Long) obj;
					} else {
						result = new Long(temp);
					}
				} else if (classType.endsWith("BigDecimal")) {
					if ("java.math.BigDecimal".equals(classname)) {
						result = (obj == null || "".equals(obj)) ? null
								: (BigDecimal) obj;
					} else {
						result = (temp == null || "".equals(temp)) ? null
								: new BigDecimal(temp);
					}
				} else if (classType.endsWith("Calendar")) {
					result = toCalendar(obj);
				} else if (classType.endsWith("Date")) {
					result = toDate(obj);
				} else if (classType.endsWith("Timestamp")) {
					result = toTimestamp(obj);
				}
			}
		} catch (ClassCastException ex) {
			logger.error("class造型失败：", ex);
		}
		return result;
	}
	
	private static Object toTimestamp(Object obj) {
		if (obj instanceof java.sql.Timestamp) {
			return obj;

		} else if (obj instanceof java.sql.Date) {
			java.sql.Date objDate = (Date) obj;
			java.sql.Timestamp result = new Timestamp(objDate.getTime());
			return result;
		}
		if (obj instanceof java.util.Calendar) {
			Calendar objC = (Calendar) obj;
			java.sql.Timestamp result = new Timestamp(objC.getTimeInMillis());
			return result;
		} else {
			logger.info("字段类型:" + obj.getClass() + ",不能转换为：Timestamp,返回null");
			return null;
		}
	}

	private static Object toDate(Object obj) {
		if (obj instanceof java.util.Calendar) {
			java.util.Calendar cobj = (java.util.Calendar) obj;
			java.sql.Date date = new Date(cobj.getTimeInMillis());
			return date;
		} else if (obj instanceof java.sql.Timestamp) {
			java.sql.Timestamp timeStamp = (java.sql.Timestamp) obj;
			java.sql.Date date = new Date(timeStamp.getTime());
			return date;
		} else if (obj instanceof java.sql.Date) {
			return obj;
		} else {
			logger.info("字段类型:" + obj.getClass() + ",不能转换为：Date,返回null");
			return null;
		}
	}

	private static Object toCalendar(Object obj) {
		if (obj instanceof java.util.Calendar) {
			return obj;
		} else if (obj instanceof java.sql.Timestamp) {
			java.sql.Timestamp timeStamp = (java.sql.Timestamp) obj;
			java.util.Calendar result = new java.util.GregorianCalendar();
			result.setTimeInMillis(timeStamp.getTime());
			return result;
		} else if (obj instanceof java.sql.Date) {
			java.sql.Date date = (java.sql.Date) obj;
			java.util.Calendar result = new java.util.GregorianCalendar();
			result.setTime(date);
			return result;
		} else {
			logger.info("字段类型:" + obj.getClass() + ",不能转换为：Calendar,返回null");
			return null;
		}
	}

	public PersistenceDAO getDao() {
		return dao;
	}

	public void setDao(PersistenceDAO dao) {
		this.dao = dao;
	}
}
