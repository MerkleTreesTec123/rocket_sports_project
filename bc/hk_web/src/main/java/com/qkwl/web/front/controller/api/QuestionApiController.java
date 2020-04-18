package com.qkwl.web.front.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.user.*;
import com.qkwl.common.rpc.user.IQuestionService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.*;
import com.qkwl.web.front.controller.base.JsonBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

/**
 * 用户相关的接口，包括：登录、注册、绑定验证、用户资产
 */
@Controller
public class QuestionApiController extends JsonBaseController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IQuestionService questionService;

    /**
     * 提交问题
     */
    @ResponseBody
    @RequestMapping(value = "/v1/question/submit")
    public ReturnResult submitQuestion(
            @RequestParam int questiontype,
            @RequestParam String questiondesc) throws Exception {
        String fquestionType = QuestionTypeEnum.getValueByCode(questiontype);
        questiondesc = HtmlUtils.htmlEscape(questiondesc);
        if (questiondesc.trim().equals("") && fquestionType == null && questiondesc.trim().length() < 10) {
            return ReturnResult.FAILUER(GetR18nMsg("com.activity.error.10006"));
        }
        try {
            String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
            FUser fuser = getCurrentUserInfoByApiToken();
            fuser = userService.selectUserById(fuser.getFid());
            FQuestion question = new FQuestion();
            question.setFtype(questiontype);
            question.setFdesc(questiondesc);
            question.setFuid(fuser.getFid());
            question.setFstatus(QuestionStatusEnum.NOT_SOLVED.getCode());
            question.setFcreatetime(Utils.getTimestamp());
            question.setFisanswer(QuestionIsAnswerEnum.NOT);
            question.setVersion(0);
            question.setFname(fuser.getFrealname());
            question.setFtelephone(fuser.getFtelephone());
            questionService.insertQuestion(question, 0, ip);
        } catch (Exception e) {
            return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
        }
        return ReturnResult.SUCCESS(GetR18nMsg("common.succeed.200"));
    }

    @ResponseBody
    @RequestMapping("/v1/question/type")
    public ReturnResult question() throws Exception {
        QuestionTypeEnum[] values = QuestionTypeEnum.values();
        JSONArray jsonArray = new JSONArray();
        for (QuestionTypeEnum value :
                values) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",value.getCode());
            jsonObject.put("value",value.getValue());
            jsonArray.add(jsonObject);
        }
        return ReturnResult.SUCCESS(jsonArray);
    }





}
