package com.rab.framework.web.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.component.dictcache.CacheFilter;
import com.rab.framework.component.dictcache.DictCacheService;
import com.rab.framework.component.dictcache.ServerCacheManager;
import com.rab.framework.web.action.base.BaseAction;

/**
 * 
 * <P>Title: CacheAction</P>
 * <P>Description: </P>
 * <P>程序说明：Action类，完成通用的页面组件对缓存代码表调用，调用方式为Ajax。</P>
 * <P>主要控制数据流转以及数据的格式转换工作</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author ZhangBin</P>
 * <P>version 1.0</P>
 * <P>2010-8-9</P>
 *
 */

public class CacheAction extends BaseAction{

	protected static final LogWritter logger = LogFactory
			.getLogger(CacheAction.class);
	
	private static final List<String> exp = new ArrayList<String>(6);
	
	static {
		exp.add("cacheName");
		exp.add("dataName");
		exp.add("displayArray");
		exp.add("query");
		exp.add("start");
		exp.add("limit");
	}
	
	public BaseResponseEvent doAction(BaseRequestEvent reqEvent)
			throws Exception {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();

		String cacheName = (String) req.getAttr("cacheName");
		String dataName = (String) req.getAttr("dataName");

		if (StringUtils.isNotBlank(cacheName)
				&& StringUtils.isNotBlank(dataName)) {
			String displayArray = StringUtils.trimToEmpty((String) req
					.getAttr("displayArray"));
			String query = StringUtils.trimToEmpty((String) req
					.getAttr("query"));
			int start = Integer.parseInt(StringUtils.trimToEmpty((String) req
					.getAttr("start")));
			int limit = Integer.parseInt(StringUtils.trimToEmpty((String) req
					.getAttr("limit")));
			String[] fields = displayArray.split("\\|");
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

			
			DictCacheService cui = ServerCacheManager.getDictCacheService();
			
			List<CacheFilter> filter = null;
			String[] querys = query.split("(\\s+)|(\\s*$)");
			filter = getFilters(cacheName,fields, querys,req);
				
			
			// 这个逻辑需要优化
			list = cui.getCacheData(cacheName, filter);
			if (list != null) {
				if (limit == 0) {
					res.addCombo(dataName, list);
				} else {

					if (list.size() >= (start + limit)) {
						for (int i = start; i < start + limit; i++) {
							retList.add(list.get(i));
						}
					} else if (list.size() >= start
							&& list.size() < (start + limit)) {
						for (int i = start; i < list.size(); i++) {
							retList.add(list.get(i));
						}
					} else {
						retList = list;
					}
					res.addCombo(dataName, retList, (long)list.size());

				}

			}
		}
		return res;

	}
	
	
	private List<CacheFilter> getFilters(String cacheName,String[] fields,String[] querys,DataRequestEvent req) throws Exception{
		List<CacheFilter> filter = new ArrayList<CacheFilter>(fields.length);
		if(querys!=null && querys.length>0 && !"".equals(querys[0])){
			for (int i = 0; i < fields.length; i++) {
				CacheFilter f1 = new CacheFilter();
				f1.setFilterOperator(CacheFilter.FILTER_OPERATOR_LIKE);
				f1.setFieldName(fields[i]);
				f1.setFieldValue(getQuery(querys, i));
				filter.add(f1);
			}
		}
		createParamsFilter(filter,req);
		
		return filter;
	}
	/**
	 * 获取动态参数
	 * @param filter
	 * @param req
	 */
	private void createParamsFilter(List<CacheFilter> filter,DataRequestEvent req){
		
		Map<String,Object> map = req.getAttrs();
		
		for(Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();iter.hasNext();){
			Map.Entry<String, Object> entry = iter.next();
			if(exp.indexOf(entry.getKey())>-1){
				continue;
			}else{
				CacheFilter f = new CacheFilter();
				f.setFilterOperator(CacheFilter.FILTER_OPERATOR_EQUAL);
				f.setFieldName(entry.getKey());
				f.setFieldValue(entry.getValue());
				filter.add(f);
			}
		}
	}
	
	
	private String getQuery(String[] s, int idx) {
		String res = "";
		if (s.length >= (idx + 1)) {
			res = s[idx];
		}
		return res;
	}
	
}
