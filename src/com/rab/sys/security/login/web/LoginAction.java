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
 * <P>����˵����ִ�е�¼��Action</P>
 * <P>��Ҫ����������¹��ܣ�</P>
 * <P>1. ���տͻ��˷��͵ĵ�¼���󣬲���������Ϣ��ʽ���󣬴��͸�ҵ�������¼����</P>
 * <P>2. ��ҵ��㷵�ص�ƾ֤��Ϣ�������������У������web��ҵ�񻷾��ĳ�ʼ��</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-11</P>
 *
 */
public class LoginAction extends BaseAction {
	/**
	 * ��־����
	 */
	protected static final LogWritter logger = LogFactory
			.getLogger(LoginAction.class);
	private static final String SESSION_FLAG = "TICKET";  //��  TimeOutFilter �ı�ʶ����һ��
	private String resJson = "{data:{}}";
	private String loginUrl =  "/login.jsp";//��¼ҳ��
	private String returnUrl =  "/page/mainpage/mainpanel.jsp";//��¼����ҳ��
	private String chooseUrl =  "/page/mainpage/opener.jsp";//��¼����ҳ��
	private String charSet = "utf-8";

	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		// 1. �����û���������
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

		// 2. �����������
//		String userSessionid = ""; // ��¼��������ʱϵͳ�л�û��userSessionid
		LoginRequestEvent req = new LoginRequestEvent("LoginBLH");
		req.setMethod("login");
		req.setUsercode(usercode);
		req.setPasswd(passwd);

		// 3. ִ�е�¼����
		try {
			// 3.1 ��������˷��͵�¼����
			BaseResponseEvent resp = (BaseResponseEvent) BizDelegate
					.delegate(req);

			// 3.2 �����ؽ��
			if (resp.isSuccess()) {
				// 3.2.1 �ӷ��ض�������ȡ ticket
				LoginResponseEvent loginResp = (LoginResponseEvent) resp;
				Ticket ticket = loginResp.getTicket();
			

				// 3.2.2 ��ticket���浽httpSession��
				request.getSession().setAttribute(LoginAction.SESSION_FLAG, ticket);

				User user = ticket.getUser();
				
				if(user.isAdmin()){
					// ����Ա����ѡ���¼������Ϣ��Ĭ��ֻ��ϵͳ����ģ��ɲ���
					//�����µĵ�λ�����ס�ģ����Ϣ������BLHͬ������̨domainSession
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
						//����Աû��ϵͳ����Ȩ��
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
						//�����µĵ�λ�����ס�ģ����Ϣ������BLHͬ������̨domainSession
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
						//�ж������ģ��Ȩ�޵���ͨ�û����ض���ѡ��λ�����ס�ģ�顢���ڵ�ҳ��
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
			request.setAttribute("PageData", "��¼�쳣!");
			try {
				request.getRequestDispatcher(loginUrl).include(request, response);
				
			} catch (Exception e1) {
				logger.error("error = " + e1.getMessage());
			} 
			return null;
		}
		
		try {
			if (returnUrl != null && resJson != null) {
				// ��ֹstruts2����ת���ƣ��Լ����������
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
					// ���� ' ת &apos; ����ǰ̨��������������� (��ʱʹ�ã��п����о�)
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
				// TODO ��װ�쳣�������				
				resJson = ExceptionUtils.transExceptionInfoToJson(new RuntimeException("δ��ȡ����Ч����ת��ַ��"));
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
//		//����node����װ��json���ݶ��󣬿���ϵͳ��ת��������ҳ��
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
