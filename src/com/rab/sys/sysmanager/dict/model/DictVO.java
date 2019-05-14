package com.rab.sys.sysmanager.dict.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DictVO {
	
	private String tableName;
	
	private String description;
	
	private List<Map<String,String>> fields = new ArrayList<Map<String,String>> ();

	public String getTableName() {
		return tableName;
	}
 
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public List<Map<String, String>> getFields() {
		return fields;
	}

	public void setFields(List<Map<String, String>> fields) {
		this.fields = fields;
	}
	
	

	public String toString(){
		
		StringBuffer str = new StringBuffer();
		str.append("tableName:"+this.getTableName())
		.append("\ndict:"+this.getDescription()+"\nfields:\n");
		for(int i=0;i<this.getFields().size();i++){
			Map map = (Map)this.getFields().get(i);
			for(Iterator iter=map.entrySet().iterator();iter.hasNext();){
				Map.Entry entry = (Map.Entry)iter.next();
				str.append("[").append(entry.getKey()+"]:").append(entry.getValue()+"\n");
			}
		}
		return str.toString();
	}

}
