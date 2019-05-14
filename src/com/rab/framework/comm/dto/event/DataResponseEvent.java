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
 * <P>程序说明：数据传输类，传输业务层返回数据到组件层。</P>
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
	 * 序列化编码
	 */
	private static final long serialVersionUID = -1358582630685113491L;
	private final static LogWritter logger = LogFactory.getLogger(DataResponseEvent.class);

	private ResComponentVO resComponentVO ;
	//缓存代码转名称信息：Map<widgetName, Map<列名, 缓存代码表名>>
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
	 * 将持久化数据（CachedRowSet）转化成WebfrmForm可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param crs
	 *            数据集
	 */
	public void addForm(String widgetName, CachedRowSet crs) throws Exception {
		addForm(widgetName, getColumnNameValueObjectMap(crs).size() > 0 ? getColumnNameValueObjectMap(crs).get(0) : null);
	}

	/**
	 * 将原生数据转化成WebfrmForm可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param map
	 *            Map数据
	 */
	public void addForm(String widgetName, Map<String, ?> map){
		if(map != null){
			ResFormVO formVO = new ResFormVO();
			formVO.setType(ComponentType.WebfrmForm);
			// 赋值
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
		// 遍历所有方法找到属性名
		List<String> fieldNameList = new ArrayList<String>();
		for (Method method : methodArray) {
			if (method.getName().indexOf("set") == 0) {
				fieldNameList.add(method.getName().substring(3));
			}
		}
		// 做一下过滤
		List<String> notFieldNameList = new ArrayList<String>();
		for (String fieldName : fieldNameList) {
			boolean isField = false;
			for (Method method : methodArray) {
				if (method.getName().indexOf("get" + fieldName) == 0
						|| method.getName().indexOf("is" + fieldName) == 0) {
					// 不是bo属性
					isField = true;
				}
			}
			if (!isField) {
				notFieldNameList.add(fieldName);
			}
		}
		fieldNameList.removeAll(notFieldNameList);
		//首字母转为小写
		for(String columnName : fieldNameList){
			result.add(columnName.substring(0, 1).toLowerCase()
			+ columnName.substring(1));
			//为适应前台的大小写不敏感，属性统一用小写 by ZhangBin since 10-08-04
//			result.add(columnName.toLowerCase());
		}
		return result;
	}
	/**
	 * 将一个BO转化成WebfrmForm可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param obj
	 *            BO
	 */
	public void addForm(String widgetName, PersistenceObject obj)  throws Exception {
		ResFormVO formVO = new ResFormVO();
		formVO.setType(ComponentType.WebfrmForm);

		List<String> fieldNameList = findBOFieldNameList(obj);
		// 赋值
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
	 * 将传入的Key，Value转化成WebfrmAttr组件可解析的数据。<br>
	 * 转化后，widgetName相当于这里的key。
	 * @param key
	 *            Attr的key
	 * @param value
	 *            Attr的value
	 */
	public void addAttr(String key, Object value) {
		AttrVO attrVO = new AttrVO();
		attrVO.setType(ComponentType.WebfrmAttr);
		attrVO.setOriginalValue(value);
		resComponentVO.getData().put(key,attrVO);
	}

	/**
	 * 传信息
	 *
	 *
	 * @param message
	 *           要传的信息
	 */
	public void addMessage(String message) {
		resComponentVO.setMessage(message);
	}
	/**
	 * 将一组BO转化成WebfrmGrid可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param objList
	 *            一组BO数据的List
	 */
	public void addTable(String widgetName, List<?> objList) throws Exception {
		addTable(widgetName, objList, null);
	}
	/**
	 * 将一组BO转化成WebfrmGrid可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param objList
	 *            一组BO数据的List
	 * @param paginationMetaData
	 *            分页信息
	 */
	public void addTable(String widgetName, List<?> objList, PaginationMetaData paginationMetaData) throws Exception {
		ResGridVO gridVO = new ResGridVO();
		gridVO.setType(ComponentType.WebfrmGrid);
		gridVO.setPageInfo(paginationMetaData);
		if(objList != null){
			ResTDSVO[] trs = new ResTDSVO[objList.size()];
			List<String> fieldNameList = findBOFieldNameList(objList.get(0));
			// 赋值
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
	 * 取出所有返回的WebfrmGrid数据的名称（页面表格名）。
	 *
	 * @return List<String>
	 * 		   表名列表
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
	 * 将持久化数据（CachedRowSet）转化成WebfrmGrid可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param crs
	 *            数据集
	 */
	public void addTable(String widgetName, CachedRowSet crs) throws Exception  {
		addTable(widgetName, crs, null);
	}

	/**
	 * 将持久化数据（CachedRowSet）转化成WebfrmGrid可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param crs
	 *            数据集
	 * @param paginationMetaData
	 *            分页信息
	 */
	public void addTable(String widgetName, CachedRowSet crs, PaginationMetaData paginationMetaData) throws Exception  {
		//addTable(widgetName, getColumnNameValueObjectMap(crs), paginationMetaData);
		addTableMap(widgetName, getColumnNameValueObjectMap(crs), paginationMetaData);
	}

	/**
	 * 将一组类似表数据结构的Map数据转化成WebfrmGrid可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param list
	 *            一组Map数据的List
	 */
	public void addTableMap(String widgetName, List<Map<String, Object>> list) {
		addTableMap(widgetName, list, null);
	}
	/**
	 * 将一组类似表数据结构的Map数据转化成WebfrmGrid可解析的数据。
	 *
	 * @param widgetName
	 *            前端组件名称
	 * @param list
	 *            一组Map数据的List
	 * @param paginationMetaData
	 *            分页信息
	 */
	public void addTableMap(String widgetName, List<Map<String, Object>> list, PaginationMetaData paginationMetaData){
		ResGridVO gridVO = new ResGridVO();
		gridVO.setType(ComponentType.WebfrmGrid);
		gridVO.setPageInfo(paginationMetaData);
		ResTDSVO[] trs = new ResTDSVO[list.size()];
		// 赋值
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
	 * 按照列数据类型取结果集中的数据并组织成MapList。 <br>
	 *
	 * @param crs
	 *            CachedRowSet
	 *
	 *
	 * @return List<Map<String, Object>>
	 * 			方便调用其它方法的名值对MapList
	 */
	private List<Map<String, Object>> getColumnNameValueObjectMap(CachedRowSet crs) throws Exception {
		if(crs == null){
			return null;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		// 取得CachedRowSet的列名及类型信息
		ResultSetMetaData rsmd;

		try {
			rsmd = crs.getMetaData();
			/*
			 * 此处无需细致处理 crs的字段类型，多数据库处理会比较麻烦
			 * By ZhangBin  2010-08-04
			 */

			int columnsCount = rsmd.getColumnCount();
			crs.beforeFirst();
			// 遍历CachedRowSet
			while (crs.next()) {
				Map<String, Object> nameValueMap = new HashMap<String, Object>();

				for(int i = 0;i<columnsCount;i++){
					String value =  converData(crs.getObject(i + 1));
					nameValueMap.put(rsmd.getColumnLabel(i + 1), value);
				}
				list.add(nameValueMap);
			}
		} catch (SQLException e) {
			logger.error("处理CachedRowSet出错：" + e.getMessage(), e);
			throw e;
		}
		return list;
	}

	/**
	 * 将CachedRowSet数据集转化成WebfrmTree可解析的数据。<br>
	 *
	 * @param widgetName
	 *            组件名称
	 * @param crs
	 *            数据集
	 * @param nameMap
	 * 			  映射信息
	 *
	 */
	public void addTree(String widgetName, CachedRowSet crs, Map<String, String> nameMap) throws Exception {
		 addTree(widgetName, getColumnNameValueObjectMap(crs), nameMap);
	}

	/**
	 * 将一组类似表数据结构的Map数据转化成WebfrmTree可解析的数据。<br>
	 *
	 * @param widgetName
	 *            组件名称
	 * @param treeDatas
	 *            查询出的树的数据
	 * @param nameMap
	 * 			  映射信息
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
	 * 将CachedRowSet数据集转化成WebfrmTree可解析的数据。<br>
	 * 此接口用来构建多选树，可加入筛选数据。<br>
	 *
	 * @param widgetName
	 *            组件名称
	 * @param crs
	 *            数据集
	 * @param checkCrs
	 *            用来筛选的数据集
	 *
	 * @param nameMap
	 * 			  映射信息
	 *
	 */
	public void addCheckTree(String widgetName, CachedRowSet crs, CachedRowSet checkCrs, Map<String, String> nameMap) throws Exception {
		addCheckTree(widgetName, getColumnNameValueObjectMap(crs), getColumnNameValueObjectMap(checkCrs), nameMap);
	}

	/**
	 * 将一组类似表数据结构的Map数据转化成WebfrmTree可解析的数据。<br>
	 * 此接口用来构建多选树，可加入筛选数据。<br>
	 *
	 * @param widgetName
	 *            组件名称
	 * @param treeDatas
	 *            查询出的树的数据
	 * @param checkDatas
	 *            用来筛选的数据
	 * @param nameMap
	 * 			  映射信息
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
	 * 用于页面跳转，传入页面的绝对路径。
	 *
	 * @param page
	 *            页面名称
	 */
	public void setPage(String page){
		this.page = page;
	}

	/**
	 * @return the page
	 * 			页面的绝对路径
	 */
	public String getPage() {
		return page;
	}

	/**
	 * 
	 * <p>properties类型的返回数据集</p>
	 *  
	 *  lfw 20110929 添加
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
	 * 将CachedRowSet持久化信息转化为WebfrmCombo组件可解析的数据。<br>
	 * 此接口传入widgetName，用于不同数据的Select。<br>
	 *
	 * @param widgetName
	 *            组件名称
	 * @param crs
	 *            数据集
	 */
	public void addCombo(String widgetName, CachedRowSet crs) throws Exception {
		addCombo(widgetName, getColumnNameValueObjectMap(crs));
	}

	/**
	 * 将原生数据转化为WebfrmCombo组件可解析的数据。<br>
	 * 此接口传入widgetName，用于不同数据的Select。<br>
	 *
	 * @param widgetName
	 *            组件名称
	 * @param list
	 *            一组类似表数据结构的Map数据的List
	 */
	public void addCombo(String widgetName, List<Map<String, Object>> list){
		addCombo(widgetName, list, list == null ? 0 : list.size());
	}

	/**
	 * 将原生数据转化为WebfrmCombo组件可解析的数据。<br>
	 * 此接口传入widgetName，用于不同数据的Select。<br>
	 *
	 * @param widgetName
	 *            组件名称
	 * @param list
	 *            一组类似表数据结构的Map数据的List
	 *
	 * @param total
	 *            加入数量
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
     * 为 cachedrowset 日期类型转换使用
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
	 * 用于Action中代码转名称调用
	 *
	 * @param widgetName
	 *            组件名称
	 * @param cacheMap
	 *           缓存代码转名称信息：Map<需转换字段名, 缓存代码表名>
	 *           缓存代码表名支持写成表名.字段名形式来取指定字段
	 */
	public void addCacheInfo(String widgetName, Map<String, String> cacheMap) {
		cacheInfo.put(widgetName, cacheMap);
	}

	/**取缓存代码转名称信息
	 * @return cacheInfo
	 * Map<widgetName, Map<列名, 缓存代码表名>>
	 */
	public Map<String, Map<String, String>> getCacheInfo() {
		return cacheInfo;
	}

	/**清除缓存代码转名称信息
	 *
	 */
	public void clearCacheInfo() {
		cacheInfo = null;
	}
}
