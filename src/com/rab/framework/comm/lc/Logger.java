package com.rab.framework.comm.lc;

/**
 * 
 * <P>Title: Logger</P>
 * <P>Description: </P>
 * <P>³ÌÐòËµÃ÷£º</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-11-3</P>
 *
 */
public class Logger {
	public static void log(String msg){
		System.out.println(msg);
	}
	public static void log(String msg, Throwable e){
		System.out.println(msg);
		e.printStackTrace();
	}
}
