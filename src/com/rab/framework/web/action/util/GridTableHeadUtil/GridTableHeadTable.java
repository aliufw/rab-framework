package com.rab.framework.web.action.util.GridTableHeadUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 表头对象
 * @version 1.0, 12/14/2011
 * @author LISHIX
 * 
 * update by LISHIX 12/26/2011 更新列头生成方式
 */
public class GridTableHeadTable {
	/**
	 * 表头对象top参数
	 */
	private Map<String, String> topMap = null;
	
	private Map<Integer, GridTableHeadCell> columnFormatMap = null;
	/**
	 * 表头行对象数组
	 */
	private List<GridTableHeadRow> rowList = new ArrayList<GridTableHeadRow>();
	
	/**
	 * 无参构造方法
	 */
	public GridTableHeadTable() {
		super();
		this.topMap = new HashMap<String, String>();
		this.columnFormatMap = new TreeMap<Integer, GridTableHeadCell>();
	}

	/**
	 * 构造方法
	 * @param topMap 表头对象顶层参数
	 */
	public GridTableHeadTable(Map<String, String> topMap) {
		super();
		this.topMap = topMap;
	}

	/**
	 * 取得表头对象顶层参数
	 * @return
	 */
	public Map<String, String> getMap() {
		return topMap;
	}

	/**
	 * 设置表头对象顶层参数
	 * @param topMap
	 */
	public void setMap(Map<String, String> topMap) {
		this.topMap = topMap;
	}
	
	/**
	 * 取得name属性
	 * @return
	 */
	public String getName() {
		return this.topMap.get("name");
	}

	/**
	 * 设置name属性
	 * @param name
	 */
	public void setName(String name) {
		this.topMap.put("name", name);
	}

	/**
	 * 是否分页
	 * @return
	 */
	public boolean isFenye() {
		String fenye=this.topMap.get("fenye");
		if(fenye==null){
			return false;
		}
		return Boolean.parseBoolean(fenye);
	}

	/**
	 * 设置是否分页
	 * @param fenye
	 */
	public void setFenye(boolean fenye) {
		this.topMap.put("fenye", fenye+"");
	}

	/**
	 * 增加一行表头
	 * @param row 行对象
	 */
	public void addRow(GridTableHeadRow row){
		this.rowList.add(row);
	}
	
	/**
	 * 取得指定列格式
	 * @param key 指定列KEY
	 * @return 列格式对象
	 */
	public GridTableHeadCell getColumnFormat(int key){
		return this.columnFormatMap.get(key);
	}
	
	/**
	 * 增加一列格式
	 * @param cell cell对象
	 */
	public void setColumnFormat(int key,GridTableHeadCell cell){
		this.columnFormatMap.put(key,cell);
	}
	
	/**
	 * 取得所有列格式
	 * @return
	 */
	public Map<Integer,GridTableHeadCell> getColumnFormatMap(){
		return this.columnFormatMap;
	}
	
	/**
	 * 设置所有列格式
	 * @param map
	 */
	public void setColumnFormatMap(TreeMap<Integer,GridTableHeadCell> map){
		this.columnFormatMap = map;
	}
	/**
	 * 取得表头对象顶层参数
	 * @return 表头HBOS头配置
	 */
	private String getTopHtml(){
		StringBuilder html=new StringBuilder();
		if(this.getMap()!=null){
			html.append("<div webfrm='WebfrmGrid' ");
			Map<String,String> map=this.getMap();
			Iterator<String> it=map.keySet().iterator();
			while(it.hasNext()){
				String key=it.next();
				html.append(key+"='"+map.get(key)+"' ");
			}
			html.append(">");
		}
		return html.toString();
	}
	
	/**
	 * 取得HBOS内容
	 * @return HBOS内容
	 */
	public String getHtml(){
		StringBuilder html=new StringBuilder();
		html.append(this.getTopHtml());
		if(this.rowList!=null && !this.rowList.isEmpty()){
			html.append("<div header='true'>");
			for(int i=0;i<this.rowList.size();i++){
				html.append(this.rowList.get(i).getHtml());
			}
			html.append("</div>");
		}

		Iterator<Integer> it=this.getColumnFormatMap().keySet().iterator();
		while(it.hasNext()){
			Integer key=it.next();
			GridTableHeadCell cell=this.getColumnFormatMap().get(key);
			html.append(cell.getHtml());
		}
		html.append("</div>");
		return html.toString();
	}
}
