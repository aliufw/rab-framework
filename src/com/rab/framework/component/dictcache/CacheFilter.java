package com.rab.framework.component.dictcache;

import java.io.Serializable;

/**
 * 
 * <P>Title: CacheFilter</P>
 * <P>Description: </P>
 * <P>����˵������������������Ϣ��װ����</P>
 * <P>�����ݼ���ʱ,�޶����˷�ʽ fieldName = fieldValue</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class CacheFilter  implements Serializable{
	/**
	 * ���кű��
	 */
	private static final long serialVersionUID = -3988144657779225356L;

	//= <> in not in
	/**
	 * ����
	 */
	public final static String FILTER_OPERATOR_EQUAL = "=";
	
	/**
	 * ������
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
	 * �ֶ���
	 */
	private String fieldName;

	/**
	 * <p>��������ֵ</p>
	 * <p>���������λ��=����<>��ʱ��fieldValueΪ����ֵ</p>
	 * <p>���������λ��in����not in��ʱ��fieldValueΪList<Objectd></p>
	 */
	private Object fieldValue;

	/**
	 * ���˲�����,Ĭ��Ϊ"����"����
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

