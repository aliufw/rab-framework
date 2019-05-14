package com.rab.framework.dao;

import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
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

import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.ByteType;
import org.hibernate.type.CalendarType;
import org.hibernate.type.CharacterType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimeType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.RefUtils;
import com.rab.framework.domain.po.PersistenceObject;

/**
 * 
 * <P>Title: PersistenceUtils</P>
 * <P>Description: </P>
 * <P>程序说明：持久层工具类</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-13</P>
 *
 */
public class PersistenceUtils {
	private static final LogWritter logger = LogFactory.getLogger(PersistenceUtils.class);
	
	/**
	 * <p>生成PreparedStatement实例，然后为PreparedStatement实例添加
	 * 属性（data中传递进来的）。最后返回PreparedStatement实例。
	 * PreparedStatement由参数con生成。PreparedStatement中需要设
	 * 置的参数由sqlParam给出。方法可以根据参数类型将参数转换为该类型，
	 * 并调用PreparedStatement中相应的setＸＸＸ方法设置参数。最后清除
	 * sqlParam中的全部属性。</p>
	 * 
	 * @param ps  与数据库建立的连接
	 * @param sqlParams  要执行的sql语句
	 * @return PreparedStatement - 生成的PreparedStatement实例
	 * 
	 * @throws SQLException
	 */
	public static PreparedStatement prepareSqlParams(PreparedStatement ps,
			List<?> sqlParams) throws SQLException {
		if (sqlParams == null || sqlParams.isEmpty()) {
			return ps;
		}
		Iterator<?> ii = sqlParams.iterator();
		int i = 1;

		while (ii.hasNext()) {
			Object value = ii.next();
			String type = RefUtils.getDataType(value);
			if (type == null) {
				ps.setNull(i++, Types.CHAR);
			} else if (type.equals("String")) {
				ps.setString(i++, (String) value);
			} else if (type.equals("Long")) {
				ps.setLong(i++, ((Long) value).longValue());
			} else if (type.equals("Integer")) {
				ps.setInt(i++, ((Integer) value).intValue());
			} else if (type.equals("Double")) {
				double dblValue = ((Double) value).doubleValue();
				if (Math.abs(dblValue) < Double.MIN_VALUE) {
					dblValue = 0.0;
				}
				ps.setDouble(i++, dblValue);

			} else if (type.equals("Date")) {
				if(value instanceof java.util.Date){
					value = new java.sql.Date(((java.util.Date)value).getTime());
				}
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
			} else if (value instanceof Calendar) {// cn
				Calendar value1 = (Calendar) value;
				ps.setTimestamp(i++, new Timestamp(value1.getTime().getTime()),
						(Calendar) value);
			} else {
				ps.setObject(i++, value);

			}

		}
		
		return ps;
	}
	
	
	/**
	 * 
	 * <p>将CachedRowSet转换成vo，推荐使用该方法</p>
	 *
	 * @param voClazz
	 * @param rs
	 * @return
	 */
	public static List<? extends PersistenceObject> rowset2VOList(Class<?> voClazz, CachedRowSet rs) throws Exception{
		List<PersistenceObject> result = new ArrayList<PersistenceObject>();
		// 字段名
		String columnName = "";
		// vo属性名
		String attributeName = "";
		// get方法类型
		String columnType = "";
		Method[] methods = voClazz.getMethods();
		// ArrayList keyList = new ArrayList();
		Map<String, Method> methodMap = new HashMap<String, Method>();
		// 将set方法放到map中 过滤掉get方法和其他接口
		for (int j = 0; j < methods.length; j++) {
			String value = methods[j].getName();// value full method name
			// Class type = methods[j].getClass();
			//isXxx 还是 getXxx 需要判断下
			String key = value.startsWith("is") ? value.substring(2).trim() : value.substring(3).trim();// key bo field name
			key = key.substring(0, 1).toLowerCase() + key.substring(1);
			Class<?> declarClass = methods[j].getDeclaringClass();// 声明此方法的类
			Constructor<?>[] constructor = declarClass.getConstructors();// 声明此方法的类的构造器
			if (constructor.length == 0 || declarClass.isInterface()
					|| "lrsj".equalsIgnoreCase(key)
					|| "xgsj".equalsIgnoreCase(key)) {// TODO: 为什么录入时间修改时间不做处理
				continue;
			}
			// 存放get方法
			if (!value.startsWith("set")) {
				if (value.startsWith("is")) {
					methodMap.put("is" + key, methods[j]);
				} else {
					methodMap.put("get" + key, methods[j]);
				}
				
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
				PersistenceObject bo = (PersistenceObject)voClazz.newInstance();
				// 字段匹配
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					columnName = metaData.getColumnName(i);
					// attributeName = (columnName.replaceAll("_", ""))
					// .toLowerCase();
//					attributeName = convertDbColToMapKey(columnName);
					//去掉转换，保持bo和数据库表字段命名统一
					attributeName = columnName.toLowerCase();
					if (methodMap.containsKey(attributeName)) {
						Object obj = rs.getObject(columnName);
						// columnType = metaData.getColumnTypeName(i);
						// 根据key获得方法
						Method setMethod = (Method) methodMap
								.get(attributeName);

						Method getMethod = (Method) methodMap.get("get"
								+ attributeName);
						if(getMethod == null){
							getMethod = (Method) methodMap.get("is"
									+ attributeName);
						} 
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
						setMethod.invoke(bo, new Object[] { classCast(columnType, obj) });
					}
				}
				result.add(bo);
			}
		} catch (Exception e) {
			throw e;
		} 

		return result;
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
	public static Object classCast(String classType, Object obj) throws ClassCastException {
		Object result = null; // 造型结果
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
			}else if (classType.endsWith("boolean")) {
				if ("java.math.BigDecimal".equals(classname)) {
					BigDecimal bd = (obj == null || "".equals(obj)) ? null
							: (BigDecimal) obj;
					if(bd != null){
						result = (bd.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE); 
					}
				} else {
					result = (temp == null || "".equals(temp)) ? null
							: new Boolean(temp);
				}
			} else if (classType.endsWith("Calendar")) {
				result = toCalendar(obj);
			} else if (classType.endsWith("Date")) {
				result = toDate(obj);
			} else if (classType.endsWith("Timestamp")) {
				result = toTimestamp(obj);
			}
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

//	/**
//	 * 该方法可以取得一个对象对应的Hibernate Type；
//	 * 
//	 * @param obj
//	 *            Object 对象
//	 * @return Type 对应该对象的Hibernate 类型
//	 */
//	public static Type judgeType1(Object obj)throws VHBaseCheckedException{
//		assert (obj != null);
//		Type type = null;
//
//		if (obj instanceof String) {
//			type = new StringType();
//		} else if (obj instanceof Integer) {
//			type = new IntegerType();
//		} else if (obj instanceof Long) {
//			type = new LongType();
//		} else if (obj instanceof Short) {
//			type = new ShortType();
//		} else if (obj instanceof Float) {
//			type = new FloatType();
//		} else if (obj instanceof Double) {
//			type = new DoubleType();
//		} else if (obj instanceof Date) {
//			type = new DateType();
//		} else if (obj instanceof Time) {
//			type = new TimeType();
//		} else if (obj instanceof Timestamp) {
//			type = new TimestampType();
//		} else if (obj instanceof Byte) {
//			type = new ByteType();
//		} else if (obj instanceof Boolean) {
//			type = new BooleanType();
//		} else if (obj instanceof BigDecimal) {
//			type = new BigDecimalType();
//		} else if (obj instanceof Calendar) {
//			type = new CalendarType();
//		} else if (obj instanceof Character) {
//			type = new CharacterType();
//		} else {
//			logger.error("00000433: 在创建HQL查询过程中，判定数据类型时出现异常(type=" + obj + ")!");
//			List<String> params = new ArrayList<String>();
//			params.add("" + obj);
//			throw new VHBaseCheckedException("00000433", params);
//		}
//		return type;
//	}

	/**
	 * 该方法可以取得一个对象对应的Hibernate Type；
	 * 
	 * @param claz 参数的数据类型
	 * 
	 * @return Type 对应该对象的Hibernate 类型
	 */
	public static Type judgeType(Class<?> claz)throws BaseCheckedException{
		assert (claz != null);
		Type type = null;

		if (claz.getName().equals("java.lang.String")) {
			type = new StringType();
		} 
		else if (claz.getName().equals("java.lang.Integer")) {
			type = new IntegerType();
		} 
		else if (claz.getName().equals("java.lang.Long")) {
			type = new LongType();
		} 
		else if (claz.getName().equals("java.lang.Short")) {
			type = new ShortType();
		} 
		else if (claz.getName().equals("java.lang.Float")) {
			type = new FloatType();
		} 
		else if (claz.getName().equals("java.lang.Double")) {
			type = new DoubleType();
		} 
//		else if (claz.getName().equals("java.util.Date")) {
//			type = new DateType();
//		} 
		else if (claz.getName().equals("java.sql.Date")) {
			type = new DateType();
		} 
		else if (claz.getName().equals("java.sql.Time")) {
			type = new TimeType();
		} 
		else if (claz.getName().equals("java.sql.Timestamp")) {
			type = new TimestampType();
		} 
		else if (claz.getName().equals("java.util.Calendar")) {
			type = new CalendarType();
		} 
		else if (claz.getName().equals("java.lang.Byte")) {
			type = new ByteType();
		} 
		else if (claz.getName().equals("java.lang.Boolean")) {
			type = new BooleanType();
		} 
		else if (claz.getName().equals("java.math.BigDecimal")) {
			type = new BigDecimalType();
		} 
		else if (claz.getName().equals("java.lang.Character")) {
			type = new CharacterType();
		} 
		else {
			logger.error("00000433: 在创建HQL查询过程中，判定数据类型时出现异常(type=" + claz + ")!");
			List<String> params = new ArrayList<String>();
			params.add("" + claz);
			throw new BaseCheckedException("00000433", params);
		}
		return type;
	}
	
	/**
	 * 
	 * <p>输出sql日志</p>
	 *
	 * @param str
	 */
	public static void doSqlLog(String str) {
		String flag = (String) ApplicationContext.singleton().getValueByKey("sql-log");
		if ("false".equals(flag)) {
			return;
		}
		logger.error(str);
	}

	/**
	 * 
	 * <p>输出sql日志</p>
	 *
	 * @param str
	 * @param sqlParams
	 */
	public static void doSqlLog(String str, List<?> sqlParams) {
		String flag = (String) ApplicationContext.singleton().getValueByKey("sql-log");
		if ("false".equals(flag)) {
			return;
		}
		
		String parms = "";
		if(sqlParams != null){
			for(int i=0; i<sqlParams.size(); i++){
				parms += "" + sqlParams.get(i) + ", ";
			}
			if(parms.endsWith(", ")){
				parms = parms.substring(0, parms.length()-2);
			}
		}
		
		str += "\tparams = [" + parms + "]";
		
		logger.error(str);
	}
	
}
