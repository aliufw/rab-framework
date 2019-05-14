package com.rab.framework.comm.log;

import org.slf4j.Logger;

import com.rab.framework.comm.threadlocal.ThreadLocalManager;

/**
 * 
 * <P>Title: LogWritter</P>
 * <P>Description: 日志记录器</P>
 * 
 * <P>程序说明：</P>
 * <p>日志级别分为三级：debug、info和error</p>
 * <p>debug: 输出调试信息</p>
 * <p>info: 输出关键运行控制监控信息</p>
 * <p>error: 输出运行异常信息</p>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-5-20</P>
 *
 */
public class LogWritter {
	

	/**
	 * 记录器
	 */
    private Logger logger;

    /**
     * 构造器
     * 
     * @param logger
     */
    public LogWritter(Logger logger) {
        this.logger = logger;
    }
       
    /**
     * 记录debug级别日志
     * 
     * @param message
     */
    public void debug(String message) {
        if (logger.isDebugEnabled()) {
        	message = this.setTxIdInfo(message);
        	
            logger.debug(message);
        }
    }

    /**
     * 记录debug级别日志
     * 
     * @param message
     * @param ex
     */
    public void debug(String message, Throwable ex) {
        if (logger.isDebugEnabled()) {
        	message = this.setTxIdInfo(message);

            logger.debug(message, ex);
        }
    }

    /**
     * 记录INFO级别日志
     * 
     * @param message
     */
    public void info(String message) {
        if (logger.isInfoEnabled()) {
        	message = this.setTxIdInfo(message);

            logger.info(message);
        }
    }

    /**
     * 记录INFO级别日志
     * 
     * @param message
     * @param ex
     */
    public void info(String message, Throwable ex) {
        if (logger.isInfoEnabled()) {
        	message = this.setTxIdInfo(message);

            logger.info(message, ex);
        }
    }

    /**
     * 记录ERROR级别日志
     * 
     * @param message
     */
    public void error(String message) {
        if (logger.isErrorEnabled()) {
        	message = this.setTxIdInfo(message);

            logger.error(message);
        }
    }

    /**
     * 记录ERROR级别日志
     * 
     * @param message
     * @param ex
     */
    public void error(String message, Throwable ex) {
        if (logger.isErrorEnabled()) {
        	message = this.setTxIdInfo(message);

            logger.error(message, ex);
        }
    }

    private String setTxIdInfo(String message){
//    	String txId = ThreadLocalManager.getThreadLocal().get();
    	String txId = "" + ThreadLocalManager.getThreadLocalMap().get("txId");
//    	System.out.println("###########  txId = " + txId);
//    	System.out.println("###########  (txId==null) = " + (txId==null));
//    	System.out.println("###########  \"null\".equals(txId) = " + "null".equals(txId));
    	if(!txId.equalsIgnoreCase(null)){
    		message = txId + ": " + message;
    	}
    	
    	return message;
    }
}