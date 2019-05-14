package com.rab.framework.web.action.vo;

import java.util.HashMap;
import java.util.Map;

import com.rab.framework.comm.dto.vo.BaseValueObject;
import com.rab.framework.web.action.vo.data.DataVO;
/**
 * 
 * <P>Title: ResComponentVO</P>
 * <P>Description: </P>
 * <P>程序说明：业务层返回给组件的数据值对象 </P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-13</P>
 *
 */
public class ResComponentVO extends BaseValueObject{
	private static final long serialVersionUID = -7267686683634022903L;
	private Map<String, DataVO> data = new HashMap<String, DataVO>();
	private int state;//0:success;1:fail
	private String message;
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
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}
