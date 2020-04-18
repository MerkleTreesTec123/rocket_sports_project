package com.qkwl.web.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.web.FAbout;
import com.qkwl.common.dto.web.FAboutType;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.i18n.LuangeHelper;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class FrontAboutController extends JsonBaseController {

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 关于我们
     */
    @ResponseBody
    @RequestMapping(value = "/about/about_json")
    public ReturnResult index(@RequestParam(required = false, defaultValue = "0") Integer id) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FSystemLan systemLan = redisHelper.getLanguageType(LuangeHelper.getLan(request));
        if (systemLan == null) {
            return ReturnResult.FAILUER("");
        }
        // 获取当前语言下所有的about种类
        List<FAboutType> fabouttypes = redisHelper.getAboutTypeList(1, WebConstant.BCAgentId);
        // 查找当前语言下制定id下的详细信息
        FAbout fabout = redisHelper.getAbout(id, 1, WebConstant.BCAgentId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fabout", fabout);
        jsonObject.put("fabouttypes", fabouttypes);
        return ReturnResult.SUCCESS(jsonObject);
    }


}
