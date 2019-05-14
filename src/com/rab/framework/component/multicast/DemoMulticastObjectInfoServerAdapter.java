package com.rab.framework.component.multicast;

public class DemoMulticastObjectInfoServerAdapter implements
		BaseMulticastObjectInfoServerAdapter {

	public void dataHander(Object obj) {
		System.out.println("Multicast ObjectInfo 数据示例========================== ");
		System.out.println("t0 = " + System.currentTimeMillis());
		System.out.println("数据对象 类型 class = " + obj.getClass().getName());
		System.out.println("数据对象 obj = " + obj);
		
		System.out.println("Multicast ObjectInfo 数据示例========================== ");

	}

}
