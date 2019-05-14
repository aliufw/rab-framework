package com.rab.framework.comm.lc.hard;

/**
 * 
 * <P>Title: GetHardInfo</P>
 * <P>Description: </P>
 * <P>����˵������ȡ�豸Ӳ����Ĺ���</P>
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
		
		System.out.println("��ȡ�豸Ӳ����ʶ�����£�");
		System.out.println("-----------------------------------------------");
		if(cpuid != null && cpuid.trim().length() > 0){
			System.out.println("CPU��ʶ��" + cpuid);
		}
		else{
			System.out.println("CPU��ʶ����");
		}
		
		if(mac != null && mac.trim().length() > 0){
			System.out.println("Mac��ʶ��" + mac);
		}
		else{
			System.out.println("Mac��ʶ����");
		}
		
		if(hdid != null && hdid.trim().length() > 0){
			System.out.println("HD��ʶ��" + hdid);
		}
		else{
			System.out.println("HD��ʶ����");
		}
		
		System.out.println("-----------------------------------------------");
		
	}
	
	
	public static void main(String[] args) {
		GetHardInfo main = new GetHardInfo();
		main.getHardInfo();
	}

}
