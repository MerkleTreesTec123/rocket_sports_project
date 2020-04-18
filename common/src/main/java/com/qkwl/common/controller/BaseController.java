package com.qkwl.common.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.qkwl.common.auth.SessionContextUtils;

public abstract class BaseController {
	
	@Autowired
	protected SessionContextUtils sessionContextUtils;
	
	
	/**
	 * 表单数据绑定
	 * @Title: initBinder 
	 * @Description: 在SpringMVC中，bean中定义了Date，double等类型，
	 * 如果没有做任何处理的话，日期以及double都无法绑定，解决的办法就是使用spring mvc提供的@InitBinder标签
	 * spring自己提供了大量的实现类，诸如CustomDateEditor ，CustomBooleanEditor，CustomNumberEditor等许多，
	 * 基本上够用
	 * @param binder
	 * @throws ServletException
	 * @return void    
	 * @throws
	 */
	@InitBinder
    protected void initBinder(WebDataBinder binder) throws ServletException {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());  
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }
}
