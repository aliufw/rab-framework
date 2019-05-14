package com.rab.framework.component.dictcache;

import com.rab.framework.comm.appcontext.ApplicationContext;

public class Test {


	public static void main(String[] args) {
		ApplicationContext.singleton();
		ServerCacheManager.getDictCacheManager().load();
//		CacheUserInterface c = ServerCacheManager.getCacheUserInterface();
		
//		ArrayList filters = new ArrayList();
//	    CacheFilter filter = new CacheFilter();
//	    filter.setFieldName("BB");
//	    List iter = new ArrayList();
//	    iter.add("5");
//	    filter.setFieldValue(iter);
//	    filter.setFilterOperator(CacheFilter.FILTER_OPERATOR_IN);
//	    filters.add(filter);
//	    
//	    CacheFilter filter2 = new CacheFilter();
//	    filter2.setFieldName("AA");
//	    filter2.setFieldValue("aaa52");
//	    filters.add(filter2);
//	    
//	    List list = c.getCacheData("t_test01", filters);
//	    System.out.println(list);

	}

}
