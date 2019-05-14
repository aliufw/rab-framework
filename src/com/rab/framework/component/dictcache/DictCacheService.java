package com.rab.framework.component.dictcache;

import java.util.List;
import java.util.Map;

/**
 * 
 * <P>Title: CacheUserInterface</P>
 * <P>Description: </P>
 * <P>����˵��������Ӧ���û��ӿ�</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public interface DictCacheService {
	/**
	 * ���ݱ�����,�����������ݣ����ݷ�����̬ΪList������valueΪ������
	 * 
	 * @param tableName ������
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row]
	 */
	public List<Map<String,Object>> getCacheData(String tableName);
	
	/**
	 * ���ݱ�����,������������,���ݵķ�����̬ΪMap������keyΪָ���ֶε�ֵ��valueΪ��Ӧ��������
	 * 
	 * @param tableName  ������
	 * @param keyFieldName ���������б���������� "," �ָ�
	 * @return  ���������б�,��װ��ʽΪ: Map[map], �൱�ڱ�[row]
	 */
	public Map<String,Map<String,Object>> getCacheDataMap(String tableName, String keyFieldName);

	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�����ȡ��������ݣ����ݷ�����̬ΪList������valueΪ������</P>
	 *  <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * @param tableName ����
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row],�������map������Ϊ����field������Ӧ��ֵ
	 */
	public List<Map<String,Object>> getCacheData(String tableName, List<CacheFilter> filters);

	/**
	 * <P>���ݱ����ơ���ʼ��������š����������͹�������,�ӻ�����ȡ��������ݣ����ݷ�����̬ΪList������valueΪ������</P>
	 *  <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * @param tableName ����
	 * @param start     ��ʼ���������
	 * @param offset    ��Ҫ���ص���������
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @return          ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row],�������map������Ϊ����field������Ӧ��ֵ
	 */
	public List<Map<String,Object>> getCacheData(String tableName, int start, int offset, List<CacheFilter> filters);

	
	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�����ȡ���������.���ݵķ�����̬ΪMap������keyΪָ���ֶε�ֵ��valueΪ��Ӧ��������</P>
	 *  <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * @param tableName ����
	 * @param filters  ��������, ��װ��CacheFilter����ʵ����
	 * @param keyFieldName ���������б���������� "," �ָ�
	 * @return  ���������б�,��װ��ʽΪ: Map[map], �൱�ڱ�[row],�������map������Ϊ����field������Ӧ��ֵ
	 */
	public Map<String,Map<String,Object>> getCacheDataMap(String tableName, List<CacheFilter> filters, String keyFieldName);

	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�����ȡ��������ݣ����ݷ�����̬ΪList������valueΪ������</P>
	 * <P>���������ڷ��ڴ滺����ֵ���������ȡ��һ�������������ܴ󡢲��ʺ����ڴ滺����ֵ��</P>
	 * <P></P>
	 * @param tableName ����
	 * @param filterSQL   where����sql�ַ���,����where�ؼ���
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row]
	 */
	public List<Map<String,Object>> getCacheDataFromDB(String tableName, String filterSQL);

	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�������ȡһ�����ݡ�����ж���ƥ���н������ֻ���ص�һ����</P>
	 * <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * @param tableName ����
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @return ����������,��װ��ʽΪ: map,�൱�ڱ�[row]�� 
	 */
	public Map<String,Object> getCacheDataRow(String tableName, List<CacheFilter> filters);

	/**
	 * <P>���ݱ����ƺ͹�������,�ӻ�����ȡ��������ݡ�</P>
	 * <P>��������������</P>
	 * 		<li>=: CacheFilter.FILTER_OPERATOR_EQUAL</li>
	 * 		<li><>: CacheFilter.FILTER_OPERATOR_NOT_EQUAL</li>
	 * 		<li>in: CacheFilter.FILTER_OPERATOR_IN</li>
	 * 		<li>not in: CacheFilter.FILTER_OPERATOR_NOT_IN</li>
	 * 		<li>like: CacheFilter.FILTER_OPERATOR_LIKE</li>
	 * <P>ע�⣺</P>
	 * <P>1. CacheFilter�б��У����CacheFilter֮���ǡ��롱�Ĺ�ϵ</P>
	 * <p>2. ���������λ��=����<>��ʱ��CacheFilter.fieldValueΪ����ֵ</p>
	 * <p>3. ���������λ��in����not in��ʱ��CacheFilter.fieldValueΪList<Objectd></p>
	 * 
	 * <P>�������ݹ������£�</P>
	 * <P>1. ֻ����ָ���ֶε�һ��ֵ</P>
	 * <P>2. ����������������1����ֻ��������������ָ���ֶε�һ��ֵ</P>
	 * <P></P>
	 * 
	 * @param tableName ����
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @param colName   �ֶ���
	 * @return �����ֶ���key��Ӧ���ֶ�ֵ, ����ж���ƥ���н������ֻ���ص�һ��ƥ���е����ֵ��
	 */
	public Object getCacheValueByColname(String tableName, List<CacheFilter> filters, String colName);

	/**
	 * <p>������ֵ�������������ֶΣ����ڱ�ʾ���ֵ���������������б���ʾ��key��value�ֶ�</p>
	 * <p>���������Ǹ���ָ�����ֵ�����ƣ����ظñ���������ֶ�</p>
	 * <P></P>
	 * 
	 * @param tableName �ֵ������
	 * @return          ����һ���ַ������飬���а��������ַ���ֵ����һ����key���ֶ����ƣ��ڶ�����value���ֶ�����
	 */
	public String[] getKeyAndValueColName(String tableName);
	
	/**
	 * ��ȡ��������Ϣ
	 * 
	 * @param csbm ��������
	 * @return     ����ֵ
	 */
	public String getCacheXtcs(String csbm);
	
}
