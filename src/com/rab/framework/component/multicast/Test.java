package com.rab.framework.component.multicast;

import java.util.Calendar;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.exception.BaseCheckedException;

public class Test {

	public void test(){
		String s = "≤‚ ‘–≈œ¢£°qwrqwr";
		try {
			MulticastMessageSender.singleton().dataSend(s.getBytes(), DemoMulticastFlatInfoServerAdapter.class.getName());
		} catch (BaseCheckedException e) {
			e.printStackTrace();
		}
	}
	
	public void test2(){
		try {
			MulticastMessageSender.singleton().dataSend(Calendar.getInstance(), DemoMulticastObjectInfoServerAdapter.class.getName());
		} catch (BaseCheckedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ApplicationContext.singleton();
		Test t = new Test();
		t.test();
		t.test2();
	}

}
