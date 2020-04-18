package com.qkwl.admin.layui.controller;

import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.Enum.QuestionIsAnswerEnum;
import com.qkwl.common.dto.Enum.QuestionStatusEnum;
import com.qkwl.common.dto.Enum.QuestionTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FQuestion;
import com.qkwl.common.rpc.admin.IAdminQuestionService;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.MQSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 问答管理
 * Created by wangchen on 2017-04-07.
 */
@Controller
public class QuestionController extends WebBaseController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private IAdminQuestionService adminQuestionService;
    @Autowired
    private MQSend mqSend;


    /**
     * 待回答问题列表
     */
    @RequestMapping(value = "question/questionForAnswerList")
    public ModelAndView articleList(
            @RequestParam(value = "pageNum" ,defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField" , defaultValue = "fcreatetime" ) String orderField,
            @RequestParam(value = "orderDirection" , defaultValue = "desc" ) String orderDirection,
            @RequestParam(value = "keywords" , required = false ) String keywords){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("question/questionForAnswerList");

        Pagination<FQuestion> page = new Pagination<FQuestion>(currentPage, Constant.adminPageSize);
        //查询关键字
        if(!StringUtils.isEmpty(keywords)){
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }
        page.setOrderField(orderField);
        page.setOrderDirection(orderDirection);

        FQuestion question = new FQuestion();
        question.setFstatus(QuestionStatusEnum.NOT_SOLVED.getCode());

        page = adminQuestionService.selectQuestionPageList(page, question);

        modelAndView.addObject("questionList", page);

        return modelAndView;
    }

    /**
     * 已回答问题列表
     */
    @RequestMapping(value = "question/questionList")
    public ModelAndView questionList(
            @RequestParam(value = "pageNum" ,defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField" , defaultValue = "fcreatetime" ) String orderField,
            @RequestParam(value = "orderDirection" , defaultValue = "desc" ) String orderDirection,
            @RequestParam(value = "keywords" , required = false ) String keywords){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("question/questionList");

        Pagination<FQuestion> page = new Pagination<FQuestion>(currentPage, Constant.adminPageSize);
        //排序字段
        if(!StringUtils.isEmpty(orderField)){
            page.setOrderField(orderField);
        }
        //正序倒序
        if(!StringUtils.isEmpty(orderDirection)){
            page.setOrderDirection(orderDirection);
        }
        //查询关键字
        if(!StringUtils.isEmpty(keywords)){
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }

        FQuestion question = new FQuestion();
        question.setFstatus(QuestionStatusEnum.SOLVED.getCode());

        page = adminQuestionService.selectQuestionPageList(page, question);
        modelAndView.addObject("questionList", page);

        return modelAndView;
    }

    @RequestMapping("question/goQuestionJSP")
    public ModelAndView goQuestionJSP(
            @RequestParam(value = "url" , required = true) String url,
            @RequestParam(value = "fid" , required = false) Integer fid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();

        if (fid != null && fid > 0) {
            FQuestion fquestion = adminQuestionService.selectQuestionById(fid);
            modelAndView.addObject("fquestion", fquestion);
        }

        JSONArray jsonArray = new JSONArray();
        for (QuestionTypeEnum entrustsource : QuestionTypeEnum.values()) {
            JSONObject item = new JSONObject();
            item.put("value", entrustsource.getValue());
            item.put("fid", entrustsource.getCode());
            jsonArray.add(item);
        }
        modelAndView.addObject("fquestionTypes", jsonArray);
        modelAndView.setViewName(url);
        return modelAndView;
    }


    @RequestMapping("question/answerQuestion")
    @ResponseBody
    public ReturnResult answerQuestion(
            @RequestParam(value = "fid" , required = true) int fid,
            @RequestParam(value = "fanswer" , required = true) String fanswer) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        try{
            FAdmin admin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
            if(admin == null){
                return ReturnResult.FAILUER("登录失效，请重新登录！");
            }

            FQuestion fquestion = adminQuestionService.selectQuestionById(fid);
            fquestion.setFanswer(fanswer);
            fquestion.setFaid(admin.getFid());
            fquestion.setFstatus(QuestionStatusEnum.SOLVED.getCode());
            fquestion.setFisanswer(QuestionIsAnswerEnum.NOT);
            fquestion.setFupdatetime(new Date());

            if(adminQuestionService.updateAnswerQuestion(fquestion)){
                String ip = Utils.getIpAddr(request);
                FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
                mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ANSWER_QUESTION, ip);
            }
            return ReturnResult.SUCCESS("更新成功");
        }catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("网络超时");
        }
    }
}


