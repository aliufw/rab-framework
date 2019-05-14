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
 * <P>程序说明：业务层服务封装接口的实现基类</P>
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
	 * 日志记录器
	 */
	private final static LogWritter logger = LogFactory.getLogger(BaseBizServiceFacade.class);
    
	/**
	 * <p>性能日志预处理，通过ThreadLocal对象设置处理跟踪标记</p>
	 *
	 * @param reqEvent  请求对象
	 */
	protected void preProcessForPerfLog(BaseRequestEvent reqEvent){
//    	ThreadLocal<String> threadLocal = ThreadLocalManager.getThreadLocal();
     	String txId = reqEvent.getTxId();
//    	threadLocal.set(txId);
    	
       	Map<Object,Object> map = ThreadLocalManager.getThreadLocalMap();
       	map.put("txId", txId);
       	
	}
	
    /**
     * 通用异常处理
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
//	 * <p>检查当前请求是否属于预定义的检查例外</p>
//	 * <p> bootstrap.xml中预定义了一些检查例外，如缓存更新、登录请求等</p>
//	 *
//	 * @param transactionID
//	 * @return
//	 * @throws VHBaseCheckedException
//	 */
//	private boolean isTransactionNotChecked(String transactionID) throws VHBaseCheckedException{
//		if(transactionID == null || transactionID.trim().equals("")){
//			logger.error("00000501: 没有定义待访问的业务层服务，请检查！");
//			throw new VHBaseCheckedException("00000501");
//		}
//		
//		String transaction_not_checked = "" + ApplicationContext.singleton().getValueByKey("transaction-not-checked");
//		if(transaction_not_checked == null){
//			logger.error("00000502: 配置文件 bootstrap.xml 中没有定义transaction-not-checked属性，请检查！");
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
