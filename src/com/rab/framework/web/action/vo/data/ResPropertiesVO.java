package com.rab.framework.web.action.vo.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * <P>Title: ResPropertiesVO</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2011-9-29</P>
 *
 */
public class ResPropertiesVO extends DataVO {

	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 5437532352285470718L;
	
	private List<Map<String,String>> list = new ArrayList<Map<String,String>>();
	
	private int total;

	public List<Map<String,String>> getData() {
		return list;
	}

	public void setProperties(Properties props) {
		if(props == null){
			total = 0;
			return;
		}
		
		Iterator<Object> keyIter = props.keySet().iterator();
		while(keyIter.hasNext()){
			String key = "" + keyIter.next();
			String value = props.getProperty(key);
			
			Map<String, String> item = new HashMap<String, String>();
			item.put("key", key);
			item.put("value", value);
			
			this.list.add(item);
		}
		
		total = this.list.size();
	}

	public int getTotal() {
		return total;
	}

}
