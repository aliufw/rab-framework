package com.rab.framework.delegate;

import org.apache.commons.lang.RandomStringUtils;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.ExceptionInfo;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.service.BizServiceFacade;
import com.rab.framework.service.ejb.EJBBizServiceFactoryImpl;
import com.rab.framework.service.local.LocalBizServiceFactoryImpl;

/**
 * 
 * <P>Title: BizDelegate</P>
 * <P>Description: </P>
 * <P>����˵�������ʴ���</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class BizDelegate {

	private final static LogWritter logger = LogFactory.getLogger(BizDelegate.class);

	public static BaseResponseEvent delegate(BaseRequestEvent req) throws Exception {
		BaseResponseEvent resp = null;
		//����txid����ַ���
		String txId = "txid-" + RandomStringUtils.randomNumeric(15);
		req.setTxId(txId);
		
		try {
			BizServiceFacade serviceFacade = null;
			String ejb_model = (String)ApplicationContext.singleton().getValueByKey("ejb-model");
			if (ejb_model.equalsIgnoreCase("true")) {
				logger.debug("������EJBģʽ����,������EJB Facade!");
				serviceFacade = EJBBizServiceFactoryImpl.singleton().createBizServiceFacade();
			} else {
				logger.debug("�����˱��ز���ֱ�ӷ���Local����ӿ�!");
				serviceFacade = LocalBizServiceFactoryImpl.singleton().createBizServiceFacade();
			}		
			
			resp = (BaseResponseEvent) serviceFacade.invoke(req);

		} catch (Exception e) {
    		logger.error("00019999: δ֪�쳣",e);
    		BaseCheckedException e2 = new BaseCheckedException("00019999",e); 
    		resp = exceptionHandler(txId, e2);
		}

		return resp;
	}

    /**
     * ͨ���쳣����
     * 
     * @param respEvent
     * @param e
     * @return
     */
    private static BaseResponseEvent exceptionHandler(String txid, BaseCheckedException e){
    	BaseResponseEvent respEvent = new BaseResponseEvent();
		respEvent.setSuccess(false);
		logger.error(e.getExceptionMsg(),e);
		
		ExceptionInfo ei = new ExceptionInfo();
		ei.setExceptionCode(e.getCode());
		ei.setExceptionMsg(e.getExceptionMsg());
		ei.setStacktraces(e.getStackTrace());
		ei.setTxid(txid);
		respEvent.setExceptionInfo(ei);
    		
    	return respEvent;
    }

}
