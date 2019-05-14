package com.rab.framework.component.dictcache;

import java.util.List;
import java.util.Map;

import com.rab.framework.component.dictcache.manager.DictMetaInfo;

/**
 * 
 * <P>Title: CacheManager</P>
 * <P>Description: </P>
 * <P>����˵�����������ӿ�</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public interface DictCacheManager {
	/**
	 * �������������: ����ȫ����������
	 */
	public final static String SERVER_METHOD_LOADALL = "loadall";
	
	/**
	 * �������������: ����ָ�����ƵĻ�������
	 */
	public final static String SERVER_METHOD_LOADONE = "loadone";
	
	/**
	 * �������������: �����趨��sql���,����ָ�����ƵĻ�������
	 */
	public final static String SERVER_METHOD_LOADWITHSQL = "loadwithsql";
	
	/**
	 * �������������: ���µ�ǰ���������
	 */
	public final static String SERVER_METHOD_UPDATE = "update";
	
	/**
	 * ����ȫ����������
	 * 
	 */
	public void load();

	/**
	 * ���µ�ǰ���������
	 *
	 */
	public void update();
	
	/**
	 * ������ǰ��������,ֱ�Ӵӷ������˵����ݿ�����ȡ����
	 * @param tableName ����
	 * @param filters   ��������, ��װ��CacheFilter����ʵ����
	 * @return ���������б�,��װ��ʽΪ: List[map], �൱�ڱ�[row]
	 */
	public List<Map<String,Object>> getDataFromServer(String tableName, List<CacheFilter> filters);
	
	/**
	 * �������ͬ���߳�,���ڶ�ʱ�ӷ�������ɸ���
	 *
	 */
	public void startMonitor();
	
	
	/**
	 * 
	 * <p>ȡ�ֵ���ԭʼ�������ݣ���Ҫ�����ֵ��ĳ־û����ݸ���</p>
	 *
	 * @return
	 */
	public Map<String, DictMetaInfo> getDictMetaInfos();
}
