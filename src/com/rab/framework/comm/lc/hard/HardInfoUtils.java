package com.rab.framework.comm.lc.hard;

public class HardInfoUtils {
	SystemInfo sinfo = null;

	public HardInfoUtils() {
		sinfo = new SystemInfo();
	}

	/**
	 * �õ�Cpu���к�
	 */
	public String getCPUId() {
		return sinfo.getCpuId();
	}

	/**
	 * �õ�Mac���������ַ
	 */
	public String getNetMac() {
		return sinfo.getNetMac();
	}

	/**
	 * �õ�Ӳ�����к�
	 */
	public String getHandID() {
		return sinfo.getHdId();
	}
}
