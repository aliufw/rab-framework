package com.rab.framework.web.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.SecurityManager;
import com.rab.framework.comm.security.SecurityManagerFactory;

public class WebServerSecurityCheckFilter implements Filter {
	/**
	 * ��־��¼��
	 */
	protected static final LogWritter logger = LogFactory.getLogger(WebServerSecurityCheckFilter.class);
	/**
	 * Э������
	 */
    private static final String HTTP_PROTOCOL1_1 = "HTTP/1.1";
    private static final String HTTP_PROTOCOL1_0 = "HTTP/1.0";  //IE8 
	
	/**
	 * ���������л�������
	 */
	protected FilterConfig filterConfig = null;
	
	/**
	 * ����������״̬
	 */
    protected boolean state = true;
    
    /**
     * Ȩ�޼��û��ͨ��ʱ��ת����ʾҳ��
     */
    private String falseToRedirectUrl = null;

    public void init(FilterConfig filterConfig) throws ServletException {
    	logger.debug("��ʼ�� WebServerSecurityCheckFilter...");
        this.filterConfig = filterConfig;
        
        this.falseToRedirectUrl = filterConfig.getInitParameter("false-to-redirect-url");
        logger.debug("falseToRedirectUrl = " + falseToRedirectUrl);
        
        //����Ƿ���Ըù������Ĳ���
        String value = filterConfig.getInitParameter("state");
        if (value == null || value.equalsIgnoreCase("on")){
            this.state = true;
        } else {
            this.state = false;
        }
        logger.debug("state = " + state);
        
       	logger.debug("��ʼ�� WebServerSecurityCheckFilter ����");

    }

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// �жϷ������ͣ�ֻ��HTTPЭ������������
		String protocol = request.getProtocol();
		if (!protocol.trim().equalsIgnoreCase(HTTP_PROTOCOL1_0)
				&& !protocol.trim().equalsIgnoreCase(HTTP_PROTOCOL1_1)) {

			chain.doFilter(request, response);
			return;
		}

		//����ܿ����Ƿ�򿪣����Ϊfalse����������ȫ���˼���߼�
		boolean flag = ApplicationContext.singleton().checkRuntimeSecurityManager();
		if(flag == false){
			chain.doFilter(request, response);
			return;
		}

		if (state) {
			boolean doNextFilter = doSecurityCheckFilter(request, response, chain);
			if (!doNextFilter) {
				return;
			}

		}

		chain.doFilter(request, response);
	}
    
    private boolean doSecurityCheckFilter(ServletRequest request,ServletResponse response,
            FilterChain chain) throws IOException, ServletException  {
    	
    	SecurityManager sm = SecurityManagerFactory.getSecurityManager();
        //ȡ����URL��Ϣ
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String uri = httpRequest.getServletPath();
        logger.debug("���ص�������URI:" + uri);
        
        boolean flag = sm.securityURICheck(uri, httpRequest);
        
        if(!flag){
            String contentType = httpRequest.getHeader("Content-Type");
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            if(contentType == null ){
            	request.getRequestDispatcher(falseToRedirectUrl).include(request,response);
            }
            else{
				httpResponse.setContentType("text/json; charset=UTF-8");
				PrintWriter out = httpResponse.getWriter();
				out.println("{\"data\":[],\"timeout\":true}");
				out.close();
            }
        }
 
    	return flag;
    }
    
    
    public void destroy() {
        this.filterConfig = null;
        this.falseToRedirectUrl = null;
    }
}