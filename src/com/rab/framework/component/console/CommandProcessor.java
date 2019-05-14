package com.rab.framework.component.console;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 
 * <P>Title: CommandProcessor</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>基于Telnet技术的控制台处理程序基类</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public abstract class CommandProcessor {
	/**
	 * 信息输出接口,一般链接到telnet控制台界面
	 */
	protected OutputStream socketOut;
	
	/**
	 * 命令处理程序
	 * 
	 * @param cmdLine 命令
	 */
	public abstract void cmdProcess(String cmdLine);
	
	
	public void setSocketOut(OutputStream socketOut) {
		this.socketOut = socketOut;
	}

	/**
	 * 字符串格式化
	 * 
	 * @param msg
	 * @param length
	 * @return
	 */
	protected String formatString(String msg, int length){
		if(msg == null){
			msg = "null";
		}
		 
		if(len(msg) > length){
			msg = cutString(msg, length-1);
			msg += "~";
		}
		
		String ret = msg;
		for(int i=0; i<length-len(msg); i++){
			ret += " ";
		}
		
		return ret;
	}

	protected int len(String s){
		int len = -1;
		try {
			len = s.getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return len;
	}
	
	protected String cutString(String msg, int len){
		String ret = null;
		
		if(msg == null){
			return null;
		}
		
		try {
			byte[] data = msg.getBytes("GBK");

			if(data.length <= len){
				return msg;
			}
			
			byte[] tmp = new byte[len];
			
			int pointer = 0;
			while(pointer < len){
				if(data[pointer] >= 0){
					tmp[pointer] = data[pointer];
					pointer ++;
				}
				else{
					if(pointer < len-1){
						tmp[pointer] = data[pointer];
						tmp[pointer+1] = data[pointer+1];
						pointer += 2;
					}
					else{
						break;
					}
				}
			}
			
			ret = new String(tmp, 0, pointer);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return ret;
	}	
	
	protected String repeat(String s, int count){
		String ret = "";
		for(int i=0; i<count; i++){
			ret += s;
		}
		
		return ret;
	}
}
