package com.qkwl.web.front.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.user.FUserDTO;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.rpc.user.IUserSecurityService;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import org.apache.http.util.TextUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.result.Result;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.framework.validate.ValidateHelper;

/****
 * 
 *   找回密码步骤
 * 
 *   第一步：/validate/sendFindPhoneCode.html
 *   第二步:  /validate/reset_phone_json.html
 *   第三步：/validate/reset_password_check.html
 *   第四步：/validate/reset_password_phone.html
 * 
 * 
 */
@Controller
public class FrontValidateJsonController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(FrontValidateJsonController.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private ValidateHelper validateHelper;
    @Autowired
    private ValidationCheckHelper validationCheckHelper;
    @Autowired
    private IUserSecurityService userSecurityService;

    /**
     * 邮箱找回密码验证并发送邮件
     */
    @ResponseBody
    @RequestMapping("/validate/send_findbackmail")
    public ReturnResult queryEmail(
            @RequestParam(required = true) String email,
            @RequestParam(required = false, defaultValue = "0") String idcard,
            @RequestParam(required = false, defaultValue = "0") String idcardno,
            @RequestParam(required = true) String imgcode) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        if (imgcode.equals("")) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11004"));
        }
        if (!email.matches(Constant.EmailReg)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11007"));
        }
        // 效验图片验证码
        String session_code = super.getRedisData("CHECKCODE");
        if (session_code == null || !imgcode.equalsIgnoreCase(session_code)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11004"));
        }
        FUser param = new FUser();
        param.setFemail(email);
        FUser fuser = this.userService.selectUserByParam(param);
        // 没找到用户
        if (fuser == null) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1000"));
        }
        if (fuser.getFhasrealvalidate() && !idcardno.toLowerCase().equals(fuser.getFidentityno())) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1013"));
        }

        if (fuser.getFismailbind()) {
            boolean emailvalidate = this.validateHelper.mailOverdueValidate(email, PlatformEnum.BC
                    , BusinessTypeEnum.EMAIL_FIND_PASSWORD);
            if (emailvalidate) {
                return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11006"));
            }
            boolean result = false;
            try {
                result = this.validateHelper.mailSendContent(email, PlatformEnum.BC, super.getLanEnum()
                        , BusinessTypeEnum.EMAIL_FIND_PASSWORD, Utils.getIpAddr(request), fuser);
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                result = false;
            }
            if (result) {
                return ReturnResult.SUCCESS();
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10002"));
            }
        } else {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11011"));
        }
    }

    /**
     * 邮箱找回密码重置密码
     */
    @ResponseBody
    @RequestMapping("/validate/reset_password")
    public ReturnResult resetPassword(
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = true) String newPassword,
            @RequestParam(required = true) String newPassword2,
            @RequestParam(required = true) int fid) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        // 检测密码强度
        if (!newPassword.matches(Constant.passwordReg) || newPassword.length() < 6) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11009"));
        }
        if (!newPassword.equals(newPassword2)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11010"));
        }

        String ip = Utils.getIpAddr(request);

        ValidateParamInfo validateParamInfo = new ValidateParamInfo();
        validateParamInfo.setCode(phoneCode);
        validateParamInfo.setBusinessType(BusinessTypeEnum.SMS_FIND_EMAIL_PASSWORD);
        validateParamInfo.setPlatform(PlatformEnum.BC);
        validateParamInfo.setIp(ip);
        validateParamInfo.setTotpCode(totpCode);

        FUser fuser = new FUser();
        fuser.setFid(fid);
        fuser.setFloginpassword(Utils.MD5(newPassword));
        fuser.setIp(ip);
        try {
            Result result = userSecurityService.restLoginPasword(fuser, validateParamInfo, 1);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS();
            }
            return ReturnResult.FAILUER(GetR18nMsg("user.findPassword.error." + result.getCode()));
        } catch (Exception e) {
            logger.error("resetPassword err {}", e);
        }
        return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
    }

    /**
     * 绑定邮件发送邮件
     */
    @ResponseBody
    @RequestMapping("/validate/send_bindmail")
    public ReturnResult postMail(@RequestParam(required = false, defaultValue = "0") String email) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FUser fuser = getCurrentUserInfoByToken();
        if (email.equals("0")) {
            //判断邮件是否为空
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1003"));
        }

        if (!email.matches(Constant.EmailReg)) {
            return ReturnResult.FAILUER("邮箱格式不正确");
        }
        fuser.setFemail(email);

        String ip = Utils.getIpAddr(request);

        ValidateParamInfo paramInfo = new ValidateParamInfo();
        paramInfo.setIp(ip);
        paramInfo.setPlatform(PlatformEnum.BC);
        paramInfo.setBusinessType(BusinessTypeEnum.EMAIL_VALIDATE_BING);
        paramInfo.setLocale(super.getLanEnum());

        Result validateResult = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, LogUserActionEnum.ADD_MAIL, null);
        if (!validateResult.getSuccess()) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + validateResult.getCode()));
        }

        //验证邮件已发送，请及时验证！
        return ReturnResult.SUCCESS(GetR18nMsg("user.security.error.1017"));
    }

    @ResponseBody
    @RequestMapping("/validate/send_bindphone_sms")
    public ReturnResult postPhone(@RequestParam(required = false, defaultValue = "0") String phone,
                                  @RequestParam(required = false, defaultValue = "0") String area) throws Exception {

        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FUser fuser = getCurrentUserInfoByToken();

        if (phone.equals("0")) {
            //判断手机是否为空
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1003"));
        }
        fuser.setFtelephone(phone);

        String ip = Utils.getIpAddr(request);

        ValidateParamInfo paramInfo = new ValidateParamInfo();
        paramInfo.setIp(ip);

        paramInfo.setPlatform(PlatformEnum.BC);
        paramInfo.setBusinessType(BusinessTypeEnum.SMS_BING_MOBILE);
        paramInfo.setLocale(super.getLanEnum());
        paramInfo.setAreaCode(area);
        paramInfo.setPhone(phone);

        Result validateResult = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, LogUserActionEnum.ADD_PHONE, null);
        if (!validateResult.getSuccess()) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + validateResult.getCode()));
        }
        //验证短信已经发送，请及时验证！
        return ReturnResult.SUCCESS(GetR18nMsg("user.security.error.1017"));
    }

    @ResponseBody
    @RequestMapping("/validate/bindphone")
    public ReturnResult bindPhone(@RequestParam(required = false, defaultValue = "0") String phone,
                                  @RequestParam(required = false, defaultValue = "0") String area,
                                  @RequestParam(required = false, defaultValue = "0") String code) throws Exception {

        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FUser fuser = getCurrentUserInfoByToken();
        if (phone.equals("0")) {
            //判断手机是否为空
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1003"));
        }
        fuser.setFtelephone(phone);

        String ip = Utils.getIpAddr(request);
        fuser.setIp(ip);
        ValidateParamInfo paramInfo = new ValidateParamInfo();
        paramInfo.setIp(ip);
        paramInfo.setPlatform(PlatformEnum.BC);
        paramInfo.setBusinessType(BusinessTypeEnum.SMS_BING_MOBILE);
        paramInfo.setLocale(super.getLanEnum());
        paramInfo.setSecondAreaCode(area);
        paramInfo.setSecondPhone(phone);
        paramInfo.setSecondCode(code);
        paramInfo.setCode(code);

        Result validateResult = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo,
                LogUserActionEnum.BIND_PHONE, null);
        if (!validateResult.getSuccess()) {
            logger.error(validateResult.getMsg());
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + validateResult.getCode()));
        }
        //验证短信已经发送，请及时验证！
        return ReturnResult.SUCCESS(GetR18nMsg("user.security.error.1017"));
    }

    /**
     * 邮件找回密码
     */
    @ResponseBody
    @RequestMapping("/validate/reset_email_json")
    public ReturnResult resetEmail(
            @RequestParam(required = false, defaultValue = "0") Integer uid,
            @RequestParam(required = false, defaultValue = "") String uuid) throws Exception {
        FUser user = userService.selectUserById(uid);
        JSONObject jsonObject = new JSONObject();
        if (user != null && user.getFismailbind()) {
            Boolean result = validateHelper.mailCodeValidate(user.getFemail(), PlatformEnum.BC.getCode(),
                    BusinessTypeEnum.EMAIL_FIND_PASSWORD.getCode(), uuid);
            if (!result) {
                super.setRedisData(RedisConstant.SETREDISINFO_KEY + user.getFid(), user);
                jsonObject.put("fuser", ModelMapperUtils.mapper(user, FUserDTO.class));
                jsonObject.put("uuid", uuid);
                jsonObject.put("emailvalidate", true);
            }
        }
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 手机找回验证码页面
     */
    @ResponseBody
    @RequestMapping("/validate/reset_phone_json")
    public ReturnResult resetPhone(HttpServletRequest request) throws Exception {
        String phone_reset = super.getRedisData("phone_reset");
        boolean isSecondStep = false;
        JSONObject jsonObject = new JSONObject();
        if (phone_reset != null) {
            FUser fuser = new FUser();
            fuser.setFtelephone(phone_reset);
            List<FUser> fusers = this.userService.selectUserByUser(fuser);
            if (fusers != null && fusers.size() > 0) {
                jsonObject.put("fuser", ModelMapperUtils.mapper(fusers.get(0), FUserDTO.class));
                isSecondStep = true;
            }
        }
        if (isSecondStep) {
            super.deletRedisData("phone_reset");
            String redisKey = super.setRedisData("phone_reset_Second", phone_reset);
            jsonObject.put("phone_reset_Second", redisKey);
        }
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 邮件绑定
     */
    @ResponseBody
    @RequestMapping(value = "/validate/mail_validate_json")
    public ReturnResult mailValidate(HttpServletRequest request, @RequestParam(required = true) int uid,
                                     @RequestParam(required = true) String uuid) throws Exception {
        boolean flag = false;
        boolean isUpdateUser = true;
        try {
            ValidateParamInfo paramInfo = new ValidateParamInfo();
            paramInfo.setUuid(uuid);
            paramInfo.setPlatform(PlatformEnum.BC);
            paramInfo.setBusinessType(BusinessTypeEnum.EMAIL_VALIDATE_BING);
            FUser user = getCurrentUserInfoByToken();
            if (user == null) {
                user = new FUser();
                isUpdateUser = false;
            }
            user.setFid(uid);
            user.setFismailbind(true);
            user.setIp(Utils.getIpAddr(sessionContextUtils.getContextRequest()));
            Result result = userSecurityService.updateUserSecurityInfo(user, paramInfo, LogUserActionEnum.BIND_MAIL, ScoreTypeEnum.EMAIL);
            if (result.getSuccess()) {
                flag = true;
                if (isUpdateUser) {
                    //更新redis中的用户信息
                    updateUserInfo(user);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("validate", flag);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 通过手机找回密码-发送验证码
     *
     * @param phone
     * @param area
     * @param imgcode
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/validate/sendFindPhoneCode")
    public ReturnResult sendFindPhoneCode(@RequestParam(required = false, defaultValue = "0") String phone,
                                          @RequestParam(required = false, defaultValue = "0") String area,
                                          @RequestParam(required = false, defaultValue = "0") String imgcode) throws Exception {
        if (phone.equals("0") || area.equals("0")) {
            //判断手机是否为空
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1003"));
        }

        //查找用户
        FUser param = new FUser();
        param.setFtelephone(phone);
        FUser fuser = this.userService.selectUserByParam(param);
        // 没找到用户
        if (fuser == null) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1000"));
        }

        // 效验图片验证码
        String session_code = super.getRedisData("CHECKCODE");
        if (session_code == null || !imgcode.equalsIgnoreCase(session_code)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11004"));
        }
        try {
            boolean isSuccess = validateHelper.smsValidateCode(0, area, phone,
                    SendTypeEnum.SMS_TEXT.getCode(), PlatformEnum.BC.getCode(),
                    BusinessTypeEnum.SMS_FIND_PHONE_PASSWORD.getCode(),super.getLanEnum().getCode());
            if (!isSuccess) {
                logger.error("找回密码短信可能已经发送失败了");
            }       
        } catch (BCException e) {
            logger.error(e.getMessage());
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1019"));
        }
        //验证短信已经发送，请及时验证！
        return ReturnResult.SUCCESS(GetR18nMsg("user.security.error.1017"));
    }

    /**
     * 通过手机找回密码-验证手机验证码
     */
    @ResponseBody
    @RequestMapping("/validate/reset_password_check")
    public ReturnResult resetPasswordCheckCode(@RequestParam(required = false, defaultValue = "0") String phone,
                                               @RequestParam(required = false, defaultValue = "0") String area,
                                               @RequestParam(required = false, defaultValue = "0") String code,
                                               @RequestParam(required = false, defaultValue = "0") String idcardno) {
        if (phone.equals("0") || area.equals("0") || code.equals("0")) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1003"));
        }

        FUser param = new FUser();
        param.setFtelephone(phone);
        FUser fuser = this.userService.selectUserByParam(param);
        // 没找到用户
        if (fuser == null) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1000"));
        }

        if (fuser.getFhasrealvalidate() && !idcardno.toLowerCase().equals(fuser.getFidentityno())) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1013"));
        }

        if (fuser.getFistelephonebind()) {
            //验证手机验证码
            Result phoneCodeCheckResult = validationCheckHelper.getPhoneCodeCheck(area, phone, code, BusinessTypeEnum.SMS_FIND_PHONE_PASSWORD.getCode(),
                    Utils.getIpAddr(sessionContextUtils.getContextRequest()), PlatformEnum.BC.getCode());
            if (phoneCodeCheckResult.getSuccess()) {
                //缓存用户手机
                String redisKey = setRedisData("phone_reset", phone);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("phone_reset", redisKey);
                return ReturnResult.SUCCESS(jsonObject);
            } else {
                return ReturnResult.FAILUER(phoneCodeCheckResult.getMsg());
            }
        } else {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11011"));
        }
    }

    /**
     * 通过手机找回密码-重置密码
     */
    @ResponseBody
    @RequestMapping("/validate/reset_password_phone")
    public ReturnResult resetPasswordByPhone(
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam(required = true) String newPassword,
            @RequestParam(required = true) String newPassword2) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        // 检测密码强度
        if (!newPassword.matches(Constant.passwordReg) || newPassword.length() < 6) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11009"));
        }

        if (!newPassword.equals(newPassword2)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11010"));
        }

        String phone = getRedisData("phone_reset_Second");
        if (TextUtils.isEmpty(phone)) {
            return ReturnResult.FAILUER("验证码过期");
        }

        String ip = Utils.getIpAddr(request);

        ValidateParamInfo validateParamInfo = new ValidateParamInfo();
        validateParamInfo.setBusinessType(BusinessTypeEnum.SMS_FIND_PHONE_PASSWORD);
        validateParamInfo.setPlatform(PlatformEnum.BC);
        validateParamInfo.setIp(ip);
        validateParamInfo.setTotpCode(totpCode);

        FUser fuser = new FUser();
        fuser.setFloginpassword(Utils.MD5(newPassword));
        fuser.setIp(ip);
        fuser.setFtelephone(phone);
        try {
            Result result = userSecurityService.restLoginPasword(fuser, validateParamInfo, 0);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS();
            }
            return ReturnResult.FAILUER(GetR18nMsg("user.findPassword.error." + result.getCode()));
        } catch (Exception e) {
            logger.error("resetPassword err {}", e);
        }
        return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
    }
}
