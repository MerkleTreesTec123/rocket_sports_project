package com.qkwl.web.front.controller.ValidateImage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qkwl.web.front.controller.base.RedisBaseControll;

@Controller
public class ValidateImageController extends RedisBaseControll {

    @RequestMapping("/servlet/ValidateImageServlet")
    public void getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        super.deletRedisData("CHECKCODE");
        super.setRedisData("CHECKCODE", verifyCode);
        // 生成图片
        int w = 100, h = 39;
        VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
        response.getOutputStream().flush();
    }


}
