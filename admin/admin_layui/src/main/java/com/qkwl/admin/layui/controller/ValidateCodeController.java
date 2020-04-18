package com.qkwl.admin.layui.controller;

import com.qkwl.common.util.GeetestLib;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.GeetestConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ZKF on 2017/3/31.
 */
@Controller
public class ValidateCodeController extends WebBaseController {


    @RequestMapping("/admin/startCaptcha")
    @ResponseBody
    public ReturnResult startCaptcha(){
        GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(),
                GeetestConfig.isnewfailback());

        String resStr = "{}";

        //自定义userid
        String userid = "test";

        //进行验证预处理
        int gtServerStatus = gtSdk.preProcess(userid);

        //将服务器状态设置到session中
        sessionContextUtils.getContextRequest().getSession().setAttribute(gtSdk.gtServerStatusSessionKey, gtServerStatus);
        //将userid设置到session中
        sessionContextUtils.getContextRequest().getSession().setAttribute("userid", userid);

        resStr = gtSdk.getResponseStr();

        return ReturnResult.SUCCESS(resStr);
    }

}
