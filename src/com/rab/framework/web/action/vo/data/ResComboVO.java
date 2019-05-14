package com.rab.framework.web.action.vo.data;

import java.util.List;
import java.util.Map;

public class ResComboVO extends DataVO {

	private static final long serialVersionUID = 6490829983257026885L;
	private List<Map<String,Object>> data;
	private long total;
	
	
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	//	private ResOptionVO[] data;
	public List<Map<String, Object>> getData() {
		return data;
	}
	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	/**
	 * @return the data
	 */
//	public ResOptionVO[] getData() {
//		return data;
//	}

	/**
	 * @param data
	 *            the data to set
	 */
//	public void setData(ResOptionVO[] data) {
//		this.data = data;
//	}
	
	
}
