package com.rab.framework.component.multicast;

import java.io.UnsupportedEncodingException;

/**
 * 
 * <P>Title: DemoMulticastFlatInfoServerAdapter</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
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
		System.out.println("Multicast FlatInfo 数据示例========================== ");
		System.out.println("t0 = " + System.currentTimeMillis());
		System.out.println("数据长度：data.length = " + data.length);
		
		for(int i=0; i<data.length; i++){
			System.out.print(data[i] + ", ");
		}
		System.out.println();
		
		try {
			System.out.println("数据字符串：str = " + new String(data,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println("Multicast FlatInfo 数据示例========================== ");
	}

}
