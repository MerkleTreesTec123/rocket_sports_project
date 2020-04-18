package com.qkwl.web.front.controller.api;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.user.IUserSecurityService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 短信接口
 */
@Controller
public class ValidateApiController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ValidateApiController.class);

    @Autowired
    IUserService userService;

    @Autowired
    private ValidateHelper validateHelper;

    @Autowired
    private IUserSecurityService userSecurityService;

    /**
     * 发送业务短信
     *
     * @param area  区号
     * @param phone 手机号
     * @param type  112手机端注册、102绑定手机、109找回登录密码-手机找回、107修改交易密码
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/validate/send")
    public ReturnResult sendMessage(String area, String phone, Integer type) {
        if (TextUtils.isEmpty(area) || TextUtils.isEmpty(phone)) {
            return ReturnResult.FAILUER("手机号和区号不能为空");
        }

        if ("86".equals(area)) {
            if (!phone.matches(Constant.PhoneReg)) {
                return ReturnResult.FAILUER(GetR18nMsg("user.reg.error.1001"));
            }
        }
        area = area.replace("+", "");
        ReturnResult checkResult = checkPhoneIsValidate(phone, type);
        if (checkResult.getCode() != ReturnResult.SUCCESS) {
            return checkResult;
        }
        boolean result = false;
        try {
            int sendType = "86".equals(area) ? SendTypeEnum.SMS_TEXT.getCode():SendTypeEnum.SMS_INTERNATIONAL.getCode();
            result = validateHelper.smsValidateCode(0, area, phone, sendType,
                    PlatformEnum.BC.getCode(), type, LocaleEnum.ZH_TW.getCode());
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return result ? ReturnResult.SUCCESS():ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));

    }

    /**
     * 发送需要签名的短信
     *
     * @param type 104人民币提现、105虚拟币提现、106修改登录密码、107修改交易密码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/v1/validate/sign", method = RequestMethod.POST)
    public ReturnResult sendNeedTokenMessage(Integer type) {
        FUser userInfo = getCurrentUserInfoByApiToken();
        if (!userInfo.getFistelephonebind()) {
            return ReturnResult.FAILUER("你还没有绑定手机，请先到安全中心绑定手机");
        }
        boolean result = false;
        try {
            int sendType = "86".equals(userInfo.getFareacode()) ? SendTypeEnum.SMS_TEXT.getCode():SendTypeEnum.SMS_INTERNATIONAL.getCode();
            result = validateHelper.smsValidateCode(userInfo.getFid(),
                    userInfo.getFareacode(), userInfo.getFtelephone(), sendType,
                    PlatformEnum.BC.getCode(), type, LocaleEnum.ZH_TW.getCode());
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            result = false;
        }
        if (result) {
            return ReturnResult.SUCCESS();
        } else {
            return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
        }
    }

    /**
     * 发送绑定邮件验证码
     */
    @ResponseBody
    @RequestMapping(value = "/v1/email/send")
    public ReturnResult sendemail(
            @RequestParam(required = true, defaultValue = "0") String address,
            @RequestParam(required = false, defaultValue = "1") Integer msgtype) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FUser fuser = getCurrentUserInfoByApiToken();
        if (address.equals("0")) {
            //判断邮件是否为空
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1003"));
        }

        if (!address.matches(Constant.EmailReg)) {
            return ReturnResult.FAILUER("邮箱格式不正确");
        }

        fuser.setFemail(address);

        String ip = Utils.getIpAddr(request);
        ValidateParamInfo paramInfo = new ValidateParamInfo();
        paramInfo.setIp(ip);
        paramInfo.setPlatform(PlatformEnum.BC);
        paramInfo.setBusinessType(BusinessTypeEnum.EMAIL_APP_BIND);
        paramInfo.setLocale(super.getLanEnum());

        Result validateResult = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, LogUserActionEnum.APP_ADD_EMAIL, null);
        if (!validateResult.getSuccess()) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + validateResult.getCode()));
        }

        //验证邮件已发送，请及时验证！
        return ReturnResult.SUCCESS(GetR18nMsg("user.security.error.1017"));
    }


    /**
     * 邮件绑定
     */
    @ResponseBody
    @RequestMapping(value = "/v1/email/add")
    public ReturnResult mailValidate(@RequestParam(required = true) String code) throws Exception {
        Integer uid = getCurrentUserInfoByApiToken().getFid();
        boolean flag = false;
        boolean isUpdateUser = true;
        try {
            ValidateParamInfo paramInfo = new ValidateParamInfo();
            paramInfo.setPlatform(PlatformEnum.BC);
            paramInfo.setBusinessType(BusinessTypeEnum.EMAIL_APP_BIND);
            paramInfo.setCode(code);
            paramInfo.setUuid(code);
            paramInfo.setIp(getIpAddr());
            FUser user = getCurrentUserInfoByApiToken();
            if(user == null){
                user = new FUser();
                isUpdateUser = false;
            }
            user.setFid(uid);
            user.setFismailbind(true);
            user.setIp(getIpAddr());
            Result result = userSecurityService.updateUserSecurityInfo(user, paramInfo,
                    LogUserActionEnum.APP_BIND_EMAIL, ScoreTypeEnum.EMAIL);
            if(result.getSuccess()) {
                flag = true;
                if (isUpdateUser) {
                    //更新redis中的用户信息
                    updateUserInfo(user);
                }
            }else{
                logger.error("邮件错误：code = "+result.getCode() + ",message = "+result.getMsg() +",uid = "+uid);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return flag?ReturnResult.SUCCESS():ReturnResult.FAILUER("绑定失败");
    }

    /**
     * 检查手机号有效性
     *
     * @param phone        手机号
     * @param businessType 业务类型
     * @return
     */
    private ReturnResult checkPhoneIsValidate(String phone, Integer businessType) {
        FUser fuser = new FUser();
        if (businessType == BusinessTypeEnum.SMS_APP_REGISTER.getCode().intValue()) {
            fuser.setFtelephone(phone);
            if (this.userService.selectIsExistByParam(fuser)) {
                return ReturnResult.FAILUER(GetR18nMsg("user.reg.error.1002"));
            }
        } else if (businessType == BusinessTypeEnum.SMS_CNY_WITHDRAW.getCode()) {
            //提现验证码
            FUser userInfo = getCurrentUserInfoByApiToken();
            if (!userInfo.getFistelephonebind()) {
                return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11020"));
            }
        } else if(businessType == BusinessTypeEnum.SMS_FIND_PHONE_PASSWORD.getCode()){
            fuser.setFtelephone(phone);
            if (!this.userService.selectIsExistByParam(fuser)) {
                return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11002"));
            }
        } else if (businessType == BusinessTypeEnum.SMS_MODIFY_TRADE_PASSWORD.getCode()){
            fuser.setFtelephone(phone);
            if (!this.userService.selectIsExistByParam(fuser)) {
                return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11021"));
            }
        }else {
            return ReturnResult.FAILUER("");

        }
        return ReturnResult.SUCCESS();

    }

    /**
     *  绑定银行卡和人民币提现申请都需要这个验证码
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/send_bank_sms")
    public ReturnResult sendBindBankCode() throws Exception {
        FUser userInfo = getCurrentUserInfoByApiToken();
        if (userInfo == null){
            return ReturnResult.FAILUER("请先登录");
        }
        if (!userInfo.getFistelephonebind()){
            return ReturnResult.FAILUER("你还没有绑定手机，请先到安全中心绑定手机");
        }
        boolean result = false;
        try {
            result = validateHelper.smsValidateCode(userInfo.getFid(),
                    userInfo.getFareacode(),userInfo.getFtelephone(), SendTypeEnum.SMS_TEXT.getCode(),
                    PlatformEnum.BC.getCode(),BusinessTypeEnum.SMS_CNY_WITHDRAW.getCode(),super.getLanEnum().getCode());
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            result = false;
        }
        if (result) {
            return ReturnResult.SUCCESS();
        } else {
            return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
        }
    }


}
