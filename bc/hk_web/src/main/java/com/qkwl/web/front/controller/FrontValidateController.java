package com.qkwl.web.front.controller;

import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.user.IUserSecurityService;
import com.qkwl.web.front.controller.base.WebBaseController;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserDTO;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FrontValidateController extends WebBaseController {

    private static final Logger logger = LoggerFactory.getLogger(FrontValidateController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserSecurityService userSecurityService;

    @Autowired
    private ValidateHelper validateHelper;

    /**
     * 密码重置请求
     */
    @RequestMapping("/validate/reset_passwd")
    public ModelAndView reset() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("front/validate/reset");
        return modelAndView;
    }

    /**
     * 邮件找回密码
     */
    @RequestMapping("/validate/reset_email")
    public ModelAndView resetEmail(
            @RequestParam(required = false, defaultValue = "0") Integer uid,
            @RequestParam(required = false, defaultValue = "") String uuid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        FUser user = userService.selectUserById(uid);
        if (user != null && user.getFismailbind()) {
            Boolean result = validateHelper.mailCodeValidate(user.getFemail(), PlatformEnum.BC.getCode(), BusinessTypeEnum.EMAIL_FIND_PASSWORD.getCode(), uuid);
            if (!result) {
                super.setRedisData(RedisConstant.SETREDISINFO_KEY + user.getFid(), user);
                modelAndView.addObject("fuser", ModelMapperUtils.mapper(user, FUserDTO.class));
                modelAndView.addObject("uuid", uuid);
                modelAndView.addObject("emailvalidate", true);
            }
        }
        modelAndView.setViewName("front/validate/resetEmail");
        return modelAndView;
    }

    /**
     * 手机找回验证码页面
     */
    @RequestMapping("/validate/reset_phone")
    public ModelAndView resetPhone(HttpServletRequest request) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        String phone_reset = super.getRedisData("phone_reset");
        boolean isSecondStep = false;
        if (phone_reset != null) {
            FUser fuser = new FUser();
            fuser.setFtelephone(phone_reset);
            List<FUser> fusers = this.userService.selectUserByUser(fuser);
            if (fusers != null && fusers.size() > 0) {
                modelAndView.addObject("fuser", ModelMapperUtils.mapper(fusers.get(0), FUserDTO.class));
                isSecondStep = true;
            }
        }
        if (isSecondStep) {
            super.deletRedisData("phone_reset");
            super.setRedisData("phone_reset_Second", phone_reset);
        }
        modelAndView.addObject("isSecondStep", isSecondStep);
        modelAndView.setViewName("front/validate/resetPhone");
        return modelAndView;
    }

    /**
     * 邮件绑定
     */
    @RequestMapping(value = "/validate/mail_validate")
    public ModelAndView mailValidate(HttpServletRequest request, @RequestParam(required = true) int uid,
                                     @RequestParam(required = true) String uuid) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
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
            } else {
                logger.error("邮件错误：code = " + result.getCode() + ",message = " + result.getMsg() + ",uid = " + uid);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        modelAndView.addObject("validate", flag);
        modelAndView.setViewName("front/user/reg_regconfirm");
        return modelAndView;
    }
}
