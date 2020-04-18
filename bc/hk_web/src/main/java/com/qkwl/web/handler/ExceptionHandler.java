package com.qkwl.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.qkwl.common.util.ReturnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.common.framework.redis.RedisHelper;

import java.util.HashMap;
import java.util.Map;


@Component("exceptionResolver")
public class ExceptionHandler implements HandlerExceptionResolver {

    @Autowired
    private RedisHelper redisHelper;
                    
    /**
     * 异常错误页
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ex.printStackTrace();
        ModelAndView mv = new ModelAndView();
        FastJsonJsonView view = new FastJsonJsonView();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("code", ReturnResult.FAILURE);
        attributes.put("msg", "server error");
        view.setAttributesMap(attributes);
        mv.setView(view);
        return mv;
    }

}
