package com.rab.framework.service.ejb;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.domain.server.CoreAppServer;
import com.rab.framework.service.BaseBizServiceFacade;

/**
 * 
 * <P>Title: VHFacadeBean</P>
 * <P>Description: </P>
 * <P>����˵����EJB facade�ӿ�</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class ServiceFacadeBean extends BaseBizServiceFacade implements SessionBean  {

	private static final long serialVersionUID = -4730820213415577798L;

	private final static LogWritter logger = LogFactory.getLogger(ServiceFacadeBean.class);

    SessionContext sessionContext;
    
    public void ejbCreate() throws CreateException {
    }

    public void ejbRemove() {
    }

    public void ejbActivate() { 
    }

    public void ejbPassivate() {
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    protected void onEjbCreate() {
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
	    	
     		respEvent = CoreAppServer.getInstance().execute(reqEvent);
		} 
    	catch (BaseCheckedException e) {
    		//�ع�����
//    		sessionContext.setRollbackOnly();
    		respEvent = this.exceptionHandler(e);
		}
    	catch(Throwable e){
    		//�ع�����
//    		sessionContext.setRollbackOnly();
    		
    		logger.error("00009999: δ֪�쳣",e);
    		BaseCheckedException e2 = new BaseCheckedException("00009999",e); 
    		respEvent = this.exceptionHandler(e2);
    	}
		
        return respEvent;
    }

}