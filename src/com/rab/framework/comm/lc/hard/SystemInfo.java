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
			logger.error("加载库文件出错！",ule);
		}		
	}
	
	/**
	 * <p>得到CPU序列号</p>
	 *
	 * @return
	 */
	public native String getCpuId();
	
	/**
	 * <p>得到网络MAC地址</p>
	 *
	 * @return
	 */
	public native String getNetMac();
	
	/**
	 * <p>得到硬盘序列号</p>
	 *
	 * @return
	 */
	public native String getHdId();

}
