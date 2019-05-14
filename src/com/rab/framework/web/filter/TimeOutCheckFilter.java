package com.rab.framework.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.Constants;

public class TimeOutCheckFilter implements Filter {
	/**
	 * ��־��¼��
	 */
	protected static final LogWritter logger = LogFactory.getLogger(TimeOutCheckFilter.class);
	
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
     * ��ʱ��ת����
     */
    private String timeoutRedirectUrl = null;
    
    /**
     * �Ǽ������URI����
     */
    private List<String> noCheckURIs = null;
    
    
    public void init(FilterConfig filterConfig) throws ServletException {
    	logger.debug("��ʼ�� TimeOutCheckFilter...");
        this.filterConfig = filterConfig;
        
        //ȡ��ʱ��ת����
        this.timeoutRedirectUrl = filterConfig.getInitParameter("timeout-redirect-url");
        logger.debug("timeoutRedirectUrl = " + timeoutRedirectUrl);
        
        //ȡ������������״̬
        String value = filterConfig.getInitParameter("state");
        if (value == null || value.equalsIgnoreCase("on")){
            this.state = true;
        } else {
            this.state = false;
        }
        logger.debug("state = " + state);
        
        //ȡ�Ǽ�������嵥
        String strNoCheckURIs = filterConfig.getInitParameter("no-check-uri");
        String delim = ",";
        this.noCheckURIs = strToArrayList(strNoCheckURIs,delim);
        logger.debug("noCheckURIs = " + strNoCheckURIs);
        
        logger.debug("��ʼ�� TimeOutCheckFilter ����");

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
		
		if (state) {
			boolean doNextFilter = doTimeoutFilter(request, response, chain);
			if (!doNextFilter) {
				return;
			}
		}

		chain.doFilter(request, response);
	}
    
    private boolean doTimeoutFilter(ServletRequest request,ServletResponse response,
                                   FilterChain chain) throws IOException, ServletException {

        boolean doNextFilter = true;
         
        //ȡ����URL��Ϣ
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String requestUrl = httpRequest.getServletPath();
        logger.debug("���ص�������URI:" + requestUrl);
        if(this.checkNOCheckURI(requestUrl)){
        	return doNextFilter;
        }

        //�жϻỰ�Ƿ�ʱ
        HttpSession session = httpRequest.getSession();
        boolean timeout = false;
        //1. ���session����Ϊ�գ����жϳ�ʱ
        //2. ���session����Ϊ�գ�ͨ���жϲ�����Ա��Ȩ�޴���������Ự�Ƿ�ʱ
        if(session == null 
        		|| session.getAttribute(Constants.SESSION_FLAG) == null) {
        	timeout = true;
        }
        logger.debug("sessionid = " + session.getId());
        logger.debug("timeout = " + timeout);
        logger.debug("timeoutRedirectUrl = " + timeoutRedirectUrl);
        
        if(timeout){
        	doNextFilter = false;
            String contentType = httpRequest.getHeader("Content-Type");
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            logger.debug("contentType = " + contentType);
            if(contentType == null ){
            	request.getRequestDispatcher(timeoutRedirectUrl).include(request,response);
            }
            else{
				httpResponse.setContentType("text/json; charset=UTF-8");
				PrintWriter out = httpResponse.getWriter();
				out.println("{\"data\":[],\"timeout\":true}");
				out.close();
            }
            
//          request.getRequestDispatcher(timeoutRedirectUrl).include(request,response);

        }
        
        return doNextFilter;
    }
    
    /**
     * �ж�����url�Ƿ���һ���������URI
     * @param requestUrl ����url
     * @return �Ƿ񱻽�ֹ���
     */
    private boolean checkNOCheckURI(String requestUrl) {
        boolean flag = false;
        requestUrl = requestUrl.trim();

        //�������urlΪ����ֱ�ӷ���true
        if( (requestUrl==null) || (requestUrl.equals("")) ){
        	flag = true;
            return flag;
        }
        
        if( (this.noCheckURIs==null)||(this.noCheckURIs.size()==0) ) {
        	flag = false;
        } else {
            int size = this.noCheckURIs.size();
            for(int i=0; i<size; i++) {
                String uri = noCheckURIs.get(i);
                if(requestUrl.startsWith(uri)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private List<String> strToArrayList(String forbidedUrlPattern,String delim) {
        StringTokenizer tokenizer = new StringTokenizer(forbidedUrlPattern,delim);
        List<String> list = new ArrayList<String>();
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            list.add(token);
        }
        return list;
    }
     
    
    public void destroy() {
        this.filterConfig = null;
        this.timeoutRedirectUrl = null;
    }
}