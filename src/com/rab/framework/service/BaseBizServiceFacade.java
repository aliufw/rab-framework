package com.rab.framework.service;

import java.util.Map;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.ExceptionInfo;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.threadlocal.ThreadLocalManager;

/**
 * 
 * <P>Title: BaseBizServiceFacade</P>
 * <P>Description: </P>
 * <P>����˵����ҵ�������װ�ӿڵ�ʵ�ֻ���</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-18</P>
 *
 */
public abstract class BaseBizServiceFacade implements BizServiceFacade{
	/**
	 * ��־��¼��
	 */
	private final static LogWritter logger = LogFactory.getLogger(BaseBizServiceFacade.class);
    
	/**
	 * <p>������־Ԥ����ͨ��ThreadLocal�������ô�����ٱ��</p>
	 *
	 * @param reqEvent  �������
	 */
	protected void preProcessForPerfLog(BaseRequestEvent reqEvent){
//    	ThreadLocal<String> threadLocal = ThreadLocalManager.getThreadLocal();
     	String txId = reqEvent.getTxId();
//    	threadLocal.set(txId);
    	
       	Map<Object,Object> map = ThreadLocalManager.getThreadLocalMap();
       	map.put("txId", txId);
       	
	}
	
    /**
     * ͨ���쳣����
     * 
     * @param respEvent
     * @param e
     * @return
     */
	protected BaseResponseEvent exceptionHandler(BaseCheckedException e){
		BaseResponseEvent respEvent = new BaseResponseEvent();
		respEvent.setSuccess(false);
		logger.error(e.getExceptionMsg(),e);
		
		ExceptionInfo ei = new ExceptionInfo();
		ei.setExceptionCode(e.getCode());
		ei.setExceptionMsg(e.getExceptionMsg());
		ei.setStacktraces(e.getStackTrace());
		
//		String txId = ThreadLocalManager.getThreadLocal().get();
		String txId = "" + ThreadLocalManager.getThreadLocalMap().get("txId");
    	if(txId != null){
    		ei.setTxid(txId);
    	}

		respEvent.setExceptionInfo(ei);
    		
    	return respEvent;
    }
	 
//	/**
//	 * <p>��鵱ǰ�����Ƿ�����Ԥ����ļ������</p>
//	 * <p> bootstrap.xml��Ԥ������һЩ������⣬�绺����¡���¼�����</p>
//	 *
//	 * @param transactionID
//	 * @return
//	 * @throws VHBaseCheckedException
//	 */
//	private boolean isTransactionNotChecked(String transactionID) throws VHBaseCheckedException{
//		if(transactionID == null || transactionID.trim().equals("")){
//			logger.error("00000501: û�ж�������ʵ�ҵ���������飡");
//			throw new VHBaseCheckedException("00000501");
//		}
//		
//		String transaction_not_checked = "" + ApplicationContext.singleton().getValueByKey("transaction-not-checked");
//		if(transaction_not_checked == null){
//			logger.error("00000502: �����ļ� bootstrap.xml ��û�ж���transaction-not-checked���ԣ����飡");
//			throw new VHBaseCheckedException("00000502");
//		}
//		
//		StringTokenizer st = new  StringTokenizer(transaction_not_checked,",");
//		
//		boolean flag = false;
//		while(st.hasMoreElements()){
//			String tid = "" + st.nextElement();
//			if(tid.trim().equals(transactionID)){
//				flag = true;
//				break;
//			}
//		}
//		
//		return flag;
//	}
	
}
