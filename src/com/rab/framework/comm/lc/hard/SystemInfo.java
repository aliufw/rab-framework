package com.rab.framework.comm.lc.hard;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

public class SystemInfo {
	protected static final LogWritter logger = LogFactory.getLogger(SystemInfo.class);
	static{
		try
		{
			System.loadLibrary("SystemInfo");
		}
		catch(UnsatisfiedLinkError ule)
		{
			logger.error("���ؿ��ļ�����",ule);
		}		
	}
	
	/**
	 * <p>�õ�CPU���к�</p>
	 *
	 * @return
	 */
	public native String getCpuId();
	
	/**
	 * <p>�õ�����MAC��ַ</p>
	 *
	 * @return
	 */
	public native String getNetMac();
	
	/**
	 * <p>�õ�Ӳ�����к�</p>
	 *
	 * @return
	 */
	public native String getHdId();

}
