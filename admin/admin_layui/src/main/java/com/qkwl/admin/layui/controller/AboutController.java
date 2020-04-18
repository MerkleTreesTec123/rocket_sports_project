package com.qkwl.admin.layui.controller;

import org.apache.commons.lang3.StringUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.web.FAbout;
import com.qkwl.common.rpc.admin.IAdminSettingService;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 关于我们
 * @author ZKF
 */
@Controller
public class AboutController extends WebBaseController {

    @Autowired
    private IAdminSettingService adminSettingService;

    /**
     * 列表
     */
    @RequestMapping("/about/aboutList")
    public ModelAndView aboutList(
            @RequestParam(value = "pageNum", required=false,defaultValue="1") Integer currentPage,
            @RequestParam(value = "keywords", required=false) String keywords,
            @RequestParam(value = "orderField", required=false,defaultValue="fid") String orderField,
            @RequestParam(value = "orderDirection", required=false,defaultValue="desc") String orderDirection
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("about/aboutList");
        //分页条件
        Pagination<FAbout> page=new Pagination<FAbout>(currentPage, 20);
        page.setOrderDirection(orderDirection);
        page.setOrderField(orderField);
        page.setKeyword(keywords);
        boolean falg=false;
        FAbout fAbout=new FAbout();
        page = adminSettingService.selectAboutPageList(page, fAbout, WebConstant.BCAgentId);

        if(!StringUtils.isEmpty(keywords)){
            modelAndView.addObject("keywords", keywords);
        }
        modelAndView.addObject("page", page);

        return modelAndView;
    }

    /**
     * 查询跳转
     */
    @RequestMapping("admin/goAbout")
    public ModelAndView goAboutJSP(
            @RequestParam(value = "url", required=false) String url,
            @RequestParam(value = "fid", required=false) Integer fid
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);

        if (fid != null) {
            FAbout about = adminSettingService.selectAboutById(fid);
            modelAndView.addObject("fabout", about);
        }

        return modelAndView;
    }

    /**
     * 更新
     */
    @ResponseBody
    @RequestMapping("admin/updateAbout")
    public ReturnResult updateAbout(
            @RequestParam(value = "fid", required=false) Integer fid,
            @RequestParam(value = "fcontent", required=false) String fcontent,
            @RequestParam(value = "ftitle", required=false) String ftitle
    ) throws Exception {
        FAbout about = adminSettingService.selectAboutById(fid);
        about.setFcontent(fcontent);
        about.setFtitle(ftitle);
        this.adminSettingService.updateAbout(about);
        return ReturnResult.SUCCESS("修改成功");
    }
}

