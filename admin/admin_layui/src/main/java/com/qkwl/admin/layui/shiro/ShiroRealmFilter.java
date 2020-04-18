package com.qkwl.admin.layui.shiro;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.admin.FAdmin;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by LY on 2017/4/13.
 */
@Component("shiroRealmFilter")
public class ShiroRealmFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        FAdmin fadmin = (FAdmin)(httpRequest.getSession().getAttribute("login_admin"));
        String requestType = httpRequest.getHeader("X-Requested-With");
        if(fadmin == null){
            if("XMLHttpRequest".equals(requestType)){
                ReturnResult rst = ReturnResult.FAILUER(401,"登录已失效，请重新登录!");
                httpResponse.setContentType("application/json; charset=utf-8");
                httpResponse.setCharacterEncoding("utf-8");
                httpResponse.getWriter().write(JSON.toJSONString(rst));
            }else{
                WebUtils.issueRedirect(request, response, "/error.html");
            }
            return false;
        }
        return true;
    }
}
