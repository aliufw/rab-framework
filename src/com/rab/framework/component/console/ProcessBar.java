package com.rab.framework.component.console;

import java.io.OutputStream;

/**
 * 
 * <P>Title: ProcessBar</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>基于telnet控制台的进度显示组件</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class ProcessBar extends Thread{
	/**
	 * 数据输出接口
	 */
	OutputStream out;
	
	/**
	 * 程序终止标记
	 */
	boolean flag = true;
	
	/**
	 * 构造器
	 * @param out
	 */
	public ProcessBar(OutputStream out){
		this.out = out;
	}
	
	
	public void run(){
		try {
			while(flag){
				out.write(".".getBytes());
				sleep(1000);
			}
		} 
		catch (Exception e) {
		}
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
