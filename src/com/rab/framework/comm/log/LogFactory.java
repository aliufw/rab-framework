package com.rab.framework.comm.log;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

import com.rab.framework.comm.util.FileUtils;

/**
 * 
 * <P>Title: LogFactory</P>
 * <P>Description: 日志记录器创建工厂</P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-5-21</P>
 *
 */
public class LogFactory {
	private static String confFileName = "frame-log4j.properties";
	
    static {
    	FileUtils fileUtils = new FileUtils(confFileName) ;
        PropertyConfigurator.configure(fileUtils.getFileURL());
    }
	
	/**
	 * 返回日志输出器
	 * @param clazz 输出日志的程序类定义
	 * 
	 * @return 日志输出器
	 */
	public static LogWritter getLogger(Class<?> clazz) {
		return new LogWritter(LoggerFactory.getLogger(clazz));
	} 
	
	
	/**
	 * 返回日志输出器
	 * @param 输出日志的程序类名称
	 * 
	 * @return日志输出器
	 */
	public static LogWritter getLogger(String name) {
		return new LogWritter(LoggerFactory.getLogger(name));
	} 
	
	/**
	 * <p>返回性能日志输出器</p>
	 *
	 * @return 性能日志输出器
	 */
	public static PerformanceLog getPerfLogger() {
		return new PerformanceLog(LoggerFactory.getLogger("perflogger"));
	} 

}