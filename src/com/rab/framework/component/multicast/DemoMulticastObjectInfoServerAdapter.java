package com.rab.framework.component.multicast;

public class DemoMulticastObjectInfoServerAdapter implements
		BaseMulticastObjectInfoServerAdapter {

	public void dataHander(Object obj) {
		System.out.println("Multicast ObjectInfo ����ʾ��========================== ");
		System.out.println("t0 = " + System.currentTimeMillis());
		System.out.println("���ݶ��� ���� class = " + obj.getClass().getName());
		System.out.println("���ݶ��� obj = " + obj);
		
		System.out.println("Multicast ObjectInfo ����ʾ��========================== ");

	}

}
