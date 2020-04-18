package com.qkwl.admin.layui.controller;

import org.apache.commons.lang3.StringUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.admin.FCsQuestion;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminCsQuestionService;
import com.qkwl.admin.layui.base.WebBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * 工单管理
 * Created by wangchen on 2017-04-08.
 */
@Controller
public class CsQuestionController extends WebBaseController {
    @Autowired
    private IAdminCsQuestionService adminCsQuestionService;

    @RequestMapping(value = "/csQuestion/csQuestionList")
    public ModelAndView csQuestionList(
            @RequestParam(value = "pageNum" ,defaultValue = "1") int currentPage,
            @RequestParam(value = "orderField" , defaultValue = "fcreatetime" ) String orderField,
            @RequestParam(value = "orderDirection" , defaultValue = "desc" ) String orderDirection,
            @RequestParam(value = "fquestion" , required = false ) String fquestion,
            @RequestParam(value = "foperation" , required = false ) String foperation,
            @RequestParam(value = "fstatus" , required = false ,defaultValue = "-1") Integer fstatus,
            @RequestParam(value = "fdetail" , required = false ) String fdetail
    ){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("csQuestion/csQuestionList");

        Pagination<FCsQuestion> page = new Pagination<FCsQuestion>(currentPage, Constant.adminPageSize);
        //排序字段
        if(!StringUtils.isEmpty(orderField)){
            page.setOrderField(orderField);
        }
        //正序倒序
        if(!StringUtils.isEmpty(orderDirection)){
            page.setOrderDirection(orderDirection);
        }

        FCsQuestion cs = new FCsQuestion();
        if(!StringUtils.isEmpty(fquestion)){
            cs.setFquestion(fquestion);
        }

        if(!StringUtils.isEmpty(foperation)){
            cs.setFoperation(foperation);
        }

        if(!StringUtils.isEmpty(fdetail)){
            cs.setFdetail(fdetail);
        }

        if(fstatus != -1){
            cs.setFstatus(fstatus);
        }


        page = adminCsQuestionService.selectQuestionByPage(page, cs);

        modelAndView.addObject("fquestion", fquestion);
        modelAndView.addObject("foperation", foperation);
        modelAndView.addObject("fdetail", fdetail);
        modelAndView.addObject("fstatus", fstatus);

        modelAndView.addObject("csQuestionList", page);
        return modelAndView;
    }

    /**
     * 页面加载
     */
    @RequestMapping("/csQuestion/goCsQuestionJSP")
    public ModelAndView goCsQuestionJSP(
            @RequestParam(value = "url" , required = true) String url,
            @RequestParam(value = "fid" , required = false,defaultValue="0") Integer fid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (fid > 0) {
            FCsQuestion question = adminCsQuestionService.selectQuestionById(fid);
            modelAndView.addObject("question", question);
        }
        return modelAndView;
    }

    /**
     * 删除
     */
    @RequestMapping("/csQuestion/deleteCsQuestion")
    @ResponseBody
    public ReturnResult deleteCsQuestion(
            @RequestParam(value = "fid" , required = false) Integer fid) throws Exception {
        if(adminCsQuestionService.deleteQuestionById(fid)){
            return ReturnResult.SUCCESS("删除成功");
        } else{
            return ReturnResult.FAILUER("删除失败");
        }
    }

    /**
     * 新增
     */
    @RequestMapping("/csQuestion/saveCsQuestion")
    @ResponseBody
    public ReturnResult saveCsQuestion(
            @RequestParam(value = "fuid" , required = true) Integer fuid,
            @RequestParam(value = "fquestion" , required = true) String fquestion,
//			@RequestParam(value = "foperation" , required = true) String foperation,
            @RequestParam(value = "fdetail" , required = true) String fdetail){

        FCsQuestion question = new FCsQuestion();
        question.setFuid(fuid);
        question.setFquestion(fquestion);
        question.setFdetail(fdetail);
        question.setFstatus(0);
        question.setVersion(0);
        question.setFcreatetime(new Date());
        question.setFupdatetime(new Date());

        if(adminCsQuestionService.insertQuestion(question)){
            return ReturnResult.SUCCESS("新增成功");
        } else{
            return ReturnResult.FAILUER("新增失败");
        }
    }

    /**
     * 更新
     */
    @RequestMapping("/csQuestion/updateCsQuestion")
    @ResponseBody
    public ReturnResult updateCsQuestion(
            @RequestParam(value = "fid" , required = true) Integer fid,
            @RequestParam(value = "fquestion" , required = true) String fquestion,
            @RequestParam(value = "foperation" , required = true) String foperation,
            @RequestParam(value = "fdetail" , required = true) String fdetail,
            @RequestParam(value = "fstatus" , required = true) Integer fstatus,
            @RequestParam(value = "fresult" , required = true) String fresult,
            @RequestParam(value = "fuid" , required = true) Integer fuid){

        FCsQuestion csquestion = adminCsQuestionService.selectQuestionById(fid);
        if(csquestion==null){
            return ReturnResult.FAILUER("更新失败");
        }

        csquestion.setFoperation(foperation);
        csquestion.setFquestion(fquestion);
        csquestion.setFdetail(fdetail);
        csquestion.setFstatus(fstatus);
        csquestion.setFresult(fresult);
        csquestion.setFuid(fuid);
        csquestion.setFupdatetime(new Date());

        boolean flag = adminCsQuestionService.updateQuestion(csquestion);
        if(flag){
           return ReturnResult.SUCCESS("更新成功");
        } else{
            return ReturnResult.FAILUER("更新失败");
        }
    }
}
