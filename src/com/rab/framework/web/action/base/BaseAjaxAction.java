package com.rab.framework.web.action.base;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.ExceptionUtils;
import com.rab.framework.component.dictcache.ServerCacheManager;
import com.rab.framework.delegate.BizDelegate;
import com.rab.framework.web.action.CacheAction;
import com.rab.framework.web.action.ModuleAction;
import com.rab.framework.web.action.util.DataConvertor;
import com.rab.framework.web.action.vo.ComponentType;
import com.rab.framework.web.action.vo.ComponentVO;
import com.rab.framework.web.action.vo.ResComponentVO;
import com.rab.framework.web.action.vo.data.DataVO;
import com.rab.framework.web.action.vo.data.GridVO;
import com.rab.framework.web.action.vo.data.ResGridVO;
import com.rab.framework.web.action.vo.data.ResTDSVO;
import com.rab.framework.web.dynamicsession.DynamicSessionManager;
import com.rab.sys.security.button.web.ButtonSecurityAction;

/**
 * 
 * <P>Title: BaseAjaxAction</P>
 * <P>Description: </P>
 * <P>����˵����Action���࣬���ͨ�õ�ҳ������Ժ�̨BLH���ã����÷�ʽΪAjax��</P>
 * <P>��Ҫ����������ת�Լ����ݵĸ�ʽת������</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-9</P>
 *
 */
public class BaseAjaxAction extends BaseAction {

	protected static final LogWritter logger = LogFactory
			.getLogger(BaseAjaxAction.class);
	protected ResComponentVO resVO = null;

	public String execute() {
		String returnUrl = "result";
		HttpServletRequest request = ServletActionContext.getRequest();
		String json = request.getParameter("json");
		if (json == null) {
			json = (String) request.getAttribute("json");
		}
		if (json == null) {
			logger.info("json����Ϊ��.");
			resVO = ExceptionUtils.transExceptionInfo(new Exception("json����Ϊ��"));
			logger.debug("resVO = " + resVO);
		}
		logger.debug("requestjson = " + json);
		try {
			ComponentVO componentVO = DataConvertor.convertJsonToVO(json, request);

			String tid = null;
			DataRequestEvent req = null;
			DataResponseEvent resp = null;
			if (StringUtils.isBlank(componentVO.getTid())) {
				logger.info("û��tid");
				req = new DataRequestEvent(null, getVHSessionId(), componentVO);
				if(!StringUtils.isBlank(componentVO.getAction())){
					if(componentVO.getAction().equalsIgnoreCase("cache")){
						//ȡ�����������ݴ���
						CacheAction cacheAction = new CacheAction();
						resp = (DataResponseEvent) cacheAction.doAction(req);
					}else if(componentVO.getAction().equalsIgnoreCase("buttonsecurity")){
						ButtonSecurityAction securityAction = new ButtonSecurityAction();
						resp = (DataResponseEvent) securityAction.doAction(req);
						
					}else if(componentVO.getAction().indexOf("module") >= 0){
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
								}
							}
						}
					}
					
				}
				
				DataConvertor.addCacheData(resp);
				resVO = resp == null ? null : resp.getResComponentVO();
				try{
					logger.debug("resVO = " + JSONObject.fromObject(resVO).toString());
				}catch(Exception e){
					
				}
			} else {
				//��Դ洢��session�ı�����ݴ���
				for(Entry<String, DataVO> dataEntry : componentVO.getData().entrySet()){
					DataVO data = dataEntry.getValue();
					if(ComponentType.WebfrmGrid.equals(data.getType())){
						//������ͣ�����Ƿ��з�ҳ��Ϣ
						GridVO gridVO = (GridVO)data;
						if(gridVO.getPageInfo() != null && gridVO.getPageInfo().getPageModelFlag() != null 
								&& gridVO.getPageInfo().getPageModelFlag().equalsIgnoreCase(gridVO.getPageInfo().PAGE_MODEL_SESSION)
								&& gridVO.getPageInfo().getCacheName() != null){
							//�ж���session����û������
							if (DynamicSessionManager.singleton().getData(request, gridVO.getPageInfo().getCacheName()) != null) {
				            		//��������ݴ�����ֱ�ӷ���
				            		ResGridVO resGridVO = (ResGridVO)DynamicSessionManager.singleton().getData(request, gridVO.getPageInfo().getCacheName());
					            	ResTDSVO[] trs = resGridVO.getTrs();
					            	int size = ((gridVO.getPageInfo().getPageIndex() + 1) * gridVO.getPageInfo().getRowsPerPage() > trs.length ?
				            				trs.length - gridVO.getPageInfo().getPageIndex() * gridVO.getPageInfo().getRowsPerPage() : gridVO.getPageInfo().getRowsPerPage());
					            	ResTDSVO[] trsReturn = new ResTDSVO[size];
					            	for(int i = gridVO.getPageInfo().getPageIndex() * gridVO.getPageInfo().getRowsPerPage() ; 
					            		i < ((gridVO.getPageInfo().getPageIndex() + 1) * gridVO.getPageInfo().getRowsPerPage() > trs.length ?
					            				trs.length : (gridVO.getPageInfo().getPageIndex() + 1) * gridVO.getPageInfo().getRowsPerPage())
					            		; i++){
					            		trsReturn[i - gridVO.getPageInfo().getPageIndex() * gridVO.getPageInfo().getRowsPerPage()] = trs[i];
					            	}
					            	resp = new DataResponseEvent();
					            	resp.setSuccess(true);
					            	ResGridVO grid = new ResGridVO();
					            	grid.setType(ComponentType.WebfrmGrid);
					            	grid.setPageInfo(gridVO.getPageInfo());
					            	grid.getPageInfo().setQueryParams(json);
					            	grid.setTrs(trsReturn);
					            	resp.getResComponentVO().getData().put(dataEntry.getKey(), grid);
					            	
					            	resVO = resp.getResComponentVO();
					            	try{
										logger.debug("resVO = " + JSONObject.fromObject(resVO).toString());
									}catch(Exception e){
										
									}
					            	return returnUrl;
				            }
						}
					}
				}
				String method = null;
				if (componentVO.getTid().lastIndexOf("_") > 0) {
					method = componentVO.getTid().substring(
							componentVO.getTid().lastIndexOf("_") + 1);
					tid = componentVO.getTid().substring(0,
							componentVO.getTid().lastIndexOf("_"));
				}
				req = new DataRequestEvent(tid, getVHSessionId(), componentVO);
				if (method != null) {
					req.setMethod(method);
				}

				BaseResponseEvent response = BizDelegate.delegate(req);
				
				//����һ�²����õ��Ķ���
				req = null; 
				
				if (response.isSuccess()) {
					if(response.isFlushCachedDict()){
						//��Ҫ��ʱˢ�»�����������
						ServerCacheManager.getDictCacheManager().update();
					}
					resp = (DataResponseEvent) response;
					//����������-����ת���ƴ���
					DataConvertor.addCacheData(resp);
					//������Ƿ��з�ҳ��Ϣ���еĻ������ҳ������Ϣ
					DataConvertor.processPageInfoData(request, resp, json);
					
					resVO = resp.getResComponentVO();
					try{
						logger.debug("resVO = " + JSONObject.fromObject(resVO).toString());
					}catch(Exception e){
						
					}
				} else {
					resVO = ExceptionUtils.transExceptionInfo(response.getExceptionInfo());
					logger.debug("resVO = " + resVO);
				}
			}
			//����һ�²����õ��Ķ���
			componentVO = null;
		} catch (Exception e) {
			logger.error("error in execute", e);
			resVO = ExceptionUtils.transExceptionInfo(e);
			logger.debug("resVO = " + resVO);
		}
		return returnUrl;
	}

	/**
	 * @return the resVO
	 */
	public ResComponentVO getResVO() {
		return resVO;
	}

	/**
	 * @param resVO
	 *            the resVO to set
	 */
	public void setResVO(ResComponentVO resVO) {
		this.resVO = resVO;
	}
}
