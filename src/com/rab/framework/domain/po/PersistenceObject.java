package com.rab.framework.domain.po;

import java.util.Calendar;
import java.util.Map;

public interface PersistenceObject{
	/**
	 * 
	 * <p>����޸�ʱ��</p>
	 *
	 * @return
	 */
	public Calendar getXgsj();
	
	/**
	 * 
	 * <p>���¼��ʱ��</p>
	 *
	 * @return
	 */
	public Calendar getLrsj();
	
	/**
	 * 
	 * <p>�����޸�ʱ��</p>
	 *
	 * @param xgsj
	 */
	public void setXgsj(Calendar xgsj);
	
	/**
	 * 
	 * <p>���¼��ʱ��</p>
	 *
	 * @param lrsj
	 */
	public void setLrsj(Calendar lrsj);
	
	/**
	 * 
	 * <p>��ñ��޸ĵ����Լ�</p>
	 *
	 * @return
	 */
	public Map<String, Object> getStatus();
}
