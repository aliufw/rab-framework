package com.rab.framework.comm.log;

import org.slf4j.Logger;

import com.rab.framework.comm.threadlocal.ThreadLocalManager;

/**
 * 
 * <P>Title: PerformanceLog</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-30</P>
 *
 */
public class PerformanceLog {
	
//	private static String confFileName = "vhlog4j.properties";
//	
//    static {
//    	FileUtils fileUtils = new FileUtils(confFileName) ;
//        PropertyConfigurator.configure(fileUtils.getFileURL());
//    }

	/**
	 * 日志记录器
	 */
    private Logger perfLogger;

    /**
     * 构造器
     * 
     * @param logger
     */
    public PerformanceLog(Logger perfLogger) {
        this.perfLogger = perfLogger;
    }

    public void perflog(String curDateTime,
    					String usercode,
    					String transactionID,
    					String methodName,
    					long t){
        
    	StringBuffer sb = new StringBuffer();
    	//记录时间
    	sb.append(curDateTime);
    	sb.append(",");
    	
    	//用户编号
    	sb.append(usercode);
    	sb.append(",");

    	//交易编号
       	sb.append(transactionID);
    	sb.append(",");

    	//交易方法
       	sb.append(methodName);
    	sb.append(",");

    	//本次交易执行时间（毫秒）
       	sb.append("" + t);
    	sb.append(",");
    	
    	//日志跟踪编号
//    	String txId = ThreadLocalManager.getThreadLocal().get();
    	String txId = "" + ThreadLocalManager.getThreadLocalMap().get("txId");
    	if(txId == null)
    		txId = "txId-";
       	sb.append("" + txId);
 
       	perfLogger.error(sb.toString());
    }
 
}
