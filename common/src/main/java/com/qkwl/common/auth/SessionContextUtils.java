package com.qkwl.common.auth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qkwl.common.util.RequestMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.SortedMap;

public class SessionContextUtils { 
	
	@Autowired 
	private HttpServletRequest request;
	@Autowired 
	private HttpServletResponse response;

	/**
	 * 获取当前上下文请求
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getContextRequest() { 
		return request;
	}
	
	/**
	 * 获取当前上下文请求
	 * @return HttpServletRequest
	 */
	public String getContextPath() { 
		return request.getContextPath();
	}
	
	/**
	 * 获取当前上下文请求
	 * @return HttpServletRequest
	 */
	public String getContextBasePath() { 
		return getContextRequest().getScheme() + "://" + getContextRequest().getServerName() + ":" + getContextRequest().getServerPort() + getContextPath() + "/";
	}
	
	/**
	 * 获取当前上下文请求
	 * @return HttpServletRequest
	 */
	public HttpServletResponse getContextResponse() { 
		return response;
	}
	
	/**
	 * 获取当前上下文SessionId
	 * @return
	 */
	public String getContextSessionId() { 
		return this.request.getSession().getId();
	}
	
	/**
	 * 获取当前Token
	 * @param tokenName
	 * @return
	 */
	public String getContextToken(String tokenName){
		Cookie[] cookies = request.getCookies();
		if(cookies == null){
			return null;
		}
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(tokenName)) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	/**
	 * 获取当前Token
	 * @return
	 */
	public String getContextApiToken(){
		SortedMap<String, String> map = RequestMapUtils.getMap(request);
		String api_key = map.get("token");
		if(StringUtils.isEmpty(api_key)){
			return null;
		}
		return api_key;
	}
	
	/**
	 * 设置当前Token
	 * @param tokenName
	 * @param token
	 */
	public void addContextToken(String tokenName,String token){
		this.response.addHeader("Set-Cookie", String.format("%s=%s;Path=/; HttpOnly", tokenName,token));
	}
}
