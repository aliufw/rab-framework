package com.rab.sys.security;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.FuncRightPrincipal;
import com.rab.framework.comm.security.FuncRightResource;
import com.rab.framework.comm.security.LogonSubject;
import com.rab.framework.comm.security.Ticket;
import com.rab.framework.comm.security.TicketImpl;
import com.rab.framework.comm.security.User;
import com.rab.framework.comm.security.SecurityManager;
import com.rab.framework.comm.security.SecurityManagerFactory;
import com.rab.framework.comm.util.TreeCreatorJson;
import com.rab.framework.delegate.BizDelegate;
import com.rab.framework.domain.server.StartPlatform;
import com.rab.sys.security.login.event.LoginRequestEvent;
import com.rab.sys.security.login.event.LoginResponseEvent;

public class Test_Security {
	private static final LogWritter logger = LogFactory.getLogger(Test_Security.class);

	public void test_login(){
		
		StartPlatform sp = new StartPlatform();
		sp.start();
		
		LoginRequestEvent req = new LoginRequestEvent("LoginBLH");
		req.setUsercode("02");
		req.setPasswd("02");
		req.setMethod("login");
		
		try {
			BaseResponseEvent resp = BizDelegate.delegate(req);
			
			System.out.println("返回状态： " + resp.isSuccess());
			LoginResponseEvent loginResp = (LoginResponseEvent)resp;
			TicketImpl ticket = loginResp.getTicket();
			System.out.println(ticket);
			
			//1. 输出状态的基本信息
			User user = (User)ticket.getUser();
			System.out.println("返回信息：\r\n=====================================");
			System.out.println("usercode \t\t= " + user.getUsercode());
			System.out.println("username \t= " + user.getUsername());
			
			System.out.println("sessionid \t= " + ticket.getUserSessionid());
			System.out.println("logonTime \t= " + ticket.getLogonTime());
			System.out.println("lastAccessTime \t= " + ticket.getLastAccessTime());
			
			//2. 输出功能权限信息
			List<FuncRightResource> menuList = new ArrayList<FuncRightResource>();
			
			LogonSubject subject = (LogonSubject)ticket.getSubject();
			System.out.println("subject \t= " + subject);
			
			Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> allFuncPrincipals = subject.getFuncPrincipals();
			
//			Map<String,FuncRightPrincipal> funcPrincipals = allFuncPrincipals.get("100101").get("001").get("02");
//			Map<String,FuncRightPrincipal> funcPrincipals = allFuncPrincipals.get(LoginAuthorizationManager.ADMIN_COMPANY_CODE).get(LoginAuthorizationManager.ADMIN_COPY_CODE).get("01");
			Map<String,FuncRightPrincipal> funcPrincipals = allFuncPrincipals.get("100101").get("001").get("02");

			Iterator<FuncRightPrincipal> iter = funcPrincipals.values().iterator();
			while(iter.hasNext()){
				FuncRightPrincipal principal = iter.next();
				FuncRightResource frr = principal.getFuncRightRes();
				menuList.add(frr);
			}
			
			
			System.out.println("menuList.size()\t= " + menuList.size());
			for(int i=0; i<menuList.size(); i++){
				System.out.println(menuList.get(i));
			}
			
			TreeCreatorXML tc = new TreeCreatorXML();
			StringBuffer sb = tc.getXMLTree(menuList);
//			System.out.println(sb);
			File file = new File("C:/tree.xml");
			FileWriter fw = new FileWriter(file);
			fw.write(sb.toString());
			fw.flush();
			fw.close();
			
			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("code", "funcId");
			nameMap.put("caption", "permName");
			nameMap.put("qtip", "permName");
			nameMap.put("url", "funcUri");
			nameMap.put("pcode", "parentId");
			nameMap.put("leaf", "funcType");
			
			List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>> ();
			for(Object menu : menuList){
				nodes.add(BeanUtils.describe(menu));
			}
			
			TreeCreatorJson tcj = new TreeCreatorJson();
			
			System.out.println(tcj.getJsonTree(nodes, null, nameMap));
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void test(){
		SecurityManager sm = SecurityManagerFactory.getSecurityManager();
		System.out.println(sm);
	}
	
	public Ticket login2(){
		
		LoginRequestEvent req = new LoginRequestEvent("LoginBLH");
		req.setUsercode("02");
		req.setPasswd("02");
		req.setMethod("login");

		TicketImpl ticket = null;
		try {
			BaseResponseEvent resp = (BaseResponseEvent) BizDelegate.delegate(req);
			System.out.println("返回状态： " + resp.isSuccess());
			LoginResponseEvent loginResp = (LoginResponseEvent)resp;
			ticket = loginResp.getTicket();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ticket.getUser().getUsercode());
		
		LogonSubject subject = ticket.getSubject();

		String permid = "SY109dmzdyBLH_init";
		
		Map<String,Map<String,Map<String,Map<String,FuncRightPrincipal>>>> funcRightPrincipals = subject.getFuncPrincipals();
		boolean flag = false;
//		Iterator<Map<String,Map<String,Map<String,DefaultFuncRightPrincipal>>>> iterComp = funcRightPrincipals.values().iterator();
		Iterator<String> iterCompCode = funcRightPrincipals.keySet().iterator();
		while(iterCompCode.hasNext()){
			//单位
			String compCode = iterCompCode.next();
			Map<String,Map<String,Map<String,FuncRightPrincipal>>> mapComp = funcRightPrincipals.get(compCode);
			
//			Iterator<Map<String,Map<String,DefaultFuncRightPrincipal>>> iterCopy = mapComp.values().iterator();
			Iterator<String> iterCopyCode = mapComp.keySet().iterator();
			while(iterCopyCode.hasNext()){
				//帐套
				String copyCode = iterCopyCode.next();
				Map<String,Map<String,FuncRightPrincipal>> mapCopy = mapComp.get(copyCode);
				
//				Iterator<Map<String,DefaultFuncRightPrincipal>> iterMod = mapCopy.values().iterator();
				Iterator<String> iterModCode = mapCopy.keySet().iterator();
				while(iterModCode.hasNext()){
					//模块
					String modCode = iterModCode.next();
					Map<String,FuncRightPrincipal> mapMod = mapCopy.get(modCode);
					
					logger.debug("运行时权限检查：检查" + permid + "是否是功能模块" + compCode + "-" + copyCode + "-" + modCode + "内的权限！");
					
					Iterator<String> iterFuncid = mapMod.keySet().iterator();
					while(iterFuncid.hasNext()){
						String funcid = iterFuncid.next();
						FuncRightPrincipal principal = mapMod.get(funcid);
						System.out.println("***" 
						+ "\t" +principal.getFuncRightRes().getFuncId() 
						+ "\t" +principal.getFuncRightRes().getParentId()
						+ "\t" + principal.getFuncRightRes().getPermId() 
						+ "\t" +principal.getFuncRightRes().getPermName() 
						);

						if(permid.equals(principal.getFuncRightRes().getPermId())){
							flag = true;
							break;
						}
					}
					
					if(flag){
						break;
					}
				}
				
				if(flag){
					break;
				}
			}
			if(flag){
				break;
			}

		}
		
		System.out.println("flag = " + flag);
		
		return ticket;
	}
	
	public static void main(String[] argv){
		Test_Security ts = new Test_Security();
		ts.test_login();
	}
}
