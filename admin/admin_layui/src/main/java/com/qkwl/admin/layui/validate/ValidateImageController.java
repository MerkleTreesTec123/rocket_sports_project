package com.qkwl.admin.layui.validate;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.admin.layui.base.WebBaseController;


@Controller
public class ValidateImageController extends WebBaseController {

	@RequestMapping("/servlet/ValidateImageServlet")
	public ModelAndView getKaptchaImage() throws Exception {
		HttpServletResponse response = sessionContextUtils.getContextResponse();
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("image/jpeg");
		String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
		super.deletRedisData("CHECKCODE");
		super.setRedisData("CHECKCODE",verifyCode);
		// 生成图片
		int w = 100, h = 41;
		VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
		return null;
	}

}
