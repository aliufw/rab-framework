package com.rab.framework.component.multicast;

import java.io.UnsupportedEncodingException;

/**
 * 
 * <P>Title: DemoMulticastFlatInfoServerAdapter</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-4</P>
 *
 */
public class DemoMulticastFlatInfoServerAdapter implements
		BaseMulticastFlatInfoServerAdapter {

	public void dataHander(byte[] data) {
		System.out.println("Multicast FlatInfo ����ʾ��========================== ");
		System.out.println("t0 = " + System.currentTimeMillis());
		System.out.println("���ݳ��ȣ�data.length = " + data.length);
		
		for(int i=0; i<data.length; i++){
			System.out.print(data[i] + ", ");
		}
		System.out.println();
		
		try {
			System.out.println("�����ַ�����str = " + new String(data,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println("Multicast FlatInfo ����ʾ��========================== ");
	}

}
