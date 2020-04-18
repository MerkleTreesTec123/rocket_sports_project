package com.qkwl.web.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.util.Constant;
import com.qkwl.web.front.controller.base.JsonBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.QuestionIsAnswerEnum;
import com.qkwl.common.dto.Enum.QuestionStatusEnum;
import com.qkwl.common.dto.Enum.QuestionTypeEnum;
import com.qkwl.common.dto.user.FQuestion;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.rpc.user.IQuestionService;
import com.qkwl.common.rpc.user.IUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class FrontQuestionJsonController extends JsonBaseController {

    @Autowired
    private IQuestionService questionService;

    @Autowired
    private IUserService userService;

    /**
     * 提交问题
     */
    @ResponseBody
    @RequestMapping(value = "/online_help/help_submit")
    public ReturnResult submitQuestion(
            @RequestParam(required = true) int questiontype,
            @RequestParam(required = true) String questiondesc) throws Exception {
        String fquestionType = QuestionTypeEnum.getValueByCode(questiontype);
        questiondesc = HtmlUtils.htmlEscape(questiondesc);
        if (questiondesc.trim().equals("") && fquestionType == null && questiondesc.trim().length() < 10) {
            return ReturnResult.FAILUER(GetR18nMsg("com.activity.error.10006"));
        }
        try {
            String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
            FUser fuser = getCurrentUserInfoByToken();
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

    /**
     * 删除提问
     */
    @ResponseBody
    @RequestMapping(value = "/online_help/help_delete")
    public ReturnResult delQuestion(@RequestParam(required = false, defaultValue = "0") Integer fid) throws Exception {
        FUser fuser = getCurrentUserInfoByToken();
        fuser = userService.selectUserById(fuser.getFid());
        if (fid == 0 || fuser == null || fuser.getFid() <= 0) {
            return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10001"));
        }
        if (!questionService.deleteQuestionById(fid, true, fuser.getFid())) {
            return ReturnResult.FAILUER(GetR18nMsg("com.activity.error.10007"));
        }
        return ReturnResult.SUCCESS();
    }

    @ResponseBody
    @RequestMapping("/online_help/index_json")
    public ReturnResult question(HttpServletRequest request) throws Exception {
        Map<Integer, Object> fquestiontypes = new LinkedHashMap<Integer, Object>();
        for (QuestionTypeEnum questionTypeEnum : QuestionTypeEnum.values()) {
            fquestiontypes.put(questionTypeEnum.getCode(), GetR18nMsg("question.type.enum" + questionTypeEnum.getCode()));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fquestiontypes", fquestiontypes);
        return ReturnResult.SUCCESS(jsonObject);
    }

    @ResponseBody
    @RequestMapping("/online_help/help_list_json")
    public ReturnResult questionColumn(
            @RequestParam(required = false, defaultValue = "1") Integer currentPage
    ) throws Exception {
        FUser fuser = getCurrentUserInfoByToken();
        JSONObject jsonObject = new JSONObject();
        if (fuser != null) {
            Pagination<FQuestion> fquestion = new Pagination<FQuestion>(currentPage, Constant.QuestionRecordPerPage, "/online_help/help_list.html?");
            FQuestion operation = new FQuestion();
            operation.setFuid(fuser.getFid());
            fquestion = questionService.selectPageQuestionList(fquestion, operation);
            jsonObject.put("currentPage", currentPage);
            jsonObject.put("list", fquestion);
        }
        return ReturnResult.SUCCESS(jsonObject);
    }


}
