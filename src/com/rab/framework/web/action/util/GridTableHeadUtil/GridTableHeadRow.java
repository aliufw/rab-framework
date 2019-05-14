package com.rab.framework.web.action.util.GridTableHeadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 表头行对象
 * @version 1.0, 12/14/2011
 * @author LISHIX
 *
 */
public class GridTableHeadRow {
	
	/**
	 * 表头行
	 */
	private List<GridTableHeadCell> cells=new ArrayList<GridTableHeadCell>();
	
	/**
	 * 取得行对象
	 * @return
	 */
	public List<GridTableHeadCell> getCells() {
		return cells;
	}

	/**
	 * 设置行对象
	 * @param cells
	 */
	public void setRow(List<GridTableHeadCell> cells) {
		this.cells = cells;
	}

	/**
	 * 增加CELL
	 * @param cell
	 */
	public void addCell(GridTableHeadCell cell){
		cells.add(cell);
	}
	
	/**
	 * 取得行对象HBOS数据
	 * @return
	 */
	public String getHtml(){
		StringBuilder html=new StringBuilder();
		html.append("<div>");
		for(GridTableHeadCell cell : this.getCells()){
			html.append(cell.getHtml());
		}
		html.append("</div>");
		return html.toString();
	}
}
