package com.rab.sys.security.login.web;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.ExceptionInfo;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.FuncRightPrincipal;
import com.rab.framework.comm.security.LogonEnvironment;
import com.rab.framework.comm.security.LogonSubject;
import com.rab.framework.comm.security.Ticket;
import com.rab.framework.comm.security.User;
import com.rab.framework.comm.util.ExceptionUtils;
import com.rab.framework.delegate.BizDelegate;
import com.rab.framework.web.action.base.BaseAction;
import com.rab.sys.security.LoginAuthorizationManager;
import com.rab.sys.security.login.event.LoginRequestEvent;
import com.rab.sys.security.login.event.LoginResponseEvent;

/**
 * 
 * <P>Title: LoginAction</P>
 * <P>Description: </P>
 * <P>程序说明：执行登录的Action</P>
 * <P>主要负责完成如下功能：</P>
 * <P>1. 接收客户端发送的登录请求，并将请求信息格式化后，传送给业务层做登录操作</P>
 * <P>2. 将业务层返回的凭证信息保存在上下文中，以完成web端业务环境的初始化</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-11</P>
 *
 */
public class LoginAction extends BaseAction {
	/**
	 * 日志对象
	 */
	protected static final LogWritter logger = LogFactory
			.getLogger(LoginAction.class);
	private static final String SESSION_FLAG = "TICKET";  //与  TimeOutFilter 的标识保持一致
	private String resJson = "{data:{}}";
	private String loginUrl =  "/login.jsp";//登录页面
	private String returnUrl =  "/page/mainpage/mainpanel.jsp";//登录后主页面
	private String chooseUrl =  "/page/mainpage/opener.jsp";//登录后主页面
	private String charSet = "utf-8";

	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		// 1. 接收用户名和密码
		String usercode = request.getParameter("userid");
		String passwd = request.getParameter("passwd");
		
		if(usercode == null || passwd == null){
			HttpServletResponse response = ServletActionContext
			.getResponse();
			try {
				response.setContentType("text/html;charset=" + charSet);
				request.getRequestDispatcher(loginUrl).include(request, response);
				return null;
			} catch (Exception e) {
			}
		}

		// 2. 创建请求对象
//		String userSessionid = ""; // 登录操作，此时系统中还没有userSessionid
		LoginRequestEvent req = new LoginRequestEvent("LoginBLH");
		req.setMethod("login");
		req.setUsercode(usercode);
		req.setPasswd(passwd);

		// 3. 执行登录操作
		try {
			// 3.1 向服务器端发送登录请求
			BaseResponseEvent resp = (BaseResponseEvent) BizDelegate
					.delegate(req);

			// 3.2 处理返回结果
			if (resp.isSuccess()) {
				// 3.2.1 从返回对象中提取 ticket
				LoginResponseEvent loginResp = (LoginResponseEvent) resp;
				Ticket ticket = loginResp.getTicket();
			

				// 3.2.2 将ticket缓存到httpSession中
				request.getSession().setAttribute(LoginAction.SESSION_FLAG, ticket);

				User user = ticket.getUser();
				
				if(user.isAdmin()){
					// 管理员不必选择登录环境信息，默认只有系统管理模块可操作
					//设置新的单位、账套、模块信息并调用BLH同步到后台domainSession
					LogonSubject subject = (LogonSubject) ticket.getSubject();
					//------------------------------------------
					Map<String, Map<String, Map<String, Map<String, FuncRightPrincipal>>>> allFuncPrincipals = subject
							.getFuncPrincipals();
					
					if(allFuncPrincipals == null || allFuncPrincipals.size() == 0 ||
							allFuncPrincipals.get(LoginAuthorizationManager.ADMIN_COMPANY_CODE) == null || allFuncPrincipals.get(LoginAuthorizationManager.ADMIN_COMPANY_CODE).size() == 0 || 
							allFuncPrincipals.get(LoginAuthorizationManager.ADMIN_COMPANY_CODE).get(LoginAuthorizationManager.ADMIN_COPY_CODE) == null ||
							allFuncPrincipals.get(LoginAuthorizationManager.ADMIN_COMPANY_CODE).get(LoginAuthorizationManager.ADMIN_COPY_CODE).size() == 0 ||
							allFuncPrincipals.get(LoginAuthorizationManager.ADMIN_COMPANY_CODE).get(LoginAuthorizationManager.ADMIN_COPY_CODE).get("01") == null ||
							allFuncPrincipals.get(LoginAuthorizationManager.ADMIN_COMPANY_CODE).get(LoginAuthorizationManager.ADMIN_COPY_CODE).get("01").size() == 0){
						//管理员没有系统管理权限
						HttpServletResponse response = ServletActionContext
						.getResponse();
						
						response.setContentType("text/html;charset=" + charSet);
						request.setAttribute("nomodule", "nomodule");
						request.getRequestDispatcher("webframe/page/webfrmHeader.jsp").include(
								request, response);
						request.getRequestDispatcher(loginUrl).include(request, response);
						return null;
					}
					
					LogonEnvironment logonEnvironment = super.getLogonEnvironment();

					logonEnvironment.setOrgId(LoginAuthorizationManager.ADMIN_COMPANY_CODE);
					logonEnvironment.setSOB(LoginAuthorizationManager.ADMIN_COPY_CODE);
					logonEnvironment.setModule("01");
					
					request.setAttribute("single", "true");					
					request.setAttribute("company", LoginAuthorizationManager.ADMIN_COMPANY_CODE.replaceAll("'", "&apos;"));
					request.setAttribute("set", LoginAuthorizationManager.ADMIN_COPY_CODE.replaceAll("'", "&apos;"));
					request.setAttribute("module", "01");
					
					returnUrl = chooseUrl;

				}else{
					LogonSubject subject = (LogonSubject) ticket.getSubject();
					//------------------------------------------
					Map<String, Map<String, Map<String, Map<String, FuncRightPrincipal>>>> allFuncPrincipals = subject
							.getFuncPrincipals();
					
					if(allFuncPrincipals == null || allFuncPrincipals.size() == 0 ||
							allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]) == null || allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]).size() == 0 || 
							allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]).get(allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]).keySet().toArray()[0]) == null ||
							allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]).get(allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]).keySet().toArray()[0]).size() == 0){
						HttpServletResponse response = ServletActionContext
						.getResponse();
						
						response.setContentType("text/html;charset=" + charSet);
						request.setAttribute("nomodule", "nomodule");
						request.getRequestDispatcher("webframe/page/webfrmHeader.jsp").include(
								request, response);
						request.getRequestDispatcher(loginUrl).include(request, response);
						return null;
					}
					
					if(allFuncPrincipals != null && allFuncPrincipals.size() == 1 && 
							allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]).size() == 1 && 
							allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]).get(allFuncPrincipals.get(allFuncPrincipals.keySet().toArray()[0]).keySet().toArray()[0]).size() == 1){
						String company = (String)allFuncPrincipals.keySet().toArray()[0];
						String set = (String)(allFuncPrincipals.get(company).keySet().toArray()[0]);
						String module = (String)(allFuncPrincipals.get(company).get(set).keySet().toArray()[0]);
						//设置新的单位、账套、模块信息并调用BLH同步到后台domainSession
						LogonEnvironment logonEnvironment = super.getLogonEnvironment();
 
						logonEnvironment.setOrgId(company);
						logonEnvironment.setSOB(set);
						logonEnvironment.setModule(module);
						if(!"02".equals(module)){
							request.setAttribute("single", "true");
						}				
						request.setAttribute("company", company);
						request.setAttribute("set", set);
						request.setAttribute("module", module);
						
						returnUrl = chooseUrl;
					}else{
						//有多个功能模块权限的普通用户，重定向到选择单位、帐套、模块、日期的页面
						returnUrl = chooseUrl;
						resJson = "{data:{}}";
					}
				}
							
			}  else {
				HttpServletResponse response = ServletActionContext
				.getResponse();
				ExceptionInfo exceptionInfo = resp.getExceptionInfo();
				
				response.setContentType("text/html;charset=" + charSet);
				request.setAttribute("PageData", exceptionInfo.getExceptionMsg().replaceAll("'",
				"&apos;"));
				request.getRequestDispatcher(loginUrl).include(request, response);
//				request.getRequestDispatcher("webframe/page/webfrmHeader.jsp").include(
//						request, response);

				logger.debug("error = " + exceptionInfo.getExceptionMsg());
				return null;
			}

		} catch (Exception e) {
			logger.error("error = " + e.getMessage());
			HttpServletResponse response = ServletActionContext
			.getResponse();			
			response.setContentType("text/html;charset=" + charSet);
			request.setAttribute("PageData", "登录异常!");
			try {
				request.getRequestDispatcher(loginUrl).include(request, response);
				
			} catch (Exception e1) {
				logger.error("error = " + e1.getMessage());
			} 
			return null;
		}
		
		try {
			if (returnUrl != null && resJson != null) {
				// 中止struts2的跳转机制，自己来处理输出
				ActionContext.getContext().getActionInvocation().getProxy()
						.setExecuteResult(false);

				HttpServletResponse response = ServletActionContext
						.getResponse();
				

				if (returnUrl.indexOf(".jsp") != -1) {
					response.setContentType("text/html;charset=" + charSet);
					request.setAttribute("PageData", resJson.replaceAll("'",
							"&apos;"));
					request.getRequestDispatcher("webframe/page/webfrmHeader.jsp").include(
									request, response);

					request.getRequestDispatcher(returnUrl).include(request, response);

				} else {
					String dataAp = "<div id=\"PageData\"  style='display:none;' data='"
							+ resJson.replaceAll("'", "&apos;") + "'></div>";					
					response.setContentType("text/html;charset=" + charSet);
					// 单引 ' 转 &apos; 否则到前台解析错误，阻断流程 (临时使用，有空再研究)
					// by ZhangBin
					PrintWriter out = new PrintWriter(new OutputStreamWriter(
							response.getOutputStream(), charSet));
					request.getRequestDispatcher(returnUrl).include(request,
							response);
					out.println(dataAp);
					out.flush();
					out.close();
				}
				return null;
			}else {
				// TODO 封装异常发给哪里？				
				resJson = ExceptionUtils.transExceptionInfoToJson(new RuntimeException("未获取到有效的跳转地址！"));
				logger.debug("resJson = " + resJson);
			}
		} catch (Exception e) {
			resJson = ExceptionUtils.transExceptionInfoToJson(e);
			logger.debug("resJson = " + resJson);
		}
		return "success";
	}
//	private void constructTree(Map<String, FuncRightPrincipal> funcPrincipals) throws Exception{
//		Map<String, String> nameMap = new HashMap<String, String>();
//		nameMap.put("code", "funcId");
//		nameMap.put("caption", "permName");
//		nameMap.put("qtip", "permName");
//		nameMap.put("url", "funcUri");
//		nameMap.put("pcode", "parentId");
//		nameMap.put("leaf", "funcType");
//		
//		List<Object> menuList = new ArrayList<Object>();
//		Iterator<FuncRightPrincipal> iter = funcPrincipals.values().iterator();
//		while (iter.hasNext()) {
//			FuncRightPrincipal principal = iter.next();
//				FuncRightResource mfrr = principal.getFuncRightRes();
//				menuList.add(mfrr);
//		}
//		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>> ();
//		for(Object menu : menuList){
//			nodes.add(BeanUtils.describe(menu));
//		}
//		
//		TreeCreatorJson tcj = new TreeCreatorJson();
//		//根据node，封装成json数据对象，控制系统跳转到主工作页面
//		List<Map<String, Object>> nodeList = tcj.getJsonTree(nodes, null, nameMap);
//		Map[] data = new Map[nodeList.size()];
//		for (int i = 0; i < nodeList.size(); i++) {
//			data[i] =  nodeList.get(i);
//		}
//		
//		ResTreeVO treeVO = new ResTreeVO();
//		treeVO.setType(ComponentType.WebfrmTree);
//		treeVO.setData(data);
//		ResComponentVO resComponentVO = new ResComponentVO();
//		resComponentVO.getData().put("tree", treeVO);
//		
//		AttrVO attrVO = new AttrVO();
//		attrVO.setType(ComponentType.WebfrmAttr);
//		attrVO.setOriginalValue("false");
//		resComponentVO.getData().put("changeModuleFlag",attrVO);
//		
//		resJson = JSONObject.fromObject(resComponentVO).toString();
//		logger.debug("" + resJson);
//	}
}
