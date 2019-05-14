package com.rab.framework.service.ejb;

import javax.ejb.EJBObject;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.service.BizServiceFacade;

public interface ServiceFacade extends EJBObject,BizServiceFacade {

	public BaseResponseEvent invoke(BaseRequestEvent reqEvent) throws Exception;
}