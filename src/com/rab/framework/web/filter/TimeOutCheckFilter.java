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
	 * 日志记录器
	 */
	protected static final LogWritter logger = LogFactory.getLogger(TimeOutCheckFilter.class);
	
	/**
	 * 协议类型
	 */
    private static final String HTTP_PROTOCOL1_1 = "HTTP/1.1";
    private static final String HTTP_PROTOCOL1_0 = "HTTP/1.0";  //IE8 
	
	/**
	 * 过滤器运行环境配置
	 */
	protected FilterConfig filterConfig = null;
	
	/**
	 * 过滤器启用状态
	 */
    protected boolean state = true;
    
    /**
     * 超时跳转链接
     */
    private String timeoutRedirectUrl = null;
    
    /**
     * 非检查链接URI集合
     */
    private List<String> noCheckURIs = null;
    
    
    public void init(FilterConfig filterConfig) throws ServletException {
    	logger.debug("初始化 TimeOutCheckFilter...");
        this.filterConfig = filterConfig;
        
        //取超时跳转链接
        this.timeoutRedirectUrl = filterConfig.getInitParameter("timeout-redirect-url");
        logger.debug("timeoutRedirectUrl = " + timeoutRedirectUrl);
        
        //取过滤器的启用状态
        String value = filterConfig.getInitParameter("state");
        if (value == null || value.equalsIgnoreCase("on")){
            this.state = true;
        } else {
            this.state = false;
        }
        logger.debug("state = " + state);
        
        //取非检查链接清单
        String strNoCheckURIs = filterConfig.getInitParameter("no-check-uri");
        String delim = ",";
        this.noCheckURIs = strToArrayList(strNoCheckURIs,delim);
        logger.debug("noCheckURIs = " + strNoCheckURIs);
        
        logger.debug("初始化 TimeOutCheckFilter 结束");

    }

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// 判断访问类型，只对HTTP协议请求做处理
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
         
        //取请求URL信息
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String requestUrl = httpRequest.getServletPath();
        logger.debug("拦截到的请求URI:" + requestUrl);
        if(this.checkNOCheckURI(requestUrl)){
        	return doNextFilter;
        }

        //判断会话是否超时
        HttpSession session = httpRequest.getSession();
        boolean timeout = false;
        //1. 如果session对象为空，则判断超时
        //2. 如果session对象不为空，通过判断操作人员的权限存根来决定会话是否超时
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
     * 判断请求url是否是一个不予检查的URI
     * @param requestUrl 请求url
     * @return 是否被禁止标记
     */
    private boolean checkNOCheckURI(String requestUrl) {
        boolean flag = false;
        requestUrl = requestUrl.trim();

        //如果请求url为空则直接返回true
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