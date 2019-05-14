package com.rab.framework.web.action.util.GridTableHeadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ��ͷ�ж���
 * @version 1.0, 12/14/2011
 * @author LISHIX
 *
 */
public class GridTableHeadRow {
	
	/**
	 * ��ͷ��
	 */
	private List<GridTableHeadCell> cells=new ArrayList<GridTableHeadCell>();
	
	/**
	 * ȡ���ж���
	 * @return
	 */
	public List<GridTableHeadCell> getCells() {
		return cells;
	}

	/**
	 * �����ж���
	 * @param cells
	 */
	public void setRow(List<GridTableHeadCell> cells) {
		this.cells = cells;
	}

	/**
	 * ����CELL
	 * @param cell
	 */
	public void addCell(GridTableHeadCell cell){
		cells.add(cell);
	}
	
	/**
	 * ȡ���ж���HBOS����
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
