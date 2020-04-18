package com.qkwl.admin.layui.controller;

import com.qkwl.common.google.GoogleAuth;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.rpc.admin.IAdminManageService;
import com.qkwl.admin.layui.base.WebBaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GoogleAuthentication extends WebBaseController {

	private static final Logger logger = LoggerFactory.getLogger(GoogleAuthentication.class);
	
	@Autowired
	private IAdminManageService adminService;

	@RequestMapping("admin/googleAuthenticationBind")
	public ModelAndView GoogleAuthenticationBind(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("comm/googleAuth");
		FAdmin fadmin = adminService.selectAdminById(((FAdmin)(request.getSession().getAttribute("login_admin"))).getFid());
		if(fadmin.getFgooglebind()== null || !fadmin.getFgooglebind()){
			Map<String, String> map = GoogleAuth.genSecret(fadmin.getFname());
			String totpKey = map.get("secret");
			String qecode = map.get("url");
			fadmin.setFgoogleauthenticator(totpKey);
			fadmin.setFgoogleurl(qecode);
			this.adminService.updateAdmin(fadmin);
			modelAndView.addObject("qecode", qecode);
			modelAndView.addObject("totpKey", totpKey);
		}else{
			modelAndView.addObject("qecode", fadmin.getFgoogleauthenticator());
			modelAndView.addObject("totpKey", fadmin.getFgoogleurl());
		}
		String device_name = Constant.GoogleAuthName + ":" + fadmin.getFname();
		modelAndView.addObject("GoogleBind", fadmin.getFgooglebind());
		modelAndView.addObject("device_name", device_name);
		return modelAndView;
	}

	/*
	 * 添加设备认证
	 */
	@ResponseBody
	@RequestMapping("/admin/validateAuthenticator")
	public ReturnResult validateAuthenticator(
			HttpServletRequest request, 
			@RequestParam(required = false) String code,
			@RequestParam(required = false) String totpKey) throws Exception {
		FAdmin fadmin = adminService.selectAdminById(((FAdmin) (request.getSession().getAttribute("login_admin"))).getFid());
		boolean b_status = fadmin.getFgooglebind() == false && totpKey.equals(fadmin.getFgoogleauthenticator()) && !totpKey.trim().equals("");
		if (!b_status) {
			return ReturnResult.FAILUER("您已绑定谷歌验证器");
		}
		boolean auth = GoogleAuth.auth(Long.parseLong(code), fadmin.getFgoogleauthenticator());
		if (auth) {
			fadmin.setFgooglebind(true);
			fadmin.setFgooglevalidate(true);
			fadmin.setFopengooglevalidate(true);
			adminService.updateAdmin(fadmin);
			request.getSession().setAttribute("login_admin", fadmin);
			return ReturnResult.SUCCESS("谷歌验证器绑定成功");
		} else {
			return ReturnResult.FAILUER("谷歌验证码错误");
		}
	}
}
