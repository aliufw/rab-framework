package com.rab.framework.component.dictcache.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.component.dictcache.manager.event.TableCacheManagerRequestEvent;
import com.rab.framework.component.dictcache.manager.event.TableCacheManagerResponseEvent;
import com.rab.framework.delegate.BizDelegate;

public class Test_DictCache {

	public void test01(){
		TableCacheManagerRequestEvent req = new TableCacheManagerRequestEvent("DictCacheManagerBLH");
		req.setTableName("t_dbm_tmp");
		req.setMethod("delete");
		
		List<Map<String,String>> datarows = new ArrayList<Map<String,String>>();
		
		for(int i=0; i<3; i++){
			Map<String,String> datarow = new HashMap<String,String>();
			datarow.put("id", "id00" + i);
			datarow.put("name", "name00" + i);
			datarow.put("shortname", "shortname00" + i);
			datarow.put("address", "addressQ00" + i);
			datarow.put("tel", "tel00" + i);
			datarows.add(datarow);
		}
		
		req.setDatarows(datarows);
		
		try {
			BaseResponseEvent resp =  BizDelegate.delegate(req);
			if(resp.isSuccess()){
				TableCacheManagerResponseEvent res = (TableCacheManagerResponseEvent)resp;
				System.out.println("·µ»Ø×´Ì¬£ºresp.isSuccess() = " + res.isSuccess());
			}
			else{
				System.out.println(resp.isSuccess());
				System.out.println(resp.getExceptionInfo());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void test02(){
		TableCacheManagerRequestEvent req = new TableCacheManagerRequestEvent("DictCacheManagerBLH");
		req.setTableName("t_dbm_tmp2");
		req.setMethod("insert");
		
		List<Map<String,String>> datarows = new ArrayList<Map<String,String>>();
		
		for(int i=1; i<5; i++){
			Map<String,String> datarow = new HashMap<String,String>();
			datarow.put("id", "id00" + i);
			datarow.put("name", "name00" + i);
			datarow.put("shengri", "1980-02-0" + i);
			datarow.put("niangling", "2" + i);
			datarow.put("shouru", "123.2" + i);
			datarows.add(datarow);
		}
		
		req.setDatarows(datarows);
		
		try {
			BaseResponseEvent resp =  BizDelegate.delegate(req);
			if(resp.isSuccess()){
				TableCacheManagerResponseEvent res = (TableCacheManagerResponseEvent)resp;
				System.out.println("·µ»Ø×´Ì¬£ºresp.isSuccess() = " + res.isSuccess());
			}
			else{
				System.out.println(resp.isSuccess());
				System.out.println(resp.getExceptionInfo());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		Test_DictCache t = new Test_DictCache();
		t.test02();
	}

}
