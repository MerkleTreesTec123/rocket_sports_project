package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.dto.Enum.IdentityStatusEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.user.FIdentityInfo;
import com.qkwl.common.dto.user.FUserIdentity;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.rpc.admin.IAdminIdentityService;
import com.qkwl.common.rpc.admin.IAdminUserIdentityService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.common.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ZKF on 2017/4/20.
 */
@Controller
public class IdentityController extends WebBaseController {

    @Autowired
    private IAdminUserIdentityService adminUserIdentityService;
    @Autowired
    private IAdminIdentityService adminIdentityService;
    @Autowired
    private ScoreHelper scoreHelper;

    @RequestMapping("admin/userIdentityList")
    public ModelAndView identityList(
            @RequestParam(value="pageNum", required=false, defaultValue="1") Integer currentPage,
            @RequestParam(required=false) String keywords,
            @RequestParam(required=false,defaultValue="fcreatetime") String orderField,
            @RequestParam(required=false,defaultValue="desc") String orderDirection,
            @RequestParam(value="fstatus", required=false, defaultValue="0") Integer fstatus,
            @RequestParam(value="ftype", required=false, defaultValue="-1") Integer ftype) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/userIdentityList");

        Pagination<FUserIdentity> page = new Pagination<FUserIdentity>(currentPage, Constant.adminPageSize);
        page.setOrderDirection(orderDirection);
        page.setOrderField(orderField);

        if(!com.alibaba.druid.util.StringUtils.isEmpty(keywords)){
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }

        FUserIdentity identity = new FUserIdentity();

        if(fstatus != null && fstatus != -1){
            identity.setFstatus(fstatus);
            modelAndView.addObject("fstatus", fstatus);
        }

        if(ftype != null && ftype != -1){
            identity.setFtype(ftype);
            modelAndView.addObject("ftype", ftype);
        }



        page = adminUserIdentityService.selectByPage(page, identity);

        modelAndView.addObject("page", page);
        modelAndView.addObject("rel", "identityList");
        modelAndView.addObject("currentPage", currentPage);

        return modelAndView;
    }


    @RequestMapping("admin/reviewIdentity")
    @ResponseBody
    public ReturnResult reviewIdentity(
            @RequestParam(value = "uid", required = true) Integer fid,
            @RequestParam(value = "fstatus", required = true) Integer fstatus){
        FUserIdentity identity = adminUserIdentityService.selectById(fid);
        if(identity==null){
            return ReturnResult.FAILUER("操作失败，未找到该信息，请刷新重试！");
        }

        if(identity.getFstatus().equals(fstatus)){
            return ReturnResult.FAILUER("操作失败，请勿重复操作！");
        }

        identity.setFstatus(fstatus);
        identity.setFupdatetime(new Date());

        try {
            Boolean i = adminUserIdentityService.updateIdentity(identity);
            if(i){
                if(identity.getFstatus().equals(IdentityStatusEnum.PASS.getCode())){
                    scoreHelper.SendUserScore(identity.getFuid(), BigDecimal.ZERO, ScoreTypeEnum.REALNAME.getCode(), "实名认证");
                }
                return ReturnResult.SUCCESS("操作成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnResult.FAILUER("操作异常！");
    }

    /**
     * 列表
     */
    @RequestMapping("admin/identityList")
    public ModelAndView aboutList(
            @RequestParam(value = "pageNum", required=false,defaultValue="1") Integer currentPage,
            @RequestParam(value = "keywords", required=false) String keywords,
            @RequestParam(value = "fisok", required=false, defaultValue = "2") Integer fisok,
            @RequestParam(value = "orderField", required=false,defaultValue="fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", required=false,defaultValue="desc") String orderDirection
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/identityList");
        //分页条件
        Pagination<FIdentityInfo> page=new Pagination<FIdentityInfo>(currentPage, Constant.adminPageSize);
        page.setOrderDirection(orderDirection);
        page.setOrderField(orderField);
        if(!StringUtils.isEmpty(keywords)){
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }

        FIdentityInfo info = new FIdentityInfo();
        info.setFisok(fisok);
        modelAndView.addObject("fisok", fisok);

        page = adminIdentityService.selectByPage(page, info);

        modelAndView.addObject("page", page);
        return modelAndView;
    }

    @RequestMapping("admin/goIdentity")
    public ModelAndView goIdentity(
            @RequestParam(value = "fid", required = false) Integer fid,
            @RequestParam(value = "url", required = false) String url){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);

        FIdentityInfo info = adminIdentityService.selectById(fid);
        modelAndView.addObject("info", info);
        return modelAndView;
    }

    @RequestMapping("admin/updateIdentity")
    @ResponseBody
    public ReturnResult updateIdentity(
            @RequestParam(value = "fid", required = true) Integer fid,
            @RequestParam(value = "fusername", required = true) String fusername,
            @RequestParam(value = "fidentityno", required = true) String fidentityno,
            @RequestParam(value = "fisok", required = true) Integer fisok){

        FIdentityInfo info = adminIdentityService.selectById(fid);
        if(info == null){
            return ReturnResult.FAILUER("未找到此记录！");
        }
        info.setFisok(fisok);
        info.setFusername(fusername);
        info.setFidentityno(fidentityno);
        info.setFcreatetime(new Date());

        if(adminIdentityService.updateIdentity(info)){
            return ReturnResult.SUCCESS("修改成功！");
        }
        return ReturnResult.FAILUER("修改失败！");

    }
}
