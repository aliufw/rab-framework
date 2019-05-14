package com.rab.framework.domain.po;

import java.util.Calendar;
import java.util.Map;

public interface PersistenceObject{
	/**
	 * 
	 * <p>获得修改时间</p>
	 *
	 * @return
	 */
	public Calendar getXgsj();
	
	/**
	 * 
	 * <p>获得录入时间</p>
	 *
	 * @return
	 */
	public Calendar getLrsj();
	
	/**
	 * 
	 * <p>设置修改时间</p>
	 *
	 * @param xgsj
	 */
	public void setXgsj(Calendar xgsj);
	
	/**
	 * 
	 * <p>获得录入时间</p>
	 *
	 * @param lrsj
	 */
	public void setLrsj(Calendar lrsj);
	
	/**
	 * 
	 * <p>获得被修改的属性集</p>
	 *
	 * @return
	 */
	public Map<String, Object> getStatus();
}
