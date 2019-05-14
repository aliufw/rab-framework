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
 * <P>����˵�����־ò㹤����</P>
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
	 * <p>����PreparedStatementʵ����Ȼ��ΪPreparedStatementʵ�����
	 * ���ԣ�data�д��ݽ����ģ�����󷵻�PreparedStatementʵ����
	 * PreparedStatement�ɲ���con���ɡ�PreparedStatement����Ҫ��
	 * �õĲ�����sqlParam�������������Ը��ݲ������ͽ�����ת��Ϊ�����ͣ�
	 * ������PreparedStatement����Ӧ��set�أأط������ò�����������
	 * sqlParam�е�ȫ�����ԡ�</p>
	 * 
	 * @param ps  �����ݿ⽨��������
	 * @param sqlParams  Ҫִ�е�sql���
	 * @return PreparedStatement - ���ɵ�PreparedStatementʵ��
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
	 * <p>��CachedRowSetת����vo���Ƽ�ʹ�ø÷���</p>
	 *
	 * @param voClazz
	 * @param rs
	 * @return
	 */
	public static List<? extends PersistenceObject> rowset2VOList(Class<?> voClazz, CachedRowSet rs) throws Exception{
		List<PersistenceObject> result = new ArrayList<PersistenceObject>();
		// �ֶ���
		String columnName = "";
		// vo������
		String attributeName = "";
		// get��������
		String columnType = "";
		Method[] methods = voClazz.getMethods();
		// ArrayList keyList = new ArrayList();
		Map<String, Method> methodMap = new HashMap<String, Method>();
		// ��set�����ŵ�map�� ���˵�get�����������ӿ�
		for (int j = 0; j < methods.length; j++) {
			String value = methods[j].getName();// value full method name
			// Class type = methods[j].getClass();
			//isXxx ���� getXxx ��Ҫ�ж���
			String key = value.startsWith("is") ? value.substring(2).trim() : value.substring(3).trim();// key bo field name
			key = key.substring(0, 1).toLowerCase() + key.substring(1);
			Class<?> declarClass = methods[j].getDeclaringClass();// �����˷�������
			Constructor<?>[] constructor = declarClass.getConstructors();// �����˷�������Ĺ�����
			if (constructor.length == 0 || declarClass.isInterface()
					|| "lrsj".equalsIgnoreCase(key)
					|| "xgsj".equalsIgnoreCase(key)) {// TODO: Ϊʲô¼��ʱ���޸�ʱ�䲻������
				continue;
			}
			// ���get����
			if (!value.startsWith("set")) {
				if (value.startsWith("is")) {
					methodMap.put("is" + key, methods[j]);
				} else {
					methodMap.put("get" + key, methods[j]);
				}
				
				continue;
			}
			// ���set����
			methodMap.put(key, methods[j]);
			// keyList.add(key);
		}
		
		
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			// һ����¼ת����һ��vo
			while (rs.next()) {
				PersistenceObject bo = (PersistenceObject)voClazz.newInstance();
				// �ֶ�ƥ��
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					columnName = metaData.getColumnName(i);
					// attributeName = (columnName.replaceAll("_", ""))
					// .toLowerCase();
//					attributeName = convertDbColToMapKey(columnName);
					//ȥ��ת��������bo�����ݿ���ֶ�����ͳһ
					attributeName = columnName.toLowerCase();
					if (methodMap.containsKey(attributeName)) {
						Object obj = rs.getObject(columnName);
						// columnType = metaData.getColumnTypeName(i);
						// ����key��÷���
						Method setMethod = (Method) methodMap
								.get(attributeName);

						Method getMethod = (Method) methodMap.get("get"
								+ attributeName);
						if(getMethod == null){
							getMethod = (Method) methodMap.get("is"
									+ attributeName);
						} 
						// get�����ķ���ֵ����
						columnType = getMethod.getReturnType().getName();
						if (obj instanceof Clob) {
							if (columnType.equals("java.lang.String")) {
								// ����clobΪstring
								Clob clob = (Clob) obj;
								// obj = clob.getSubString(0, (int)
								// clob.length());//todo �������� ̫���ʱ������

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
	 * ���ݶ�����������
	 * 
	 * @param classType
	 *            ���ͽ������
	 * @param obj
	 *            Ҫ���͵Ķ���
	 * @return Object ���ͽ��
	 */
	public static Object classCast(String classType, Object obj) throws ClassCastException {
		Object result = null; // ���ͽ��
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
			logger.info("�ֶ�����:" + obj.getClass() + ",����ת��Ϊ��Timestamp,����null");
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
			logger.info("�ֶ�����:" + obj.getClass() + ",����ת��Ϊ��Date,����null");
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
			logger.info("�ֶ�����:" + obj.getClass() + ",����ת��Ϊ��Calendar,����null");
			return null;
		}
	}

//	/**
//	 * �÷�������ȡ��һ�������Ӧ��Hibernate Type��
//	 * 
//	 * @param obj
//	 *            Object ����
//	 * @return Type ��Ӧ�ö����Hibernate ����
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
//			logger.error("00000433: �ڴ���HQL��ѯ�����У��ж���������ʱ�����쳣(type=" + obj + ")!");
//			List<String> params = new ArrayList<String>();
//			params.add("" + obj);
//			throw new VHBaseCheckedException("00000433", params);
//		}
//		return type;
//	}

	/**
	 * �÷�������ȡ��һ�������Ӧ��Hibernate Type��
	 * 
	 * @param claz ��������������
	 * 
	 * @return Type ��Ӧ�ö����Hibernate ����
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
			logger.error("00000433: �ڴ���HQL��ѯ�����У��ж���������ʱ�����쳣(type=" + claz + ")!");
			List<String> params = new ArrayList<String>();
			params.add("" + claz);
			throw new BaseCheckedException("00000433", params);
		}
		return type;
	}
	
	/**
	 * 
	 * <p>���sql��־</p>
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
	 * <p>���sql��־</p>
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
