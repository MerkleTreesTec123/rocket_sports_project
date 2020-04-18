package com.qkwl.admin.layui.listener;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@WebFilter(filterName = "urlFilter", urlPatterns = "/*")
public class UrlFilter implements Filter {
	protected static final Log log = LogFactory.getLog(UrlFilter.class);
	
	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String proto = request.getHeader("X-Forwarded-Proto");
		if (!"https".equals(proto) && "/_ehc.html".equals(request.getRequestURI())){

			return;
		}

		String uri = request.getRequestURI().toLowerCase().trim();
		Cookie[] cookies = request.getCookies();
		boolean cookieFlag = false;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("open")) {
					cookieFlag = true;
					break;
				}
			}
		}
		
		if (!cookieFlag) {
			response.addCookie(new Cookie("open", "true"));
		}

		// 不接受任何jsp请求
		if (uri.endsWith(".jsp")) {
			return;
		}

		// 只拦截.html结尾的请求
		if (!uri.endsWith(".html")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		request.setCharacterEncoding("UTF-8");
		
		filterChain.doFilter(request, response);
	}

	public void init(FilterConfig arg0) throws ServletException {

	}
}
