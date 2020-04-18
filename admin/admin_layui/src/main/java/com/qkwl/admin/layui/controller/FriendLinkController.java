package com.qkwl.admin.layui.controller;

import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.LinkTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.web.FFriendLink;
import com.qkwl.common.rpc.admin.IAdminSettingService;
import com.qkwl.admin.layui.base.WebBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 友情链接
 * Created by wangchen on 2017-04-07.
 */
@Controller
public class FriendLinkController extends WebBaseController {
    @Autowired
    private IAdminSettingService adminSettingService;

    @RequestMapping("/friendLink/linkList")
    public ModelAndView linkList(
            @RequestParam(value = "pageNum", required=false,defaultValue="1") Integer currentPage,
            @RequestParam(value = "keywords", required=false) String keywords,
            @RequestParam(value = "orderField", required=false,defaultValue="fcreatetime") String orderField,
            @RequestParam(value = "orderDirection", required=false,defaultValue="desc") String orderDirection
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("friendLink/linkList");

        Pagination<FFriendLink> page=new Pagination<FFriendLink>(currentPage, Constant.adminPageSize);
        FFriendLink friendLink=new FFriendLink();
        if (keywords != null && keywords.trim().length() > 0) {
            page.setKeyword(keywords);
            modelAndView.addObject("keywords", keywords);
        }

        page.setOrderDirection(orderDirection);
        page.setOrderField(orderField);
        page=adminSettingService.selectLinkPageList(page,friendLink);

        modelAndView.addObject("linkList", page);
        return modelAndView;
    }

    @RequestMapping("friendLink/goLinkJSP")
    public ModelAndView goLinkJSP(
            @RequestParam(value = "url", required=false,defaultValue="") String url,
            @RequestParam(value = "fid", required=false,defaultValue="0") Integer fid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (fid != null && fid > 0) {
            FFriendLink friendlink = this.adminSettingService.selectLinkById(fid);
            modelAndView.addObject("friendlink", friendlink);
        }
        Map<Integer,String> map = new HashMap<Integer,String>();
        map.put(LinkTypeEnum.LINK_VALUE, LinkTypeEnum.getEnumString(LinkTypeEnum.LINK_VALUE));
        modelAndView.addObject("linkTypeMap", map);
        return modelAndView;
    }

    @RequestMapping("friendLink/saveLink")
    @ResponseBody
    public ReturnResult saveLink(
            @RequestParam(value = "fdescription", required=false,defaultValue="") String fdescription,
            @RequestParam(value = "fname", required=false,defaultValue="") String fname,
            @RequestParam(value = "forder", required=false,defaultValue="0") Integer forder,
            @RequestParam(value = "furl", required=false,defaultValue="") String furl
    ) throws Exception {

        if(forder <= 0){
            return ReturnResult.FAILUER("请设置序号！");
        }

        FFriendLink link = new FFriendLink();
        link.setFdescription(fdescription);
        link.setFname(fname);
        link.setForder(forder);
        link.setFurl(furl);
        link.setFcreatetime(Utils.getTimestamp());
        link.setVersion(0);
        link.setFtype(LinkTypeEnum.LINK_VALUE);
        this.adminSettingService.insertLink(link);

        return ReturnResult.SUCCESS("新增成功");
    }

    @RequestMapping("friendLink/deleteLink")
    @ResponseBody
    public ReturnResult deleteLink(@RequestParam(required=false,defaultValue="1") Integer fid) throws Exception {
        this.adminSettingService.deleteLink(fid);
        return ReturnResult.SUCCESS("删除成功");
    }

    @RequestMapping("friendLink/updateLink")
    @ResponseBody
    public ReturnResult updateLink(
            @RequestParam(value = "fid", required=false,defaultValue="") Integer fid,
            @RequestParam(value = "fdescription", required=false,defaultValue="") String fdescription,
            @RequestParam(value = "fname", required=false,defaultValue="") String fname,
            @RequestParam(value = "forder", required=false,defaultValue="0") Integer forder,
            @RequestParam(value = "furl", required=false,defaultValue="") String furl
    ) throws Exception {

        if(forder <= 0){
            return ReturnResult.FAILUER("请设置序号！");
        }

        FFriendLink link = adminSettingService.selectLinkById(fid);
        link.setFdescription(fdescription);
        link.setFname(fname);
        link.setForder(forder);
        link.setFurl(furl);
        adminSettingService.updateLink(link);

        return ReturnResult.SUCCESS("修改成功");
    }
}
