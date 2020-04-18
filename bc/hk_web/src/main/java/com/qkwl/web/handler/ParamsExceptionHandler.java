package com.qkwl.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ParamsExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ParamsExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String[]> entry : httpServletRequest.getParameterMap().entrySet()) {
            params.append(",").append(entry.getKey()).append(" = ").append(entry.getValue().length > 0 ? entry.getValue()[0] : "");
        }
        logger.error("error:{}, url:{}, params:[{}]", ex.getMessage(), httpServletRequest.getRequestURI(), params.toString());
        return new ResponseEntity<>(status);
    }
}
