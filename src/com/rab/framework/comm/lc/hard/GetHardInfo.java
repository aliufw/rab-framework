package com.rab.framework.comm.lc.hard;

/**
 * 
 * <P>Title: GetHardInfo</P>
 * <P>Description: </P>
 * <P>程序说明：获取设备硬件码的工具</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2011-1-12</P>
 *
 */
public class GetHardInfo {

	public void getHardInfo(){
		SystemInfo si = new SystemInfo();
		
		String cpuid = si.getCpuId();
		String mac   = si.getNetMac();
		String hdid  = si.getHdId();
		
		System.out.println("获取设备硬件标识码如下：");
		System.out.println("-----------------------------------------------");
		if(cpuid != null && cpuid.trim().length() > 0){
			System.out.println("CPU标识：" + cpuid);
		}
		else{
			System.out.println("CPU标识：无");
		}
		
		if(mac != null && mac.trim().length() > 0){
			System.out.println("Mac标识：" + mac);
		}
		else{
			System.out.println("Mac标识：无");
		}
		
		if(hdid != null && hdid.trim().length() > 0){
			System.out.println("HD标识：" + hdid);
		}
		else{
			System.out.println("HD标识：无");
		}
		
		System.out.println("-----------------------------------------------");
		
	}
	
	
	public static void main(String[] args) {
		GetHardInfo main = new GetHardInfo();
		main.getHardInfo();
	}

}
