package com.rab.framework.service.local;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.domain.server.CoreAppServer;
import com.rab.framework.service.BaseBizServiceFacade;

/**
 * 
 * <P>Title: LocalDebugVHFacadeBean</P>
 * <P>Description: </P>
 * <P>����˵�������ط��ʽӿڣ�һ�����ڷ�EJB������Ӧ�÷���������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class LocalServiceFacadeBean extends BaseBizServiceFacade {

	private final static LogWritter logger = LogFactory.getLogger(LocalServiceFacadeBean.class);

//	public static final String DEBUG_EJB_SESSION_ID = "debug_ejb_sessionid";

	private LocalServiceFacadeBean() {

	}

	public static LocalServiceFacadeBean getInstance() {
		return new LocalServiceFacadeBean();
	}

	/**
	 * ����ӿ�
	 * 
	 * @param reqEvent
	 * @return
	 * @throws Exception
	 */
	public BaseResponseEvent invoke(BaseRequestEvent reqEvent) throws Exception {

    	BaseResponseEvent respEvent = null;
    	
       	try {
	    	//----------------------------------------------------
	    	//1. ��־Ԥ����
       		preProcessForPerfLog(reqEvent);
       		
	    	//----------------------------------------------------
	    	//2. ��ȫ��飬���������Ƿ����Ե�¼�û�
//	    	checkLogonState(reqEvent);
	    	
	    	//----------------------------------------------------

     		respEvent = CoreAppServer.getInstance().execute(reqEvent);
		} 
    	catch (BaseCheckedException e) {
    		respEvent = this.exceptionHandler( e);
		}
    	catch(Throwable e){
    		logger.error("00009999: δ֪�쳣",e);
    		BaseCheckedException e2 = new BaseCheckedException("00009999",e); 
    		respEvent = this.exceptionHandler(e2);
    	}
		
        return respEvent;
    }


}