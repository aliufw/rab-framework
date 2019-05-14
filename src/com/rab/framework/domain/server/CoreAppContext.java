package com.rab.framework.domain.server;

import java.util.Map;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.dao.PersistenceManager;
import com.rab.framework.domain.domainconfig.ModelConfig;


/**
 * 
 * <P>Title: VHAppContext</P>
 * <P>Description: </P>
 * <P>程序说明：核心业务逻辑处理的Context环境管理器</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public interface CoreAppContext extends Cloneable{
	public Object clone() throws CloneNotSupportedException;
	public void setServer(CoreAppServer server);
	public BaseResponseEvent execute(BaseRequestEvent reqEvent) throws BaseCheckedException;
	public ModelConfig getModelConfig();
	public Map<String,PersistenceManager> getCtxPersistenceManagers();
	public CoreAppServer getServer();
}

