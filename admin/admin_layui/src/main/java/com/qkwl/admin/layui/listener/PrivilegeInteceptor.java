package com.qkwl.admin.layui.listener;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 系统拦截器
 * 
 * @author ZKF
 */
public class PrivilegeInteceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		String uri = request.getRequestURI();
//		if (!"/_ehc.html".equals(uri) && !"https".equals(request.getHeader("X-Forwarded-Proto"))){
//			response.sendRedirect("https://admin.hotcoin.top"+uri);
//			return false;
//		}
		//System.out.println("url = "+uri);
		//System.out.println("scheme:"+request.getScheme());
		//System.out.println("Protocol:"+request.getProtocol());
		//System.out.println("X-Forwarded-Proto:"+request.getHeader("X-Forwarded-Proto"));
		//System.out.println("X-Forwarded-For:"+request.getHeader("X-Forwarded-Por"));
		return true;
	}

}
