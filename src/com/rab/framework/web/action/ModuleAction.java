package com.rab.framework.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.FuncRightPrincipal;
import com.rab.framework.comm.security.FuncRightResource;
import com.rab.framework.comm.security.LogonEnvironment;
import com.rab.framework.comm.security.MenuNodeComparator;
import com.rab.framework.component.dictcache.CacheFilter;
import com.rab.framework.component.dictcache.ServerCacheManager;
import com.rab.framework.delegate.BizDelegate;
import com.rab.framework.web.action.base.BaseAction;
import com.rab.sys.security.login.event.ChangeModuleInfoRequestEvent;

/**
 * 
 * <P>Title: ModuleAction</P>
 * <P>Description: </P>
 * <P>程序说明：：Action类，完成取单位、帐套、模块信息，切换功能模块等功能。</P>
 * <P>主要控制数据的格式转换工作</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-9-16</P>
 *
 */

public class ModuleAction extends BaseAction {

	protected static final LogWritter logger = LogFactory
			.getLogger(ModuleAction.class);

	public BaseResponseEvent getCompInfo(BaseRequestEvent reqEvent)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String company : super.getSubject().getFuncPrincipals().keySet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			List<CacheFilter> filter = new ArrayList<CacheFilter>();
			CacheFilter cacheFilter = new CacheFilter();
			cacheFilter.setFieldName("COMP_CODE");
			cacheFilter.setFilterOperator(CacheFilter.FILTER_OPERATOR_EQUAL);
			cacheFilter.setFieldValue(company);
			filter.add(cacheFilter);
			
			List<Map<String, Object>> cacheList = ServerCacheManager.getDictCacheService().getCacheData("t_sys_company", filter);
			String compName = (String)cacheList.get(0).get("COMP_NAME");
			
			map.put("code", company);
			map.put("caption", compName);
			
			list.add(map);
		}
		
		DataResponseEvent res = new DataResponseEvent();
		res.addCombo("company", list);

		return res;
	}

	public BaseResponseEvent getSetInfo(BaseRequestEvent reqEvent)
			throws Exception {
		DataRequestEvent dataRequestEvent = (DataRequestEvent) reqEvent;
		String company =dataRequestEvent.getAttr("company") == null ? null : dataRequestEvent.getAttr("company").toString();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(super.getSubject().getFuncPrincipals().get(company) != null){
			for (String set : super.getSubject().getFuncPrincipals().get(company).keySet()) {
				Map<String, Object> map = new HashMap<String, Object>();				

				List<CacheFilter> filter = new ArrayList<CacheFilter>();
				CacheFilter cacheFilter = new CacheFilter();
				cacheFilter.setFieldName("COPY_CODE");
				cacheFilter.setFilterOperator(CacheFilter.FILTER_OPERATOR_EQUAL);
				cacheFilter.setFieldValue(set);
				filter.add(cacheFilter);
				
				List<Map<String, Object>> cacheList = ServerCacheManager.getDictCacheService().getCacheData("t_sys_copy", filter);
				String copyName = (String)cacheList.get(0).get("COPY_NAME");
				
				map.put("code", set);
				map.put("caption", copyName);
				list.add(map);
			}
		}
		
		DataResponseEvent res = new DataResponseEvent();
		res.addCombo("set", list);

		return res;
	}
	
	public BaseResponseEvent getModuleInfo(BaseRequestEvent reqEvent)
			throws Exception {
		DataRequestEvent dataRequestEvent = (DataRequestEvent) reqEvent;
		String company =dataRequestEvent.getAttr("company") == null ? null : dataRequestEvent.getAttr("company").toString();
		String set =dataRequestEvent.getAttr("set") == null ? null : dataRequestEvent.getAttr("set").toString();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String module : super.getSubject().getFuncPrincipals().get(company).get(set)
				.keySet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			List<CacheFilter> filter = new ArrayList<CacheFilter>();
			CacheFilter cacheFilter = new CacheFilter();
			cacheFilter.setFieldName("MOD_CODE");
			cacheFilter.setFilterOperator(CacheFilter.FILTER_OPERATOR_EQUAL);
			cacheFilter.setFieldValue(module);
			filter.add(cacheFilter);
			
			List<Map<String, Object>> cacheList = ServerCacheManager.getDictCacheService().getCacheData("t_sys_mod", filter);
			String modName = (String)cacheList.get(0).get("MOD_NAME");
			
			map.put("code", module);
			map.put("caption", modName);
			list.add(map);
		}
		DataResponseEvent res = new DataResponseEvent();
		res.addCombo("module", list);

		return res;
	}
	
	/**
	 * 根据前台页面选择的模块信息，获得该模块功能列表。<br>
	 * 并构造成树形列表数据返回给页面<br>
	 * 
	 * 
	 * @param reqEvent
	 *            request DTO
	 * @return BaseResponseEvent
	 * 			  response DTO
	 */
	public BaseResponseEvent changeModuleInfo(BaseRequestEvent reqEvent)
			throws Exception {
		DataRequestEvent dataRequestEvent = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		
		String company = dataRequestEvent.getAttr("company") == null ? null : dataRequestEvent.getAttr("company").toString();
		String set = dataRequestEvent.getAttr("set") == null ? null : dataRequestEvent.getAttr("set").toString();		
		String module = dataRequestEvent.getAttr("module") == null ? null : dataRequestEvent.getAttr("module").toString();
		String changeModuleFlag = dataRequestEvent.getAttr("changeModuleFlag") == null ? null : dataRequestEvent.getAttr("changeModuleFlag").toString();
		String loginDate = dataRequestEvent.getAttr("loginDate") == null ? null : dataRequestEvent.getAttr("loginDate").toString();
		
		if (!StringUtils.isBlank(company) && !StringUtils.isBlank(set) && !StringUtils.isBlank(module)) {
			//设置新的单位、账套、模块信息并调用BLH同步到后台domainSession
			LogonEnvironment logonEnvironment = super.getLogonEnvironment();

			logonEnvironment.setOrgId(company);
			logonEnvironment.setSOB(set);
			logonEnvironment.setModule(module);
			if(loginDate != null && loginDate.length() == 10){
				Calendar date = Calendar.getInstance();
				date.set(Integer.parseInt(loginDate.substring(0,4)), Integer.parseInt(loginDate.substring(5,7)), Integer.parseInt(loginDate.substring(8)));
				logonEnvironment.setAcctYear(loginDate.substring(0,4));
				logonEnvironment.setCurrDate(date);
			}else{
				Calendar now = Calendar.getInstance();
				logonEnvironment.setAcctYear(new Integer(now.get(Calendar.YEAR)).toString());
				logonEnvironment.setCurrDate(now);
			}
			
			
			ChangeModuleInfoRequestEvent req = new ChangeModuleInfoRequestEvent("LoginBLH", getVHSessionId());
			req.setMethod("updateLogonEnvironment");
			req.setLogonEnvironment(logonEnvironment);
			
			BaseResponseEvent response = BizDelegate.delegate(req);
			if(!response.isSuccess()){
				return response;
			}
			
			List<Object> menuList = new ArrayList<Object>();
			Map<String, FuncRightPrincipal> funcPrincipals = super
					.getSubject().getFuncPrincipals().get(company).get(set)
					.get(module);
			Iterator<FuncRightPrincipal> iter = funcPrincipals.values()
					.iterator();
			while (iter.hasNext()) {
				FuncRightPrincipal principal = iter.next();
					FuncRightResource mfrr = principal.getFuncRightRes();
					menuList.add(mfrr);
			}

			//排序
	    	Collections.sort(menuList, new MenuNodeComparator());
			
			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("code", "funcId");
			nameMap.put("caption", "permName");
			nameMap.put("qtip", "permName");
			nameMap.put("url", "funcUri");
			nameMap.put("pcode", "parentId");
			nameMap.put("leaf", "funcType");
			
			List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>> ();
			for(Object menu : menuList){
				nodes.add(BeanUtils.describe(menu));
			}
			res.addTree("tree", nodes, nameMap);
			
			if(changeModuleFlag != null){
				res.addAttr("changeModuleFlag", changeModuleFlag);
			}
		}

		return res;
	}


}
