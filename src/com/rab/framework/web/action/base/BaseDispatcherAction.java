package com.rab.framework.web.action.base;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.exception.ExceptionInfo;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.ExceptionUtils;
import com.rab.framework.component.dictcache.ServerCacheManager;
import com.rab.framework.delegate.BizDelegate;
import com.rab.framework.web.action.ModuleAction;
import com.rab.framework.web.action.util.DataConvertor;
import com.rab.framework.web.action.vo.ComponentType;
import com.rab.framework.web.action.vo.ComponentVO;
import com.rab.framework.web.action.vo.data.AttrVO;
import com.rab.framework.web.action.vo.data.DataVO;

/**
 * 
 * <P>Title: BaseDispatcherAction</P>
 * <P>Description: </P>
 * <P>����˵����Action���࣬���ͨ�õ�ҳ������Ժ�̨BLH���ú���ת����ҳ�档</P>
 * <P>��Ҫ����������ת�Լ����ݵĸ�ʽת������</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-23</P>
 *
 */
public class BaseDispatcherAction  extends BaseAction{

	protected static final LogWritter logger = LogFactory
			.getLogger(BaseDispatcherAction.class);
	protected String resJson = null;
	protected String returnUrl = null;
	protected String errorUrl = "webframe/page/error/error.jsp";
	private String loginUrl =  "/login.jsp";//��¼ҳ��
	private String charSet = "utf-8";

	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String tid = request.getParameter("tid");
		String page = request.getParameter("page");
		if (tid == null) {
			tid = (String) request.getAttribute("tid");
		}
		if (page == null) {
			page = (String) request.getAttribute("page");
		}
		if (tid == null && page == null) {
			// tid��page������ͬʱΪ�գ�tidΪ�տ���ֱ����ת��ҳ�棬pageΪ�տ�����tid����BLH������page��Ϣ��
			Exception e = new Exception("tid��page������ͬʱΪ��");
			logger.error("��������쳣��", e);
			resJson = ExceptionUtils.transExceptionInfoToJson(e);
			logger.debug("resJson = " + resJson);
			returnUrl = errorUrl;
		}

		String json = request.getParameter("json");
		if (json == null) {
			json = (String) request.getAttribute("json");
		}
		logger.debug("requestjson = " + json);

		returnUrl = page;

		ComponentVO componentVO = null;
		try {
			if (StringUtils.isBlank(tid)) {
				// tidΪ��,������̨����
				if (!StringUtils.isBlank(json) || request.getParameter("action") != null) {
					if (!StringUtils.isBlank(json)) {
						componentVO = DataConvertor.convertJsonToVO(json,request);
					} else {
						componentVO = new ComponentVO();
						componentVO.setAction(request.getParameter("action"));
					}
					componentVO.setPage(page);
					setAttrs(request, componentVO);					
					DataRequestEvent req = new DataRequestEvent(null,
							getVHSessionId(), componentVO);
					DataResponseEvent resp = null;
					if (!StringUtils.isBlank(componentVO.getAction())) {
						if(componentVO.getAction().indexOf("module") >= 0){
							//��λ����ģ����ز���
							String method = null;
							ModuleAction moduleAction = new ModuleAction();
							if (componentVO.getAction().lastIndexOf("_") > 0) {
								method = componentVO.getAction().substring(
										componentVO.getAction().lastIndexOf("_") + 1);
								Method[] methods = ModuleAction.class.getMethods();
								for(Method m : methods){
									if(m.getName().equalsIgnoreCase(method)){
										Object arglist[] = new Object[1];
							            arglist[0] = req;
							            resp = (DataResponseEvent)m.invoke(moduleAction, arglist);
							            if(!resp.isSuccess()){
							            	HttpServletResponse response = ServletActionContext
											.getResponse();
											ExceptionInfo exceptionInfo = new ExceptionInfo();
											exceptionInfo.setExceptionMsg("��鵽�û�session�ѳ�ʱ�������µ�¼");											
											response.setContentType("text/html;charset=" + charSet);
											request.setAttribute("PageData", exceptionInfo.getExceptionMsg().replaceAll("'",
											"&apos;"));
											request.getRequestDispatcher(loginUrl).include(request, response);
											logger.debug("error = " + exceptionInfo.getExceptionMsg());
											return null;
							            }
							            resJson = JSONObject.fromObject(resp.getResComponentVO())
										.toString();
							            logger.debug("resVO = " + resJson);
									}
								}
							}
						}
					}
				}				
				
			} else {

				if (!StringUtils.isBlank(json)) {
					componentVO = DataConvertor.convertJsonToVO(json,request);
				} else {
					componentVO = new ComponentVO();
				}

				componentVO.setTid(tid);
				componentVO.setPage(page);

				// ��װ�������Ϊ��ɢ���ݽṹ�Թ���̨��ҳ�ȹ�����
				setAttrs(request, componentVO);	

				String method = null;
				if (tid.lastIndexOf("_") > 0) {
					method = tid.substring(tid.lastIndexOf("_") + 1);
					tid = tid.substring(0, tid.lastIndexOf("_"));
				}

				DataRequestEvent req = new DataRequestEvent(tid,
						getVHSessionId(), componentVO);
				if (method != null) {
					req.setMethod(method);
				}

				BaseResponseEvent response = BizDelegate.delegate(req);
				if (response.isSuccess()) {
					if(response.isFlushCachedDict()){
						//��Ҫ��ʱˢ�»�����������
						ServerCacheManager.getDictCacheManager().update();
					}
					
					DataResponseEvent resp = (DataResponseEvent) response;
					// ����������-����ת���ƴ���
					DataConvertor.addCacheData(resp);
					// ������Ƿ��з�ҳ��Ϣ���еĻ������ҳ������Ϣ
					DataConvertor.processPageInfoData(request, resp, JSONObject
							.fromObject(componentVO).toString());
					resJson = JSONObject.fromObject(resp.getResComponentVO())
							.toString();
					logger.debug("resVO = " + resJson);
					if (resp.getPage() != null && !"".equals(resp.getPage())) {
						returnUrl = resp.getPage();
					}
				} else {
					request.setAttribute("exceptionName", response
							.getExceptionInfo().getExceptionCode());
					request.setAttribute("exceptionMsg", response
							.getExceptionInfo().getExceptionMsg());
					returnUrl = errorUrl;
					// ���� ���� ���� ����
					return "success";
				}
			}

			if (returnUrl != null ) {
				// ��ֹstruts2����ת���ƣ��Լ����������
				ActionContext.getContext().getActionInvocation().getProxy()
						.setExecuteResult(false);

				HttpServletResponse response = ServletActionContext
						.getResponse();
				

				if (returnUrl.indexOf(".jsp") != -1) {
					response.setContentType("text/html;charset=" + charSet);
					request.setAttribute("PageData", resJson == null ? null : resJson.replaceAll("'",
							"&apos;"));
					request
							.getRequestDispatcher(
									"webframe/page/webfrmHeader.jsp").include(
									request, response);

					request.getRequestDispatcher(returnUrl).include(request,
							response);

				} else {
					String dataAp = "<div id=\"PageData\"  style='display:none;' data='"
							+ resJson == null ? "" : resJson.replaceAll("'", "&apos;") + "'></div>";
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
			} else {
				Exception e = new Exception("δ��ȡ����Ч����ת��ַ��");
				logger.debug("δ��ȡ����Ч����ת��ַ��", e);
				request.setAttribute("exceptionName", "�������");
				request.setAttribute("exceptionMsg", "δ��ȡ����Ч����ת��ַ��");
				returnUrl = errorUrl;
			}
		} catch (Exception e) {
			logger.error("error in execute", e);
			resJson = ExceptionUtils.transExceptionInfoToJson(e);
			logger.debug("resJson = " + resJson);
			returnUrl = errorUrl;
		}

		return "success";
	}

	private void setAttrs(HttpServletRequest request, ComponentVO componentVO ){
		Map parameterMap = request.getParameterMap();
		Map<String, DataVO> data = new HashMap<String, DataVO>();
		for (Object parameterName : parameterMap.keySet()) {
			String parameter = (String) parameterName;
			if (!"tid".equalsIgnoreCase(parameter)
					&& !"page".equalsIgnoreCase(parameter)
					&& !"json".equalsIgnoreCase(parameter)
					&& !"action".equalsIgnoreCase(parameter)) {
				String[] parameterValue = (String[])parameterMap.get(parameter);
				if(parameterValue != null && parameterValue.length > 0 && !"null".equalsIgnoreCase(parameterValue[0])){
					AttrVO attrVO = new AttrVO();
					attrVO.setType(ComponentType.WebfrmAttr);
					attrVO.setOriginalValue(parameterValue[0]);
					data.put(parameter, attrVO);
				}
				
			}
		}
		if (componentVO.getData() == null) {
			componentVO.setData(data);
		} else {
			componentVO.getData().putAll(data);
		}
	}

	/**
	 * @return the resJson
	 */
	public String getResJson() {
		return resJson;
	}

	/**
	 * @param resJson the resJson to set
	 */
	public void setResJson(String resJson) {
		this.resJson = resJson;
	}

	/**
	 * @return the returnUrl
	 */
	public String getReturnUrl() {
		return returnUrl;
	}

	/**
	 * @param returnUrl the returnUrl to set
	 */
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

}
