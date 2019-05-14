package com.rab.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

public class CharacterEncodingFilter implements Filter {
	protected static final LogWritter logger = LogFactory.getLogger(CharacterEncodingFilter.class);

	protected String encoding = null;
	protected FilterConfig filterConfig = null;
	protected boolean state = true;

	public void destroy() {
		this.encoding = null;
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.debug("��ʼ����SetCharacterEncodingFilter...");

		if (!state || (request.getCharacterEncoding() == null)) {
			String encoding = selectEncoding(request);
			if (encoding != null)
				request.setCharacterEncoding(encoding);
		}
		logger.debug("���ú��request�ַ�������Ϊ:"+ request.getCharacterEncoding());

		logger.debug("�˳�SetCharacterEncodingFilter.");

		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.encoding = filterConfig.getInitParameter("encoding");
		String value = filterConfig.getInitParameter("ignore");
		if (value == null || value.equalsIgnoreCase("true")) {
			this.state = true;
		} 
		else {
			this.state = false;
		}
	}

	protected String selectEncoding(ServletRequest request) {
		return (this.encoding);
	}
}
