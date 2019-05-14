package com.rab.framework.comm.log;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

import com.rab.framework.comm.util.FileUtils;

/**
 * 
 * <P>Title: LogFactory</P>
 * <P>Description: ��־��¼����������</P>
 * <P>����˵����</P>
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
	 * ������־�����
	 * @param clazz �����־�ĳ����ඨ��
	 * 
	 * @return ��־�����
	 */
	public static LogWritter getLogger(Class<?> clazz) {
		return new LogWritter(LoggerFactory.getLogger(clazz));
	} 
	
	
	/**
	 * ������־�����
	 * @param �����־�ĳ���������
	 * 
	 * @return��־�����
	 */
	public static LogWritter getLogger(String name) {
		return new LogWritter(LoggerFactory.getLogger(name));
	} 
	
	/**
	 * <p>����������־�����</p>
	 *
	 * @return ������־�����
	 */
	public static PerformanceLog getPerfLogger() {
		return new PerformanceLog(LoggerFactory.getLogger("perflogger"));
	} 

}