package com.rab.framework.comm.lc.hard;

public class HardInfoUtils {
	SystemInfo sinfo = null;

	public HardInfoUtils() {
		sinfo = new SystemInfo();
	}

	/**
	 * 得到Cpu序列号
	 */
	public String getCPUId() {
		return sinfo.getCpuId();
	}

	/**
	 * 得到Mac网络物理地址
	 */
	public String getNetMac() {
		return sinfo.getNetMac();
	}

	/**
	 * 得到硬盘序列号
	 */
	public String getHandID() {
		return sinfo.getHdId();
	}
}
