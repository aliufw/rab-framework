package com.rab.framework.component.console;

import java.io.OutputStream;

/**
 * 
 * <P>Title: ProcessBar</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>����telnet����̨�Ľ�����ʾ���</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class ProcessBar extends Thread{
	/**
	 * ��������ӿ�
	 */
	OutputStream out;
	
	/**
	 * ������ֹ���
	 */
	boolean flag = true;
	
	/**
	 * ������
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
