package com.rab.framework.web.action.util.GridTableHeadUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 表头CELL对象
 * @version 1.0, 12/14/2011
 * @author LISHIX
 * 
 */
public class GridTableHeadCell {
	private Map<String, String> cellMap = null;

	/**
	 * 构造方法
	 */
	public GridTableHeadCell() {
		super();
		this.cellMap = new HashMap<String, String>();
	}

	/**
	 * 构造方法
	 * @param cellMap cell配置参数
	 */
	public GridTableHeadCell(Map<String, String> cellMap) {
		super();
		this.cellMap = cellMap;
	}

	/**
	 * 取得cell配置参数
	 * @return
	 */
	public Map<String, String> getCellMap() {
		return cellMap;
	}

	/**
	 * 设置cell配置参数
	 * @param cellMap
	 */
	public void setCellMap(Map<String, String> cellMap) {
		this.cellMap = cellMap;
	}

	/**
	 * 取得cell name参数值
	 * @return
	 */
	public String getName() {
		return cellMap.get("name");
	}

	/**
	 * 设置cell name参数值
	 * @param name
	 */
	public void setName(String name) {
		this.cellMap.put("name", name);
	}

	public String getCaption() {
		return cellMap.get("caption");
	}

	public void setCaption(String caption) {
		this.cellMap.put("caption", caption);
	}

	public String getColspan() {
		if(cellMap.get("colspan")==null||cellMap.get("colspan").toString().equals("")){
			return "1";
		}
		return cellMap.get("colspan");
	}

	public void setColspan(int colspan) {
		this.cellMap.put("colspan", colspan+"");
	}

	public String getRowspan() {
		if(cellMap.get("rowspan")==null||cellMap.get("rowspan").toString().equals("")){
			return "1";
		}
		return cellMap.get("rowspan");
	}

	public void setRowspan(int rowspan) {
		this.cellMap.put("rowspan", rowspan+"");
	}
	
	public String getCellAlign() {
		return cellMap.get("cellAlign");
	}

	public void setCellAlign(String cellAlign) {
		this.cellMap.put("cellAlign", cellAlign);
	}

	public String getWidthX() {
		return cellMap.get("widthX");
	}

	public void setWidthX(String widthX) {
		this.cellMap.put("widthX", widthX);
	}

	public String getFormat() {
		return cellMap.get("format");
	}

	public void setFormat(String format) {
		this.cellMap.put("format", format);
	}

	public String getFormatType() {
		return cellMap.get("formatType");
	}

	public void setFormatType(String formatType) {
		this.cellMap.put("formatType", formatType);
	}
	
	/**
	 * 取得cell的HBOS内容
	 */
	public String getHtml(){
		StringBuilder html=new StringBuilder();
		if(this.getCellMap()!=null){
			html.append("<div ");
			Map<String,String> cellMap=this.getCellMap();
			Iterator<String> it=cellMap.keySet().iterator();
			while(it.hasNext()){
				String key=it.next();
				html.append(key+"='"+cellMap.get(key)+"' ");
			}
			html.append(">");
			html.append("</div>");
		}
		return html.toString();
	}
}
