package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import org.apache.commons.lang3.StringUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.LogAdminActionEnum;
import com.qkwl.common.dto.Enum.SystemArgsTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.system.FSystemArgs;
import com.qkwl.common.rpc.admin.IAdminSettingService;
import com.qkwl.common.oss.OssHelper;
import com.qkwl.admin.layui.utils.MQSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统参数列表
 * Created by wangchen on 2017-04-07.
 */
@Controller
public class SystemArgsController extends WebBaseController {
    @Autowired
    private IAdminSettingService adminSettingService;
    @Autowired
    private MQSend mqSend;
    @Autowired
    private OssHelper ossHelper;

    // 每页显示多少条数据
    private int numPerPage = Constant.adminPageSize;

    @RequestMapping("/systemArgs/systemArgsList")
    public ModelAndView Index(
            @RequestParam(value="pageNum", required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false, defaultValue = "fid") String orderField,
            @RequestParam(required = false, defaultValue = "asc") String orderDirection) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("systemArgs/systemArgsList");

        Pagination<FSystemArgs> page = new Pagination<FSystemArgs>(currentPage, Constant.adminPageSize);
        page.setOrderDirection(orderDirection);
        page.setOrderField(orderField);
        page.setKeyword(keywords);

        FSystemArgs fVirtualCoinType = new FSystemArgs();
        page = adminSettingService.selectSystemArgsPageList(page, fVirtualCoinType);

        if (!StringUtils.isEmpty(keywords)) {
            modelAndView.addObject("keywords", keywords);
        }

        modelAndView.addObject("systemArgsList", page);
        return modelAndView;
    }

    @RequestMapping("systemArgs/goSystemArgsJSP")
    public ModelAndView goSystemArgsJSP(@RequestParam(value = "fid", required = false, defaultValue = "0") Integer fid,
                                        @RequestParam(value = "url", required = false) String url,
                                        @RequestParam(value = "fileType", required = false, defaultValue = "") String fileType,
                                        @RequestParam(value = "maxSize", required = false, defaultValue = "") String maxSize) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (fid != null && fid > 0) {
            FSystemArgs systemargs = this.adminSettingService.selectSystemArgsById(fid);
            modelAndView.addObject("systemargs", systemargs);
        }
        if (maxSize != null) {
            modelAndView.addObject("maxSize", maxSize);
        }
        if (fileType != null) {
            modelAndView.addObject("fileType", fileType);
        }
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(SystemArgsTypeEnum.ADMIN.getCode(), SystemArgsTypeEnum.getValueByCode(SystemArgsTypeEnum.ADMIN.getCode()));
        map.put(SystemArgsTypeEnum.FRONT.getCode(), SystemArgsTypeEnum.getValueByCode(SystemArgsTypeEnum.FRONT.getCode()));
        modelAndView.addObject("typeMap", map);
        return modelAndView;
    }

    @RequestMapping("systemArgs/saveSystemArgs")
    @ResponseBody
    public ReturnResult saveSystemArgs(
            @RequestParam(value = "filedata", required = false) String filedata,
            @RequestParam(value = "key", required = true) String key,
            @RequestParam(value = "value", required = false) String value,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "furl", required = false) String furl,
            @RequestParam(value = "desc", required = false) String desc) throws Exception {

        FSystemArgs systemArgs = new FSystemArgs();
        systemArgs.setFkey(key);
        systemArgs.setFvalue(value);
        systemArgs.setFurl(furl);
        systemArgs.setFtype(type);
        systemArgs.setFdescription(desc);

        if (StringUtils.isEmpty(systemArgs.getFvalue())) {
            return ReturnResult.FAILUER("参数值或图片链接不能全为空");
        }
        systemArgs.setVersion(0);
        this.adminSettingService.insertSystemArgs(systemArgs);

        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        FAdmin sessionAdmin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
        mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ARGS_SAVE, ip);
        return ReturnResult.SUCCESS("新增成功");
    }

    @RequestMapping("systemArgs/updateSystemArgs")
    @ResponseBody
    public ReturnResult updateSystemArgs(
            @RequestParam(value = "filedata", required = false) String filedata,
            @RequestParam(value = "key", required = true) String key,
            @RequestParam(value = "furl", required = false) String furl,
            @RequestParam(value = "value", required = true) String value,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "fid", required = true) int fid,
            @RequestParam(value = "desc", required = false) String desc) throws Exception {

        FSystemArgs systemargs = this.adminSettingService.selectSystemArgsById(fid);
        systemargs.setFvalue(value);
        systemargs.setFdescription(desc);
        systemargs.setFurl(furl);
        systemargs.setFtype(type);

        if (systemargs.getFvalue() == null || systemargs.getFvalue().trim().length() == 0) {
            return ReturnResult.FAILUER("参数值或图片链接不能全为空");
        }

        this.adminSettingService.updateSystemArgs(systemargs);

        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        FAdmin sessionAdmin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
        mqSend.SendAdminAction(sessionAdmin.getFagentid(), sessionAdmin.getFid(), LogAdminActionEnum.ARGS_MODIFY, ip);
        return ReturnResult.SUCCESS("更新成功");
    }

}
