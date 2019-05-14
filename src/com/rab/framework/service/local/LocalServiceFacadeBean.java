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
 * <P>程序说明：本地访问接口，一般用于非EJB容器的应用服务器场景</P>
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
	 * 服务接口
	 * 
	 * @param reqEvent
	 * @return
	 * @throws Exception
	 */
	public BaseResponseEvent invoke(BaseRequestEvent reqEvent) throws Exception {

    	BaseResponseEvent respEvent = null;
    	
       	try {
	    	//----------------------------------------------------
	    	//1. 日志预处理
       		preProcessForPerfLog(reqEvent);
       		
	    	//----------------------------------------------------
	    	//2. 安全检查，检查该请求是否来自登录用户
//	    	checkLogonState(reqEvent);
	    	
	    	//----------------------------------------------------

     		respEvent = CoreAppServer.getInstance().execute(reqEvent);
		} 
    	catch (BaseCheckedException e) {
    		respEvent = this.exceptionHandler( e);
		}
    	catch(Throwable e){
    		logger.error("00009999: 未知异常",e);
    		BaseCheckedException e2 = new BaseCheckedException("00009999",e); 
    		respEvent = this.exceptionHandler(e2);
    	}
		
        return respEvent;
    }


}