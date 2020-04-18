package com.qkwl.admin.layui.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.qkwl.common.controller.BaseController;
import com.qkwl.common.util.Utils;
import com.qkwl.common.framework.redis.RedisHelper;


public class WebBaseController extends BaseController {

	@Autowired
	private RedisHelper redisHelper;
	
	@ModelAttribute
	public void addConstant() {
	}
	
	public void setRedisData(String token, Object restInfo) {
		String redisKey = Utils.UUID();
		sessionContextUtils.addContextToken(token, redisKey);
		redisHelper.setRedisData(redisKey, restInfo);
	}

	public void deletRedisData(String token) {
		String redisKey = sessionContextUtils.getContextToken(token);
		redisHelper.deletRedisData(redisKey);
	}

	public String getRedisData(String token) {
		String redisKey = sessionContextUtils.getContextToken(token);
		return redisHelper.getRedisData(redisKey);
	}
}
