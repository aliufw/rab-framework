package com.rab.framework.comm.log;

import org.slf4j.Logger;

import com.rab.framework.comm.threadlocal.ThreadLocalManager;

/**
 * 
 * <P>Title: PerformanceLog</P>
 * <P>Description: </P>
 * <P>����˵����</P>
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
	 * ��־��¼��
	 */
    private Logger perfLogger;

    /**
     * ������
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
    	//��¼ʱ��
    	sb.append(curDateTime);
    	sb.append(",");
    	
    	//�û����
    	sb.append(usercode);
    	sb.append(",");

    	//���ױ��
       	sb.append(transactionID);
    	sb.append(",");

    	//���׷���
       	sb.append(methodName);
    	sb.append(",");

    	//���ν���ִ��ʱ�䣨���룩
       	sb.append("" + t);
    	sb.append(",");
    	
    	//��־���ٱ��
//    	String txId = ThreadLocalManager.getThreadLocal().get();
    	String txId = "" + ThreadLocalManager.getThreadLocalMap().get("txId");
    	if(txId == null)
    		txId = "txId-";
       	sb.append("" + txId);
 
       	perfLogger.error(sb.toString());
    }
 
}
