/**
 *
 */
package com.rab.framework.comm.dto.event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.comm.util.Constants;
import com.rab.framework.comm.util.TreeCreatorJson;
import com.rab.framework.domain.po.PersistenceObject;
import com.rab.framework.web.action.vo.ComponentType;
import com.rab.framework.web.action.vo.ResComponentVO;
import com.rab.framework.web.action.vo.data.AttrVO;
import com.rab.framework.web.action.vo.data.DataVO;
import com.rab.framework.web.action.vo.data.ResComboVO;
import com.rab.framework.web.action.vo.data.ResFormVO;
import com.rab.framework.web.action.vo.data.ResGridVO;
import com.rab.framework.web.action.vo.data.ResPropertiesVO;
import com.rab.framework.web.action.vo.data.ResTDSVO;
import com.rab.framework.web.action.vo.data.ResTreeVO;

/**
 *
 * <P>Title: DataResponseEvent</P>
 * <P>Description: </P>
 * <P>����˵�������ݴ����࣬����ҵ��㷵�����ݵ�����㡣</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-13</P>
 *
 */
public class DataResponseEvent extends BaseResponseEvent {
	/**
	 * ���л�����
	 */
	private static final long serialVersionUID = -1358582630685113491L;
	private final static LogWritter logger = LogFactory.getLogger(DataResponseEvent.class);

	private ResComponentVO resComponentVO ;
	//�������ת������Ϣ��Map<widgetName, Map<����, ����������>>
	private Map<String, Map<String, String>> cacheInfo = new HashMap<String, Map<String, String>>();
	private String page;

	public DataResponseEvent(){
		resComponentVO = new ResComponentVO();
	}

	/**
	 * @return the resComponentVO
	 */
	public ResComponentVO getResComponentVO() {
		return resComponentVO;
	}

	/**
	 * ���־û����ݣ�CachedRowSet��ת����WebfrmForm�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param crs
	 *            ���ݼ�
	 */
	public void addForm(String widgetName, CachedRowSet crs) throws Exception {
		addForm(widgetName, getColumnNameValueObjectMap(crs).size() > 0 ? getColumnNameValueObjectMap(crs).get(0) : null);
	}

	/**
	 * ��ԭ������ת����WebfrmForm�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param map
	 *            Map����
	 */
	public void addForm(String widgetName, Map<String, ?> map){
		if(map != null){
			ResFormVO formVO = new ResFormVO();
			formVO.setType(ComponentType.WebfrmForm);
			// ��ֵ
			Map<String, Map<String, Object>> data = new HashMap<String, Map<String, Object>>();
			for (String fieldName : map.keySet()) {
				Map<String, Object> valueMap = new HashMap<String, Object>();
				valueMap.put(Constants.originalValueName, map.get(fieldName));
				data.put(fieldName, valueMap);
			}
			formVO.setData(data);
			resComponentVO.getData().put(widgetName,formVO);
		}
	}

	private List<String> findBOFieldNameList (Object obj) throws Exception {
		List<String> result = new ArrayList<String> ();
		Method[] methodArray = obj.getClass().getDeclaredMethods();
		// �������з����ҵ�������
		List<String> fieldNameList = new ArrayList<String>();
		for (Method method : methodArray) {
			if (method.getName().indexOf("set") == 0) {
				fieldNameList.add(method.getName().substring(3));
			}
		}
		// ��һ�¹���
		List<String> notFieldNameList = new ArrayList<String>();
		for (String fieldName : fieldNameList) {
			boolean isField = false;
			for (Method method : methodArray) {
				if (method.getName().indexOf("get" + fieldName) == 0
						|| method.getName().indexOf("is" + fieldName) == 0) {
					// ����bo����
					isField = true;
				}
			}
			if (!isField) {
				notFieldNameList.add(fieldName);
			}
		}
		fieldNameList.removeAll(notFieldNameList);
		//����ĸתΪСд
		for(String columnName : fieldNameList){
			result.add(columnName.substring(0, 1).toLowerCase()
			+ columnName.substring(1));
			//Ϊ��Ӧǰ̨�Ĵ�Сд�����У�����ͳһ��Сд by ZhangBin since 10-08-04
//			result.add(columnName.toLowerCase());
		}
		return result;
	}
	/**
	 * ��һ��BOת����WebfrmForm�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param obj
	 *            BO
	 */
	public void addForm(String widgetName, PersistenceObject obj)  throws Exception {
		ResFormVO formVO = new ResFormVO();
		formVO.setType(ComponentType.WebfrmForm);

		List<String> fieldNameList = findBOFieldNameList(obj);
		// ��ֵ
		Map<String, Map<String, Object>> data = new HashMap<String, Map<String, Object>>();
		for (String fieldName : fieldNameList) {
			Field field = null;
			try {
				field = obj.getClass().getDeclaredField(fieldName);
			} catch (SecurityException e) {
				logger.debug(e.getMessage());
			} catch (NoSuchFieldException e) {
				logger.debug(e.getMessage());
			}
			if (field != null) {
				field.setAccessible(true);
				Map<String, Object> valueMap = new HashMap<String, Object>();
				try {
					valueMap.put(Constants.originalValueName, field.get(obj) == null ? null : field
							.get(obj).toString());
				} catch (IllegalArgumentException e) {
					throw e;
				} catch (IllegalAccessException e) {
					throw e;
				}
				data.put(fieldName, valueMap);
			}
		}
		formVO.setData(data);
		resComponentVO.getData().put(widgetName, formVO);
	}


	/**
	 * �������Key��Valueת����WebfrmAttr����ɽ��������ݡ�<br>
	 * ת����widgetName�൱�������key��
	 * @param key
	 *            Attr��key
	 * @param value
	 *            Attr��value
	 */
	public void addAttr(String key, Object value) {
		AttrVO attrVO = new AttrVO();
		attrVO.setType(ComponentType.WebfrmAttr);
		attrVO.setOriginalValue(value);
		resComponentVO.getData().put(key,attrVO);
	}

	/**
	 * ����Ϣ
	 *
	 *
	 * @param message
	 *           Ҫ������Ϣ
	 */
	public void addMessage(String message) {
		resComponentVO.setMessage(message);
	}
	/**
	 * ��һ��BOת����WebfrmGrid�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param objList
	 *            һ��BO���ݵ�List
	 */
	public void addTable(String widgetName, List<?> objList) throws Exception {
		addTable(widgetName, objList, null);
	}
	/**
	 * ��һ��BOת����WebfrmGrid�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param objList
	 *            һ��BO���ݵ�List
	 * @param paginationMetaData
	 *            ��ҳ��Ϣ
	 */
	public void addTable(String widgetName, List<?> objList, PaginationMetaData paginationMetaData) throws Exception {
		ResGridVO gridVO = new ResGridVO();
		gridVO.setType(ComponentType.WebfrmGrid);
		gridVO.setPageInfo(paginationMetaData);
		if(objList != null){
			ResTDSVO[] trs = new ResTDSVO[objList.size()];
			List<String> fieldNameList = findBOFieldNameList(objList.get(0));
			// ��ֵ
			for (int i = 0; i < objList.size(); i++) {
				Object row = objList.get(i);
				ResTDSVO tds = new ResTDSVO();
				Map<String, Map<String, Object>> data = new HashMap<String, Map<String, Object>>();

				for (String columnName : fieldNameList) {
					Field field = null;
					try {
						field = row.getClass().getDeclaredField(columnName);
					} catch (SecurityException e) {
						logger.debug(e.getMessage());
					} catch (NoSuchFieldException e) {
						logger.debug(e.getMessage());
					}
					if (field != null) {
						field.setAccessible(true);
						Map<String, Object> valueMap = new HashMap<String, Object>();
						try {
							valueMap.put(Constants.originalValueName, field.get(row) == null ? null : field
									.get(row).toString());
						} catch (IllegalArgumentException e) {
							throw e;
						} catch (IllegalAccessException e) {
							throw e;
						}
						data.put(columnName, valueMap);
					}
				}
				tds.setTds(data);
				trs[i] = tds;
			}
			gridVO.setTrs(trs);
		}
		resComponentVO.getData().put(widgetName, gridVO);
	}
	/**
	 * ȡ�����з��ص�WebfrmGrid���ݵ����ƣ�ҳ����������
	 *
	 * @return List<String>
	 * 		   �����б�
	 */
	public List<String> getTableNames(){
		List<String> tableNames = new ArrayList<String>();
		if(resComponentVO != null && resComponentVO.getData() != null && resComponentVO.getData().size() > 0){
			for(Entry<String, DataVO> entry : resComponentVO.getData().entrySet()){
				if(entry.getValue() != null && entry.getValue().getType().equals(ComponentType.WebfrmGrid)){
					tableNames.add(entry.getKey());
				}
			}
		}
		return tableNames;
	}

	/**
	 * ���־û����ݣ�CachedRowSet��ת����WebfrmGrid�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param crs
	 *            ���ݼ�
	 */
	public void addTable(String widgetName, CachedRowSet crs) throws Exception  {
		addTable(widgetName, crs, null);
	}

	/**
	 * ���־û����ݣ�CachedRowSet��ת����WebfrmGrid�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param crs
	 *            ���ݼ�
	 * @param paginationMetaData
	 *            ��ҳ��Ϣ
	 */
	public void addTable(String widgetName, CachedRowSet crs, PaginationMetaData paginationMetaData) throws Exception  {
		//addTable(widgetName, getColumnNameValueObjectMap(crs), paginationMetaData);
		addTableMap(widgetName, getColumnNameValueObjectMap(crs), paginationMetaData);
	}

	/**
	 * ��һ�����Ʊ����ݽṹ��Map����ת����WebfrmGrid�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param list
	 *            һ��Map���ݵ�List
	 */
	public void addTableMap(String widgetName, List<Map<String, Object>> list) {
		addTableMap(widgetName, list, null);
	}
	/**
	 * ��һ�����Ʊ����ݽṹ��Map����ת����WebfrmGrid�ɽ��������ݡ�
	 *
	 * @param widgetName
	 *            ǰ���������
	 * @param list
	 *            һ��Map���ݵ�List
	 * @param paginationMetaData
	 *            ��ҳ��Ϣ
	 */
	public void addTableMap(String widgetName, List<Map<String, Object>> list, PaginationMetaData paginationMetaData){
		ResGridVO gridVO = new ResGridVO();
		gridVO.setType(ComponentType.WebfrmGrid);
		gridVO.setPageInfo(paginationMetaData);
		ResTDSVO[] trs = new ResTDSVO[list.size()];
		// ��ֵ
		for (int i = 0; i < list.size(); i++) {
			ResTDSVO tds = new ResTDSVO();
			Map<String, Map<String, Object>> data = new HashMap<String, Map<String, Object>>();
			Map<String, ?> row = list.get(i);
			for (String columnName : row.keySet()) {
				Map<String, Object> valueMap = new HashMap<String, Object>();
				valueMap.put(Constants.originalValueName, row.get(columnName));
				data.put(columnName, valueMap);
			}
			tds.setTds(data);
			trs[i] = tds;
		}

		gridVO.setTrs(trs);
		resComponentVO.getData().put(widgetName, gridVO);
	}
	/**
	 * ��������������ȡ������е����ݲ���֯��MapList�� <br>
	 *
	 * @param crs
	 *            CachedRowSet
	 *
	 *
	 * @return List<Map<String, Object>>
	 * 			�������������������ֵ��MapList
	 */
	private List<Map<String, Object>> getColumnNameValueObjectMap(CachedRowSet crs) throws Exception {
		if(crs == null){
			return null;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		// ȡ��CachedRowSet��������������Ϣ
		ResultSetMetaData rsmd;

		try {
			rsmd = crs.getMetaData();
			/*
			 * �˴�����ϸ�´��� crs���ֶ����ͣ������ݿ⴦���Ƚ��鷳
			 * By ZhangBin  2010-08-04
			 */

			int columnsCount = rsmd.getColumnCount();
			crs.beforeFirst();
			// ����CachedRowSet
			while (crs.next()) {
				Map<String, Object> nameValueMap = new HashMap<String, Object>();

				for(int i = 0;i<columnsCount;i++){
					String value =  converData(crs.getObject(i + 1));
					nameValueMap.put(rsmd.getColumnLabel(i + 1), value);
				}
				list.add(nameValueMap);
			}
		} catch (SQLException e) {
			logger.error("����CachedRowSet����" + e.getMessage(), e);
			throw e;
		}
		return list;
	}

	/**
	 * ��CachedRowSet���ݼ�ת����WebfrmTree�ɽ��������ݡ�<br>
	 *
	 * @param widgetName
	 *            �������
	 * @param crs
	 *            ���ݼ�
	 * @param nameMap
	 * 			  ӳ����Ϣ
	 *
	 */
	public void addTree(String widgetName, CachedRowSet crs, Map<String, String> nameMap) throws Exception {
		 addTree(widgetName, getColumnNameValueObjectMap(crs), nameMap);
	}

	/**
	 * ��һ�����Ʊ����ݽṹ��Map����ת����WebfrmTree�ɽ��������ݡ�<br>
	 *
	 * @param widgetName
	 *            �������
	 * @param treeDatas
	 *            ��ѯ������������
	 * @param nameMap
	 * 			  ӳ����Ϣ
	 *
	 */
	public void addTree(String widgetName, List<Map<String, Object>> treeDatas, Map<String, String> nameMap) throws Exception {
		TreeCreatorJson tcj = new TreeCreatorJson();
		List<Map<String, Object>> nodes = tcj.getJsonTree(treeDatas, null, nameMap);
		ResTreeVO treeVO = new ResTreeVO();
		treeVO.setType(ComponentType.WebfrmTree);
		if(nodes != null){
			Map[] mapArray = new Map[nodes.size()];
			for (int i = 0; i < nodes.size(); i++) {
				mapArray[i] =  nodes.get(i);
			}
			treeVO.setData(mapArray);
		}
		resComponentVO.getData().put(widgetName, treeVO);
	}

	/**
	 * ��CachedRowSet���ݼ�ת����WebfrmTree�ɽ��������ݡ�<br>
	 * �˽ӿ�����������ѡ�����ɼ���ɸѡ���ݡ�<br>
	 *
	 * @param widgetName
	 *            �������
	 * @param crs
	 *            ���ݼ�
	 * @param checkCrs
	 *            ����ɸѡ�����ݼ�
	 *
	 * @param nameMap
	 * 			  ӳ����Ϣ
	 *
	 */
	public void addCheckTree(String widgetName, CachedRowSet crs, CachedRowSet checkCrs, Map<String, String> nameMap) throws Exception {
		addCheckTree(widgetName, getColumnNameValueObjectMap(crs), getColumnNameValueObjectMap(checkCrs), nameMap);
	}

	/**
	 * ��һ�����Ʊ����ݽṹ��Map����ת����WebfrmTree�ɽ��������ݡ�<br>
	 * �˽ӿ�����������ѡ�����ɼ���ɸѡ���ݡ�<br>
	 *
	 * @param widgetName
	 *            �������
	 * @param treeDatas
	 *            ��ѯ������������
	 * @param checkDatas
	 *            ����ɸѡ������
	 * @param nameMap
	 * 			  ӳ����Ϣ
	 *
	 */
	public void addCheckTree(String widgetName, List<Map<String, Object>> treeDatas, List<Map<String, Object>> checkDatas, Map<String, String> nameMap) throws Exception {
		TreeCreatorJson tcj = new TreeCreatorJson();
		List<Map<String, Object>> nodes = tcj.getJsonTree(treeDatas, checkDatas, nameMap);
		ResTreeVO treeVO = new ResTreeVO();
		treeVO.setType(ComponentType.WebfrmTree);
		if(nodes != null){
			Map[] mapArray = new Map[nodes.size()];
			for (int i = 0; i < nodes.size(); i++) {
				mapArray[i] =  nodes.get(i);
			}
			treeVO.setData(mapArray);
		}
		resComponentVO.getData().put(widgetName, treeVO);
	}

	/**
	 * ����ҳ����ת������ҳ��ľ���·����
	 *
	 * @param page
	 *            ҳ������
	 */
	public void setPage(String page){
		this.page = page;
	}

	/**
	 * @return the page
	 * 			ҳ��ľ���·��
	 */
	public String getPage() {
		return page;
	}

	/**
	 * 
	 * <p>properties���͵ķ������ݼ�</p>
	 *  
	 *  lfw 20110929 ���
	 *  
	 * @param widgetName
	 * @param props
	 * @throws Exception
	 */
	public void addProperties(String widgetName, Properties props) throws Exception {
		ResPropertiesVO propsVO = new ResPropertiesVO();
		propsVO.setType(ComponentType.WebfrmMultselect);
		propsVO.setProperties(props);
		
		resComponentVO.getData().put(widgetName, propsVO);
	}

	
	/**
	 * ��CachedRowSet�־û���Ϣת��ΪWebfrmCombo����ɽ��������ݡ�<br>
	 * �˽ӿڴ���widgetName�����ڲ�ͬ���ݵ�Select��<br>
	 *
	 * @param widgetName
	 *            �������
	 * @param crs
	 *            ���ݼ�
	 */
	public void addCombo(String widgetName, CachedRowSet crs) throws Exception {
		addCombo(widgetName, getColumnNameValueObjectMap(crs));
	}

	/**
	 * ��ԭ������ת��ΪWebfrmCombo����ɽ��������ݡ�<br>
	 * �˽ӿڴ���widgetName�����ڲ�ͬ���ݵ�Select��<br>
	 *
	 * @param widgetName
	 *            �������
	 * @param list
	 *            һ�����Ʊ����ݽṹ��Map���ݵ�List
	 */
	public void addCombo(String widgetName, List<Map<String, Object>> list){
		addCombo(widgetName, list, list == null ? 0 : list.size());
	}

	/**
	 * ��ԭ������ת��ΪWebfrmCombo����ɽ��������ݡ�<br>
	 * �˽ӿڴ���widgetName�����ڲ�ͬ���ݵ�Select��<br>
	 *
	 * @param widgetName
	 *            �������
	 * @param list
	 *            һ�����Ʊ����ݽṹ��Map���ݵ�List
	 *
	 * @param total
	 *            ��������
	 *
	 */
	public void addCombo(String widgetName, List<Map<String, Object>> list,long total){
		ResComboVO comboVO = new ResComboVO();
		comboVO.setType(ComponentType.WebfrmCombo);
		comboVO.setData(list);
		comboVO.setTotal(total);
		resComponentVO.getData().put(widgetName, comboVO);
	}

	/**
     * Ϊ cachedrowset ��������ת��ʹ��
     * @author ZhangBin
     * @since 2010-08-04
     * @param obj
     * @return String
     */
    public static String converData(Object obj) {
		if (obj == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (obj instanceof Calendar) {
			Calendar calendar = (Calendar) obj;
			return sdf.format(new java.util.Date(calendar.getTimeInMillis()));
		} else if (obj instanceof Timestamp) {
			Timestamp timestamp = (Timestamp) obj;
			return sdf.format(new java.util.Date(timestamp.getTime()));
		} else if (obj instanceof java.util.Date) {
			java.util.Date date = (java.util.Date) obj;
			return sdf.format(date);
		}
		return obj.toString();
	}
	/**
	 * ����Action�д���ת���Ƶ���
	 *
	 * @param widgetName
	 *            �������
	 * @param cacheMap
	 *           �������ת������Ϣ��Map<��ת���ֶ���, ����������>
	 *           ����������֧��д�ɱ���.�ֶ�����ʽ��ȡָ���ֶ�
	 */
	public void addCacheInfo(String widgetName, Map<String, String> cacheMap) {
		cacheInfo.put(widgetName, cacheMap);
	}

	/**ȡ�������ת������Ϣ
	 * @return cacheInfo
	 * Map<widgetName, Map<����, ����������>>
	 */
	public Map<String, Map<String, String>> getCacheInfo() {
		return cacheInfo;
	}

	/**����������ת������Ϣ
	 *
	 */
	public void clearCacheInfo() {
		cacheInfo = null;
	}
}
