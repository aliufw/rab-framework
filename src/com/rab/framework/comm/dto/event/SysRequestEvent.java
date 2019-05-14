package com.rab.framework.comm.dto.event;

public class SysRequestEvent extends BaseRequestEvent{

	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = -2198908861992832474L;

	/**
	 * 系统请求的标记信息
	 */
	private boolean sysfunction = true;
	
	/**
	 * 构造器
	 * 
	 * @param transactionID 必须参数，标识来源于服务器端业务服务逻辑编号
	 */
	public SysRequestEvent(String transactionID) {
		super(transactionID, "sys-sessionid");
	}
	
//	/**
//	 * 构造器
//	 * 
//	 * @param transactionID 必须参数，标识来源于服务器端业务服务逻辑编号 
//	 * @param sessionID 用户登录状态，来源于登录后系统返回的唯一标识字符串
//	 */
//	public SysRequestEvent(String transactionID, String sessionID) {
//		super(transactionID, sessionID);
//	}

	public boolean isSysfunction() {
		return sysfunction;
	}

}
