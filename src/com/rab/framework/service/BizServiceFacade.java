package com.rab.framework.service;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;

/**
 * 
 * <P>Title: IBizServiceFacade</P>
 * <P>Description: </P>
 * <P>程序说明：业务层服务封装接口</P>
 * <P>本接口定义了业务层的接入方式，在实现时，可根据具体的应用架构需求，分别给予不同的实现</P>
 * <P>例如：</P>
 * <li>EJB接口：采用EJB技术封装业务层服务，适用于网格化、分布式应用环境</li>
 * <li>本地接口：采用javaBean技术封装业务层服务，适用于规模较小的、集中化部署环境</li>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-9</P>
 *
 */
public interface BizServiceFacade {
	/**
	 * 服务接口
	 * 
	 * @param reqEvent
	 * @return
	 * @throws Exception
	 */
	public BaseResponseEvent invoke(BaseRequestEvent reqEvent) throws Exception ;
}
