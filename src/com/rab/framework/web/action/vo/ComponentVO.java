package com.rab.framework.web.action.vo;

import java.util.Map;

import com.rab.framework.comm.dto.vo.BaseValueObject;
import com.rab.framework.web.action.vo.data.DataVO;
/**
 * 
 * <P>Title: ComponentVO</P>
 * <P>Description: </P>
 * <P>程序说明：组件数据值对象 </P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-12</P>
 *
 */
public class ComponentVO extends BaseValueObject{
	private static final long serialVersionUID = -5316754945627209731L;
	private String tid;
	private String page;
	private String action;
	private Map<String, DataVO> data;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * @return the data
	 */
	public Map<String, DataVO> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Map<String, DataVO> data) {
		this.data = data;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

}
