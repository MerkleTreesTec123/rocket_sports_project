package com.qkwl.admin.layui.controller;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.Enum.validate.*;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.validate.ValidateAccountDTO;
import com.qkwl.common.dto.validate.ValidateTemplateDTO;
import com.qkwl.common.rpc.admin.validate.IAdminValidateSettingService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.validate.model.ValidatePlatformDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 验证相关设置
 */
@Controller
public class ValidateSettingController extends WebBaseController {

    @Autowired
    private IAdminValidateSettingService adminValidateService;

    /**
     * 验证账号列表
     */
    @RequestMapping("/validate/accountlist")
    public ModelAndView accountList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "keywords", required = false) String keyWord,
            @RequestParam(value = "orderField", defaultValue = "id") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "ASC") String orderDirection) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("validate/accountlist");

        Pagination<ValidateAccountDTO> pageParam = new Pagination<>(currentPage, Constant.adminPageSize);
        // 排序条件
        pageParam.setOrderField(orderField);
        pageParam.setOrderDirection(orderDirection);

        ValidateAccountDTO validateAccount = new ValidateAccountDTO();
        // 关键字
        if (!StringUtils.isEmpty(keyWord)) {
            pageParam.setKeyword(keyWord);
            modelAndView.addObject("keywords", keyWord);
        }
        // 查询
        pageParam = adminValidateService.listValidateAccount(pageParam, validateAccount);
        modelAndView.addObject("page", pageParam);
        return modelAndView;
    }

    /**
     * 验证账号跳转
     */
    @RequestMapping("validate/goaccount")
    public ModelAndView goAccount(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "id", required = false, defaultValue = "0") Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (id != 0) {
            ValidateAccountDTO validateAccount = adminValidateService.getValidateAccount(id);
            modelAndView.addObject("account", validateAccount);
        }
        Map<Integer, String> typeMap = new LinkedHashMap<>();
        for (SendTypeEnum sendTypeEnum : SendTypeEnum.values()) {
            typeMap.put(sendTypeEnum.getCode(), sendTypeEnum.getValue());
        }
        modelAndView.addObject("typeMap", typeMap);
        return modelAndView;
    }

    /**
     * 验证账号新增
     */
    @RequestMapping("validate/accountadd")
    @ResponseBody
    public ReturnResult accountAdd(
            @RequestParam(value = "type") Integer type,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "accessKey") String accessKey,
            @RequestParam(value = "secretKey") String secretKey,
            @RequestParam(value = "url") String url) {
        ValidateAccountDTO validateAccount = new ValidateAccountDTO();
        validateAccount.setType(type);
        validateAccount.setName(name);
        validateAccount.setAccessKey(accessKey);
        validateAccount.setSecretKey(secretKey);
        validateAccount.setUrl(url);
        if (adminValidateService.insertValidateAccount(validateAccount)) {
            return ReturnResult.SUCCESS("新增成功！");
        }
        return ReturnResult.FAILUER("新增失败！");
    }

    /**
     * 验证账号修改
     */
    @RequestMapping("validate/accountedit")
    @ResponseBody
    public ReturnResult accountEdit(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "type") Integer type,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "accessKey") String accessKey,
            @RequestParam(value = "secretKey") String secretKey,
            @RequestParam(value = "url") String url) {
        ValidateAccountDTO validateAccount = adminValidateService.getValidateAccount(id);
        if (validateAccount == null) {
            return ReturnResult.FAILUER("修改记录不存在！");
        }
        validateAccount.setType(type);
        validateAccount.setName(name);
        validateAccount.setAccessKey(accessKey);
        validateAccount.setSecretKey(secretKey);
        validateAccount.setUrl(url);
        if (adminValidateService.updateValidateAccount(validateAccount)) {
            return ReturnResult.SUCCESS("修改成功！");
        }
        return ReturnResult.FAILUER("修改失败！");
    }

    /**
     * 验证平台列表
     */
    @RequestMapping("/validate/platformlist")
    public ModelAndView platformList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "keywords", required = false) String keyWord,
            @RequestParam(value = "orderField", defaultValue = "id") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "ASC") String orderDirection) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("validate/platformlist");

        Pagination<ValidatePlatformDTO> pageParam = new Pagination<>(currentPage, Constant.adminPageSize);
        // 排序条件
        pageParam.setOrderField(orderField);
        pageParam.setOrderDirection(orderDirection);

        ValidatePlatformDTO validatePlatform = new ValidatePlatformDTO();
        // 关键字
        if (!StringUtils.isEmpty(keyWord)) {
            pageParam.setKeyword(keyWord);
            modelAndView.addObject("keywords", keyWord);
        }
        // 查询
        pageParam = adminValidateService.listValidatePlatform(pageParam, validatePlatform);
        modelAndView.addObject("page", pageParam);
        return modelAndView;
    }

    /**
     * 验证平台跳转
     */
    @RequestMapping("validate/goplatform")
    public ModelAndView goPlatform(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "id", required = false, defaultValue = "0") Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (id != 0) {
            ValidatePlatformDTO validatePlatform = adminValidateService.getValidatePlatform(id);
            modelAndView.addObject("platform", validatePlatform);
        }
        Map<Integer, String> accountMap = new LinkedHashMap<>();
        Pagination<ValidateAccountDTO> pageParam = new Pagination<>(1, Constant.adminPageSize);
        // 排序条件
        pageParam.setOrderField("id");
        pageParam.setOrderDirection("ASC");
        ValidateAccountDTO validateAccount = new ValidateAccountDTO();
        pageParam = adminValidateService.listValidateAccount(pageParam, validateAccount);
        for (ValidateAccountDTO account : pageParam.getData()) {
            accountMap.put(account.getId(), account.getName());
        }
        modelAndView.addObject("accountMap", accountMap);
        return modelAndView;
    }

    /**
     * 验证平台新增
     */
    @RequestMapping("validate/platformadd")
    @ResponseBody
    public ReturnResult platformAdd(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "domain") String domain,
            @RequestParam(value = "sign", required = false) String sign,
            @RequestParam(value = "smsId") Integer smsId,
            @RequestParam(value = "voiceSmsId") Integer voiceSmsId,
            @RequestParam(value = "internationalSmsId") Integer internationalSmsId,
            @RequestParam(value = "emailId") Integer emailId) {
        ValidatePlatformDTO validatePlatform = new ValidatePlatformDTO();
        validatePlatform.setName(name);
        validatePlatform.setDomain(domain);
        validatePlatform.setSign(sign);
        validatePlatform.setSmsId(smsId);
        validatePlatform.setVoiceSmsId(voiceSmsId);
        validatePlatform.setInternationalSmsId(internationalSmsId);
        validatePlatform.setEmailId(emailId);
        if (adminValidateService.insertValidatePlatform(validatePlatform)) {
            return ReturnResult.SUCCESS("新增成功！");
        }
        return ReturnResult.FAILUER("新增失败！");
    }

    /**
     * 验证平台修改
     */
    @RequestMapping("validate/platformedit")
    @ResponseBody
    public ReturnResult platformEdit(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "domain") String domain,
            @RequestParam(value = "sign", required = false) String sign,
            @RequestParam(value = "smsId") Integer smsId,
            @RequestParam(value = "voiceSmsId") Integer voiceSmsId,
            @RequestParam(value = "internationalSmsId") Integer internationalSmsId,
            @RequestParam(value = "emailId") Integer emailId) {
        ValidatePlatformDTO validatePlatform = adminValidateService.getValidatePlatform(id);
        if (validatePlatform == null) {
            return ReturnResult.FAILUER("修改记录不存在！");
        }
        validatePlatform.setName(name);
        validatePlatform.setDomain(domain);
        validatePlatform.setSign(sign);
        validatePlatform.setSmsId(smsId);
        validatePlatform.setVoiceSmsId(voiceSmsId);
        validatePlatform.setInternationalSmsId(internationalSmsId);
        validatePlatform.setEmailId(emailId);
        if (adminValidateService.updateValidatePlatform(validatePlatform)) {
            return ReturnResult.SUCCESS("修改成功！");
        }
        return ReturnResult.FAILUER("修改失败！");
    }

    /**
     * 验证模板列表
     */
    @RequestMapping("/validate/templatelist")
    public ModelAndView templateList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "keywords", required = false) String keyWord,
            @RequestParam(value = "orderField", defaultValue = "send_type") String orderField,
            @RequestParam(value = "orderDirection", defaultValue = "ASC") String orderDirection) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("validate/templatelist");

        Pagination<ValidateTemplateDTO> pageParam = new Pagination<>(currentPage, Constant.adminPageSize);
        // 排序条件
        pageParam.setOrderField(orderField);
        pageParam.setOrderDirection(orderDirection);

        ValidateTemplateDTO validateTemplate = new ValidateTemplateDTO();
        // 关键字
        if (!StringUtils.isEmpty(keyWord)) {
            pageParam.setKeyword(keyWord);
            modelAndView.addObject("keywords", keyWord);
        }
        // 查询
        pageParam = adminValidateService.listValidateTemplate(pageParam, validateTemplate);
        modelAndView.addObject("page", pageParam);
        return modelAndView;
    }

    /**
     * 验证模板跳转
     */
    @RequestMapping("validate/gotemplate")
    public ModelAndView goTemplate(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "id", required = false, defaultValue = "0") Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (id != 0) {
            ValidateTemplateDTO validateTemplate = adminValidateService.getValidateTemplate(id);
            modelAndView.addObject("template", validateTemplate);
        }

        Map<Integer, String> languageMap = new LinkedHashMap<>();
        for(LocaleEnum e : LocaleEnum.values()){
            languageMap.put(e.getCode(), e.getValue());
        }

        Map<Integer, String> platformMap = new LinkedHashMap<>();
        for(PlatformEnum e : PlatformEnum.values()){
            platformMap.put(e.getCode(), e.getValue());
        }

        Map<Integer, String> sendMap = new LinkedHashMap<>();
        for(SendTypeEnum e : SendTypeEnum.values()){
            sendMap.put(e.getCode(), e.getValue());
        }

        modelAndView.addObject("platformMap", platformMap);
        modelAndView.addObject("languageMap", languageMap);
        modelAndView.addObject("sendMap", sendMap);
        modelAndView.addObject("params", ParameterTypeEnum.values());
        modelAndView.addObject("business", BusinessTypeEnum.values());
        return modelAndView;
    }

    /**
     * 验证模板修改
     */
    @RequestMapping("validate/templateedit")
    @ResponseBody
    public ReturnResult templateEdit(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "platform") Integer platform,
            @RequestParam(value = "sendType") Integer sendType,
            @RequestParam(value = "language") Integer language,
            @RequestParam(value = "template") String template) {
        ValidateTemplateDTO validateTemplate = adminValidateService.getValidateTemplate(id);
        if (validateTemplate == null) {
            return ReturnResult.FAILUER("修改记录不存在！");
        }
        validateTemplate.setTemplate(template);
        // 解析参数
        String params = "";
        for (ParameterTypeEnum parameterTypeEnum : ParameterTypeEnum.values()) {
            if (template.contains(parameterTypeEnum.getValue())) {
                params += parameterTypeEnum.getCode().toString() + "#";
            }
        }
        if (params.endsWith("#")) {
            params = params.substring(0, params.length() - 1);
        }
        validateTemplate.setParams(params);
        // 解析业务类型
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        String requestParam;
        String businessType = "";
        for (BusinessTypeEnum businessTypeEnum : BusinessTypeEnum.values()) {
            requestParam = request.getParameter("business[" + businessTypeEnum.getCode().toString() + "]");
            if (!StringUtils.isEmpty(requestParam)) {
                businessType += businessTypeEnum.getCode().toString() + "#";
            }
        }
        if (businessType.endsWith("#")) {
            businessType = businessType.substring(0, businessType.length() - 1);
        }
        validateTemplate.setBusinessType(businessType);
        validateTemplate.setPlatform(platform);
        validateTemplate.setSendType(sendType);
        validateTemplate.setLanguage(language);
        if (adminValidateService.updateValidateTemplate(validateTemplate)) {
            return ReturnResult.SUCCESS("修改成功！");
        }
        return ReturnResult.FAILUER("修改失败！");
    }
}
