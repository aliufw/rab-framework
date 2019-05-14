package com.rab.framework.comm.log;

import org.slf4j.Logger;

import com.rab.framework.comm.threadlocal.ThreadLocalManager;

/**
 * 
 * <P>Title: LogWritter</P>
 * <P>Description: ��־��¼��</P>
 * 
 * <P>����˵����</P>
 * <p>��־�����Ϊ������debug��info��error</p>
 * <p>debug: ���������Ϣ</p>
 * <p>info: ����ؼ����п��Ƽ����Ϣ</p>
 * <p>error: ��������쳣��Ϣ</p>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-5-20</P>
 *
 */
public class LogWritter {
	

	/**
	 * ��¼��
	 */
    private Logger logger;

    /**
     * ������
     * 
     * @param logger
     */
    public LogWritter(Logger logger) {
        this.logger = logger;
    }
       
    /**
     * ��¼debug������־
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
     * ��¼debug������־
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
     * ��¼INFO������־
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
     * ��¼INFO������־
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
     * ��¼ERROR������־
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
     * ��¼ERROR������־
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