package com.rab.sys.security.login.event;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.security.LogonEnvironment;

public class ChangeModuleInfoRequestEvent extends BaseRequestEvent{
	
	
	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 1136772441622259709L;
	
	/**
	 * 用户登录系统后的环境变量
	 */
	private LogonEnvironment logonEnvironment;

	/**
	 * 构造器
	 * 
	 * @param transactionID 必须参数，标识来源于服务器端业务服务逻辑编号 
	 * @param sessionID 用户登录状态，来源于登录后系统返回的唯一标识字符串
	 */
	public ChangeModuleInfoRequestEvent(String transactionID, String sessionID) {
		super(transactionID,sessionID);
	}

	public LogonEnvironment getLogonEnvironment() {
		return logonEnvironment;
	}

	public void setLogonEnvironment(LogonEnvironment logonEnvironment) {
		this.logonEnvironment = logonEnvironment;
	}

	
}
