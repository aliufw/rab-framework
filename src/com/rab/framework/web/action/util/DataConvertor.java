package com.rab.framework.web.action.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.comm.util.Constants;
import com.rab.framework.component.dictcache.CacheFilter;
import com.rab.framework.component.dictcache.DictCacheService;
import com.rab.framework.component.dictcache.ServerCacheManager;
import com.rab.framework.web.action.vo.ComponentType;
import com.rab.framework.web.action.vo.ComponentVO;
import com.rab.framework.web.action.vo.data.AttrVO;
import com.rab.framework.web.action.vo.data.DataVO;
import com.rab.framework.web.action.vo.data.FormVO;
import com.rab.framework.web.action.vo.data.GridVO;
import com.rab.framework.web.action.vo.data.PropertiesVO;
import com.rab.framework.web.action.vo.data.ResFormVO;
import com.rab.framework.web.action.vo.data.ResGridVO;
import com.rab.framework.web.action.vo.data.ResTDSVO;
import com.rab.framework.web.action.vo.data.TDSVO;
import com.rab.framework.web.action.vo.data.UploadFileVO;
import com.rab.framework.web.dynamicsession.DynamicSessionManager;

/**
 *
 * <P>Title: DataConvertor</P>
 * <P>Description: </P>
 * <P>程序说明：数据转换类，主要进行请求与响应数据的格式转换工作。</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-12</P>
 *
 */
public class DataConvertor {
	protected static final LogWritter logger = LogFactory.getLogger(DataConvertor.class);
	
	private static String filterSpecialCharacter(String json){
		if(json != null){
			if(json.indexOf("%25") >= 0){
				//%转换
				return json.replaceAll("%25", "%");
			}else{
				return json;
			}
		}else{
			return null;
		}
	}
	/**
	 * 将页面请求的HttpServletRequest解析封装成系统DataRequestEvent对象<br>
	 *
	 * @param request
	 *            请求HttpServletRequest
	 * @param vhSessionId
	 *            vhSessionId           
	 *       
	 * @return req
	 * 			  系统DataRequestEvent
	 */
	public static DataRequestEvent convertRequest(HttpServletRequest request, String vhSessionId, String tid) throws Exception {
		String json = request.getParameter("json");
		if (json == null) {
			json = (String) request.getAttribute("json");
		}
		if (json == null) {
			logger.error("json请求为空.");
		}
		logger.debug("request json = " + json);
	
		ComponentVO componentVO = DataConvertor.convertJsonToVO(json, request);
		if(tid == null){
			if (componentVO.getTid().lastIndexOf("_") > 0) {
				tid = componentVO.getTid().substring(0,
				componentVO.getTid().lastIndexOf("_"));
			}
		}
		
		setAttrs(request, componentVO);		
		return new DataRequestEvent(tid, vhSessionId, componentVO);

	}
	private static void setAttrs(HttpServletRequest request, ComponentVO componentVO ){
		Map parameterMap = request.getParameterMap();
		Map<String, DataVO> data = new HashMap<String, DataVO>();
		for (Object parameterName : parameterMap.keySet()) {
			String parameter = (String) parameterName;
			if (!"tid".equalsIgnoreCase(parameter)
					&& !"json".equalsIgnoreCase(parameter)) {
				String[] parameterValue = (String[])parameterMap.get(parameter);
				if(parameterValue != null && parameterValue.length > 0 && !"null".equalsIgnoreCase(parameterValue[0])){
					AttrVO attrVO = new AttrVO();
					attrVO.setType(ComponentType.WebfrmAttr);
					attrVO.setOriginalValue(parameterValue[0]);
					data.put(parameter, attrVO);
				}
				
			}
		}
		if (componentVO.getData() == null) {
			componentVO.setData(data);
		} else {
			componentVO.getData().putAll(data);
		}
	}
	/**
	 * 将页面请求的json字符串解析封装成系统VO对象<br>
	 *
	 * @param json
	 *            请求字符串
	 * @return ComponentVO
	 * 			  系统VO
	 */
	public static ComponentVO convertJsonToVO(String json, HttpServletRequest request) throws Exception {
		if(json == null || json.trim().length() <= 0){
			return null;
		}
		json = filterSpecialCharacter(json);
		ComponentVO componentVO = new ComponentVO();

		JSONObject jo = JSONObject.fromObject(json);
		componentVO.setTid(getJsonStringValue(jo, "tid"));
		componentVO.setPage(getJsonStringValue(jo, "page"));
		componentVO.setAction(getJsonStringValue(jo, "action"));
		JSONObject data = getJsonObjectValue(jo, "data");
		Map<String, DataVO> dataMap = new HashMap<String, DataVO>();
		JSONArray objectNames = data.names();
		for (int i = 0; i < data.size(); i++) {
			JSONObject dataObject = getJsonObjectValue(data, objectNames.getString(i));
			switch (ComponentType.valueOf(ComponentType.class,getJsonStringValue(dataObject, "type"))) {
			case WebfrmAttr:

				AttrVO attrVO = new AttrVO();
				attrVO.setOriginalValue(getJsonStringValue(dataObject, Constants.originalValueName));
				attrVO.setType(ComponentType.valueOf(ComponentType.class,
						getJsonStringValue(dataObject, "type")));
				dataMap.put(objectNames.getString(i), attrVO);
				break;
			case WebfrmForm:

				FormVO formVO = new FormVO();
				formVO.setType(ComponentType.valueOf(ComponentType.class,
						getJsonStringValue(dataObject, "type")));
				formVO.setBeanName( getJsonStringValue(dataObject, "beanname"));
				JSONObject formData = getJsonObjectValue(dataObject, "data");
				JSONArray names = formData.names();
				Map<String, Map<String, Object>> nameValueMap = new HashMap<String, Map<String, Object>>();
				for (int j = 0; j < names.size(); j++) {
					JSONObject valueData =  getJsonObjectValue(formData, names.getString(j));
					Map<String, Object> valueMap = new HashMap<String, Object>();
					JSONArray valueNames = valueData.names();
					for (int k = 0; k < valueNames.size(); k++) {
						String key = valueNames.getString(k);
						Object value =  getObjectValue(valueData, key);
						valueMap.put(key, value);
					}
					String key = names.getString(j);
					//--------------------------------------
					//处理上传附件
					if("upload".equals(key)){
						valueMap = getUploadFiles(valueMap, request);
					}
					//--------------------------------------
					nameValueMap.put(key, valueMap);
				}
				formVO.setData(nameValueMap);
				dataMap.put(objectNames.get(i).toString(), formVO);
				break;
			case WebfrmVouchGrid:
			case WebfrmGrid:

				GridVO gridVO = new GridVO();
				gridVO.setType(ComponentType.valueOf(ComponentType.class,
						getJsonStringValue(dataObject, "type")));
				gridVO.setBeanName( getJsonStringValue(dataObject, "beanname"));
				//如果有设置分页信息
				if( getJsonObjectValue(dataObject, "pageInfo") != null){
					PaginationMetaData paginationMetaData = new PaginationMetaData();
					JSONObject pageInfo =  getJsonObjectValue(dataObject, "pageInfo");

					if(getJsonIntegerValue(pageInfo, "pageIndex") != null){
						paginationMetaData.setPageIndex(getJsonIntegerValue(pageInfo, "pageIndex"));
					}
					if(getJsonIntegerValue(pageInfo, "rowsPerPage") != null){
						paginationMetaData.setRowsPerPage(getJsonIntegerValue(pageInfo, "rowsPerPage"));
					}
					if(getJsonIntegerValue(pageInfo, "totalRowNum") != null){
						paginationMetaData.setTotalRowNum(getJsonIntegerValue(pageInfo, "totalRowNum"));
					}
					paginationMetaData.setSortFieldName(getJsonStringValue(pageInfo, "sortFieldName"));
					paginationMetaData.setSortFlag(getJsonStringValue(pageInfo, "sortFlag"));
					if(getJsonStringValue(pageInfo, "pageModelFlag") != null){
						paginationMetaData.setPageModelFlag(getJsonStringValue(pageInfo, "pageModelFlag"));
					}
					if(getJsonStringValue(pageInfo, "cacheName") != null){
						paginationMetaData.setCacheName(getJsonStringValue(pageInfo, "cacheName"));
					}
					gridVO.setPageInfo(paginationMetaData);
				}
				JSONArray gridTrs = getJsonArrayValue(dataObject, "trs");
				if(gridTrs != null && gridTrs.size() > 0){
					TDSVO[] tdsArray = new TDSVO[gridTrs.size()];
					for (int a = 0; a < gridTrs.size(); a++) {
						TDSVO tDSVO = new TDSVO();
						JSONObject tr =  (JSONObject)gridTrs.get(a);
						tDSVO.setStatus( getJsonStringValue(tr, "status"));
						JSONObject tds =  getJsonObjectValue(tr, "tds");
						JSONArray tdsNames = tds.names();
						Map<String, Map<String, Object>> nameValuesMap = new HashMap<String, Map<String, Object>>();
						for (int j = 0; j < tdsNames.size(); j++) {
							JSONObject valueData =   getJsonObjectValue(tds, tdsNames.getString(j));
							Map<String, Object> valueMap = new HashMap<String, Object>();
							JSONArray valueNames = valueData.names();
							for (int k = 0; k < valueNames.size(); k++) {
								valueMap.put(valueNames.getString(k), getObjectValue(valueData, valueNames.getString(k)));
							}
							nameValuesMap.put(tdsNames.getString(j), valueMap);
						}
						tDSVO.setTds(nameValuesMap);
						tdsArray[a] = tDSVO;
					}
					gridVO.setTrs(tdsArray);
				}
				dataMap.put(objectNames.get(i).toString(), gridVO);
				break;
				
			case WebfrmMultselect:
				PropertiesVO propsVO = new PropertiesVO();
				
				//JSONObject dataObject
				
				JSONArray propsList = getJsonArrayValue(dataObject, "data");
				Properties props = new Properties();
				for(int k=0; k<propsList.size(); k++){
					JSONObject item =  (JSONObject)propsList.get(k);

					String key = item.getString("key");
					String value = item.getString("value");
					
					props.setProperty(key, value);
				}
				
				propsVO.setProps(props);
				
				dataMap.put(objectNames.get(i).toString(), propsVO);

				break;
			}

		}
		componentVO.setData(dataMap);

		return componentVO;
	}

	private static Map<String, Object> getUploadFiles(Map<String, Object> valueMap, HttpServletRequest request){
		Map<String, Object> ret = new HashMap<String, Object>();
		
		Map<String, UploadFileVO> upfiles = (Map<String, UploadFileVO>)request.getSession().getAttribute(UploadFileVO.tmpCacheName);
		
		if(upfiles == null){
			return ret;
		}
		
		Iterator<Object> iter = valueMap.values().iterator();
		while(iter.hasNext()){
			String filenames = "" + iter.next();
			
			StringTokenizer st = new StringTokenizer(filenames, ";");
			while(st.hasMoreTokens()){
				String filename = st.nextToken();
				UploadFileVO uf = upfiles.get(filename);
				if(uf != null){
					ret.put(filename, uf);
					upfiles.remove(filename);
				}
			}
		}
		
		return ret;
	}
	
	private static String getJsonStringValue(JSONObject jsonObject, String name) {
		String result = null;
		try {
			result = jsonObject.getString(name);
			if(result != null && result.trim().equalsIgnoreCase("null")){
				result = null;
			}
		} catch (Exception e) {
		}
		return result;
	}

	private static Integer getJsonIntegerValue(JSONObject jsonObject, String name) {
		Integer result = null;
		try {
			result = jsonObject.getInt(name);
		} catch (Exception e) {
		}
		return result;
	}

	private static JSONObject getJsonObjectValue(JSONObject jsonObject, String name) {
		JSONObject result = null;
		try {
			result = jsonObject.getJSONObject(name);
			if(result.isNullObject()){
				return null;
			}
		} catch (Exception e) {
		}
		return result;
	}
	private static Object getObjectValue(JSONObject jsonObject, String name) {
		Object result = null;
		try {
			result = jsonObject.get(name);
			if(result instanceof JSONObject){
				if(((JSONObject)result).isNullObject()){
					return null;
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
	private static JSONArray getJsonArrayValue(JSONObject jsonObject, String name) {
		JSONArray result = null;
		try {
			result = jsonObject.getJSONArray(name);
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 将页面请求的json字符串解析封装到DataResponseEvent中的分页信息里<br>
	 *
	 * @param dataResponseEvent
	 *            response DTO
	 * @param json
	 *            请求字符串
	 */
	public static void processPageInfoData(HttpServletRequest request, DataResponseEvent dataResponseEvent, String json) {
		if(dataResponseEvent != null && dataResponseEvent.getResComponentVO() != null && dataResponseEvent.getResComponentVO().getData() != null
				&& dataResponseEvent.getResComponentVO().getData().size() > 0)
		for(Entry<String, DataVO> dataEntry : dataResponseEvent.getResComponentVO().getData().entrySet()){
			DataVO data = dataEntry.getValue();
			if(data != null && ComponentType.WebfrmGrid.equals(data.getType())){
				//表格类型，检查是否有分页信息
				ResGridVO resGridVO = (ResGridVO)data;
				if(resGridVO.getPageInfo() != null){
					resGridVO.getPageInfo().setQueryParams(json);
					if(resGridVO.getPageInfo().getPageModelFlag() != null && resGridVO.getPageInfo().getPageModelFlag().equalsIgnoreCase(resGridVO.getPageInfo().PAGE_MODEL_SESSION)){
						//需要算一下总页数
						long time = Calendar.getInstance().getTimeInMillis();
						resGridVO.getPageInfo().setTotalRowNum(resGridVO.getTrs().length);
						DynamicSessionManager.singleton().setData(request, dataEntry.getKey() + time, resGridVO);
						resGridVO.getPageInfo().setCacheName(dataEntry.getKey() + time);
					}
				}
			}
		}
	}

	/**
	 * 处理DataResponseEvent中指定的缓存表代码转名称信息<br>
	 *
	 * @param dataResponseEvent
	 *            response DTO
	 *
	 */
	public static void addCacheData(DataResponseEvent dataResponseEvent) {
		if(dataResponseEvent != null && dataResponseEvent.getCacheInfo() != null
				&& dataResponseEvent.getCacheInfo().size() > 0) {
			//有缓存转换信息，需要做代码转名称转换
			for(String widgetName : dataResponseEvent.getCacheInfo().keySet()){
				DataVO data = dataResponseEvent.getResComponentVO().getData().get(widgetName);
				switch (data.getType()) {
				case WebfrmAttr:
					break;
				case WebfrmForm:
					ResFormVO resFormVO = (ResFormVO) data;
					if(resFormVO != null && resFormVO.getData() != null && resFormVO.getData().size() > 0){
						//遍历需要转换的字段名
						for(String key : resFormVO.getData().keySet()){
							for(String volumnName : dataResponseEvent.getCacheInfo().get(widgetName).keySet()){
								if(key.equalsIgnoreCase(volumnName)){
									getCacheValue(volumnName, resFormVO.getData().get(key), dataResponseEvent.getCacheInfo().get(widgetName).get(volumnName));
								}
							}
						}
					}
					break;
				case WebfrmVouchGrid:
				case WebfrmGrid:
					ResGridVO resGridVO = (ResGridVO) data;
					if(resGridVO != null && resGridVO.getTrs() != null && resGridVO.getTrs().length >= 1 && resGridVO.getTrs()[0].getTds() != null && resGridVO.getTrs()[0].getTds().size() > 0){
						//遍历需要转换的字段名
						for(String key : resGridVO.getTrs()[0].getTds().keySet()){
							for(String volumnName : dataResponseEvent.getCacheInfo().get(widgetName).keySet()){
								if(key.equalsIgnoreCase(volumnName)){
									for(ResTDSVO tr : resGridVO.getTrs()){
										getCacheValue(volumnName, tr.getTds().get(key), dataResponseEvent.getCacheInfo().get(widgetName).get(volumnName));
									}
								}
							}
						}
					}
					break;
				}
			}
			//释放map防止内存泄漏
			dataResponseEvent.clearCacheInfo();
		}
	}
	private static void getCacheValue(String volumnName, Map<String, Object> nameValueMap, String cacheTableName){
		//取到要转换的原始值
		Object originalValue = nameValueMap.get(Constants.originalValueName);
		DictCacheService cui = ServerCacheManager.getDictCacheService();
		
		if(cacheTableName.indexOf(".") < 0){
			//只是表名，取默认的值字段
			//[0]:KeyColumnName;[1]ValueColumnName
			String[] colNames = cui.getKeyAndValueColName(cacheTableName);
			List<Map<String,Object>> cacheTableData= cui.getCacheData(cacheTableName);
			if(cacheTableData != null){
				for(Map<String,Object> rowData: cacheTableData){
					if(originalValue.equals(rowData.get(colNames[0]))){
						//定位到需要转换的数据所在行
						if(nameValueMap.containsKey(Constants.valueName)){
							nameValueMap.remove(Constants.valueName);
						}
						nameValueMap.put(Constants.valueName, rowData.get(colNames[1]));
					}
				}
			}
		}else{
			//表名.字段名形式，按指定字段取值
			String[] tableNameAndColumnNames = cacheTableName.split("\\.");
			List<CacheFilter> filter = new ArrayList<CacheFilter>();
			CacheFilter cacheFilter = new CacheFilter();
			cacheFilter.setFieldName(volumnName);
			cacheFilter.setFilterOperator(CacheFilter.FILTER_OPERATOR_EQUAL);
			cacheFilter.setFieldValue(originalValue);
			filter.add(cacheFilter);
			nameValueMap.put(Constants.valueName, cui.getCacheValueByColname(tableNameAndColumnNames[0], filter, tableNameAndColumnNames[1]));
		}

	}

}
