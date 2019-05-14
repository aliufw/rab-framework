package com.rab.framework.component.dictcache;

import java.io.Serializable;

/**
 * 
 * <P>Title: CacheFilter</P>
 * <P>Description: </P>
 * <P>程序说明：过滤条件描述信息封装对象</P>
 * <P>在数据检索时,限定过滤方式 fieldName = fieldValue</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class CacheFilter  implements Serializable{
	/**
	 * 序列号标记
	 */
	private static final long serialVersionUID = -3988144657779225356L;

	//= <> in not in
	/**
	 * 等于
	 */
	public final static String FILTER_OPERATOR_EQUAL = "=";
	
	/**
	 * 不等于
	 */
	public final static String FILTER_OPERATOR_NOT_EQUAL = "<>";
	
	/**
	 * in
	 */
	public final static String FILTER_OPERATOR_IN = "in";
	
	/**
	 * not in
	 */
	public final static String FILTER_OPERATOR_NOT_IN = "not in";

	/**
	 * like
	 */
	public final static String FILTER_OPERATOR_LIKE = "like";

	/**
	 * 字段名
	 */
	private String fieldName;

	/**
	 * <p>过滤条件值</p>
	 * <p>当运算符号位“=”或“<>”时，fieldValue为单个值</p>
	 * <p>当运算符号位“in”或“not in”时，fieldValue为List<Objectd></p>
	 */
	private Object fieldValue;

	/**
	 * 过滤操作符,默认为"等于"符号
	 */
	private String filterOperator = "=";
	
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getFilterOperator() {
		return filterOperator;
	}

	public void setFilterOperator(String filterOperator) {
		this.filterOperator = filterOperator;
	}

}

