package com.rab.framework.web.action.util.GridTableHeadUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ��ͷ����
 * @version 1.0, 12/14/2011
 * @author LISHIX
 * 
 * update by LISHIX 12/26/2011 ������ͷ���ɷ�ʽ
 */
public class GridTableHeadTable {
	/**
	 * ��ͷ����top����
	 */
	private Map<String, String> topMap = null;
	
	private Map<Integer, GridTableHeadCell> columnFormatMap = null;
	/**
	 * ��ͷ�ж�������
	 */
	private List<GridTableHeadRow> rowList = new ArrayList<GridTableHeadRow>();
	
	/**
	 * �޲ι��췽��
	 */
	public GridTableHeadTable() {
		super();
		this.topMap = new HashMap<String, String>();
		this.columnFormatMap = new TreeMap<Integer, GridTableHeadCell>();
	}

	/**
	 * ���췽��
	 * @param topMap ��ͷ���󶥲����
	 */
	public GridTableHeadTable(Map<String, String> topMap) {
		super();
		this.topMap = topMap;
	}

	/**
	 * ȡ�ñ�ͷ���󶥲����
	 * @return
	 */
	public Map<String, String> getMap() {
		return topMap;
	}

	/**
	 * ���ñ�ͷ���󶥲����
	 * @param topMap
	 */
	public void setMap(Map<String, String> topMap) {
		this.topMap = topMap;
	}
	
	/**
	 * ȡ��name����
	 * @return
	 */
	public String getName() {
		return this.topMap.get("name");
	}

	/**
	 * ����name����
	 * @param name
	 */
	public void setName(String name) {
		this.topMap.put("name", name);
	}

	/**
	 * �Ƿ��ҳ
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
	 * �����Ƿ��ҳ
	 * @param fenye
	 */
	public void setFenye(boolean fenye) {
		this.topMap.put("fenye", fenye+"");
	}

	/**
	 * ����һ�б�ͷ
	 * @param row �ж���
	 */
	public void addRow(GridTableHeadRow row){
		this.rowList.add(row);
	}
	
	/**
	 * ȡ��ָ���и�ʽ
	 * @param key ָ����KEY
	 * @return �и�ʽ����
	 */
	public GridTableHeadCell getColumnFormat(int key){
		return this.columnFormatMap.get(key);
	}
	
	/**
	 * ����һ�и�ʽ
	 * @param cell cell����
	 */
	public void setColumnFormat(int key,GridTableHeadCell cell){
		this.columnFormatMap.put(key,cell);
	}
	
	/**
	 * ȡ�������и�ʽ
	 * @return
	 */
	public Map<Integer,GridTableHeadCell> getColumnFormatMap(){
		return this.columnFormatMap;
	}
	
	/**
	 * ���������и�ʽ
	 * @param map
	 */
	public void setColumnFormatMap(TreeMap<Integer,GridTableHeadCell> map){
		this.columnFormatMap = map;
	}
	/**
	 * ȡ�ñ�ͷ���󶥲����
	 * @return ��ͷHBOSͷ����
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
	 * ȡ��HBOS����
	 * @return HBOS����
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
