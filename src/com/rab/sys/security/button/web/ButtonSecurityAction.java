package com.rab.sys.security.button.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.security.SecurityManager;
import com.rab.framework.comm.security.SecurityManagerFactory;
import com.rab.framework.web.action.base.BaseAction;

/**
 * °´Å¥È¨ÏÞ
 * @Description£º
 * @Author£ºZhangBin
 * @Date£º2010-10-9
 */
public class ButtonSecurityAction  extends BaseAction{
	
	private static final String SECURITY_LIST = "webfrmSecurityList";
	
	public BaseResponseEvent doAction(BaseRequestEvent reqEvent)
			throws Exception {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		
		String listStr = StringUtils.trimToEmpty((String)req.getAttr(ButtonSecurityAction.SECURITY_LIST));
		if(StringUtils.isNotBlank(listStr)){
			String[] sp = listStr.split(",");
			SecurityManager sm = SecurityManagerFactory.getSecurityManager();
			HttpServletRequest request = ServletActionContext.getRequest();
			for(int i=0;i<sp.length;i++){
				res.addAttr(sp[i], sm.securityPermidCheck(sp[i], request));
				
			}
			
		}
		
		return res;
	}
}