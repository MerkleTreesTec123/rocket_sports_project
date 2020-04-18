package com.qkwl.web.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.common.PageHelper;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.news.FArticleType;
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
public class FrontServiceJsonController extends JsonBaseController {

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 通知列表
     */
    @ResponseBody
    @RequestMapping("/notice/index_json")
    public ReturnResult ourService(
            @RequestParam(required = false, defaultValue = "2") Integer id,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {// 12,5,5
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FSystemLan systemLan = redisHelper.getLanguageType(LuangeHelper.getLan(request));
        if (systemLan == null) {
            return ReturnResult.FAILUER("");
        }

        FArticleType farticletype = redisHelper.getArticleType(id, systemLan.getFid());
        if (farticletype == null) {

            return ReturnResult.FAILUER("");
        }
        List<FArticle> farticles = redisHelper.getArticles(2, farticletype.getFid(), currentPage,
                WebConstant.BCAgentId);

        int total = redisHelper.getArticlesPageCont(2, farticletype.getFid(), WebConstant.BCAgentId);
        String pagin = PageHelper.generatePagin(total / 10 + (total % 10 == 0 ? 0 : 1), currentPage,
                "/notice/index.html?id=" + id + "&");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pagin", pagin);
        jsonObject.put("farticles", farticles);
        jsonObject.put("id", id);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 通知详情
     */
    @ResponseBody
    @RequestMapping("/notice/detail_json")
    public ReturnResult article(int id) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        FArticle farticle = redisHelper.getArticleById(2, id, WebConstant.BCAgentId);
        if (farticle == null) {
            return ReturnResult.FAILUER("");
			/*modelAndView.setViewName("redirect:/notice/index.html");
			return modelAndView;*/
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("farticle", farticle);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * app页面
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/service/appnews_json")
    public ReturnResult AppNews(
            @RequestParam(required = false, defaultValue = "2") Integer id,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
        int pagesize = 10;
        FArticleType farticletype = redisHelper.getArticleType(id, 1);
        List<FArticle> farticles = redisHelper.getArticles(1, farticletype.getFid(), currentPage, WebConstant.BCAgentId);
        int total = redisHelper.getArticlesPageCont(1, farticletype.getFid(), WebConstant.BCAgentId);
        int totalpage = total / pagesize + (total % pagesize == 0 ? 0 : 1);
        int nextpage = 0;
        if (currentPage <= totalpage - 1) {
            nextpage = currentPage + 1;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("farticles", farticles);
        jsonObject.put("nextpage", nextpage);
        jsonObject.put("id", id);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * app页面详情
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/service/appnew_json")
    public ReturnResult AppNew(int id) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        FArticle farticle = redisHelper.getArticleById(1, id, WebConstant.BCAgentId);
        if (farticle == null) {
            return ReturnResult.FAILUER("");
			/*modelAndView.setViewName("redirect:/service/appnews.html");
			return modelAndView;*/
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("farticle", farticle);
        return ReturnResult.SUCCESS(jsonObject);
    }

}
