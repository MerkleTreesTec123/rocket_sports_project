package com.qkwl.common.framework.validate;

import com.qkwl.common.dto.Enum.LimitTypeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.limit.LimitHelper;
import com.qkwl.common.google.GoogleAuth;
import com.qkwl.common.result.Result;
import com.qkwl.common.result.ResultErrorCode;
import com.qkwl.common.util.Utils;

/**
 * 有效性验证工具类
 * Created by ZKF on 2017/7/5.
 */
public class ValidationCheckHelper {

    private ValidateHelper validateHelper;
    private LimitHelper limitHelper;

    public void setValidateHelper(ValidateHelper validateHelper) {
        this.validateHelper = validateHelper;
    }

    public void setLimitHelper(LimitHelper limitHelper) {
        this.limitHelper = limitHelper;
    }

    /**
     * 验证手机验证码是否正确
     *
     * @param areaCode    区号
     * @param phone       手机号
     * @param code        验证码
     * @param messageType 消息类型
     * @param ip          IP地址
     * @param platform    平台
     * @return 服务端返回消息
     */
    public Result getPhoneCodeCheck(String areaCode, String phone, String code, Integer messageType, String ip, Integer platform) {
        Integer type = LimitTypeEnum.PhoneCode.getCode();
        Boolean checkResult = limitHelper.checkLimit(ip, type);
        ResultErrorCode errorCode = ResultErrorCode.PHONE_LIMIT_BEYOND_ERROR;
        if (!checkResult) {
            return Result.failure(errorCode.getCode(), errorCode.getValue());
        }
        boolean mobilValidate = validateHelper.validateSmsCode(areaCode, phone, platform, messageType, code);
        if (mobilValidate) {
            limitHelper.deleteLimit(ip, type);
            return Result.success();
        }
        Integer limitCount = limitHelper.updateLimit(ip, type);
        if(limitCount > 0){
            errorCode = ResultErrorCode.PHONE_LIMIT_COUNT_ERROR;
        }
        return Result.failure(errorCode.getCode(), String.format(errorCode.getValue(), limitCount), limitCount);
    }

    /**
     * 验证邮箱验证码是否正确
     *
     * @param email       邮箱
     * @param code        验证码
     * @param messageType 消息类型
     * @param ip          IP地址
     * @param platform    平台
     * @return 服务端返回消息
     */
    public Result getEmailCodeCheck(String email, String code, Integer messageType, String ip, Integer platform) {
        Integer type = LimitTypeEnum.EmailCode.getCode();
        Boolean checkResult = limitHelper.checkLimit(ip, type);
        ResultErrorCode errorCode = ResultErrorCode.EMAIL_LIMIT_BEYOND_ERROR;
        if (!checkResult) {
            return Result.failure(errorCode.getCode(), errorCode.getValue());
        }
        boolean mailValidate = validateHelper.mailCodeValidate(email, platform, messageType, code);
        if (!mailValidate) {
            limitHelper.deleteLimit(ip, type);
            return Result.success();
        }
        Integer limitCount = limitHelper.updateLimit(ip, type);
        if(limitCount > 0){
            errorCode = ResultErrorCode.EMAIL_LIMIT_COUNT_ERROR;
        }
        return Result.failure(errorCode.getCode(), String.format(errorCode.getValue(), limitCount), limitCount);
    }

    /**
     * 验证谷歌验证码是否正确
     *
     * @param secret 谷歌KEY
     * @param code   验证码
     * @param ip     IP地址
     * @return 服务端返回消息
     */
    public Result getGoogleCodeCheck(String secret, String code, String ip) {
        Integer type = LimitTypeEnum.GoogleCode.getCode();
        Boolean checkResult = limitHelper.checkLimit(ip, type);
        ResultErrorCode errorCode = ResultErrorCode.GOOGLE_LIMIT_BEYOND_ERROR;
        if (!checkResult) {
            return Result.failure(errorCode.getCode(), errorCode.getValue());
        }
        try {
            boolean googleAuth = GoogleAuth.auth(Long.parseLong(code), secret);
            if (googleAuth) {
                limitHelper.deleteLimit(ip, type);
                return Result.success();
            }
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Integer limitCount = limitHelper.updateLimit(ip, type);
        if(limitCount > 0){
            errorCode = ResultErrorCode.GOOGLE_LIMIT_COUNT_ERROR;
        }
        return Result.failure(errorCode.getCode(), String.format(errorCode.getValue(), limitCount), limitCount);
    }

    /**
     * 验证交易密码是否正确
     *
     * @param oldPassword 原密码
     * @param newPassword 输入密码
     * @param ip          IP地址
     * @return 服务端返回消息
     */
    public Result getTradePasswordCheck(String oldPassword, String newPassword, String ip) {
        Integer type = LimitTypeEnum.TradePassword.getCode();
        Boolean checkResult = limitHelper.checkLimit(ip, type);
        ResultErrorCode errorCode = ResultErrorCode.TRADE_LIMIT_BEYOND_ERROR;
        if (!checkResult) {
            return Result.failure(errorCode.getCode(), errorCode.getValue());
        }
        if (oldPassword.equals(newPassword)) {
            limitHelper.deleteLimit(ip, type);
            return Result.success();
        }
        Integer limitCount = limitHelper.updateLimit(ip, type);
        if(limitCount > 0){
            errorCode = ResultErrorCode.TRADE_LIMIT_COUNT_ERROR;
        }
        return Result.failure(errorCode.getCode(), String.format(errorCode.getValue(), limitCount), limitCount);
    }

    /**
     * 资金操作验证
     *
     * @param user          用户实体
     * @param phoneCode     手机验证码
     * @param messageType   消息类型
     * @param googleCode    谷歌验证码
     * @param tradePassword 交易密码
     * @param ip            IP地址
     * @param platform    平台
     * @return 服务端返回消息
     */
    public Result getCapitalCheck(FUser user, String phoneCode, Integer messageType, String googleCode
            , String tradePassword, String ip, Integer platform) {
        Result result = getTradePasswordCheck(user.getFtradepassword(),tradePassword,ip);
        if (!result.getSuccess()) {
            return result;
        }
        if (user.getFgooglebind()) {
            result = getGoogleCodeCheck(user.getFgoogleauthenticator(), googleCode, ip);
            if (!result.getSuccess()) {
                return result;
            }
        }
        if (user.getFistelephonebind()) {
            result = getPhoneCodeCheck(user.getFareacode(), user.getFtelephone(), phoneCode, messageType, ip, platform);
            if (!result.getSuccess()) {
                return result;
            }
        }
        return result;
    }

    /**
     * 变更操作验证
     *
     * @param user        用户实体
     * @param phoneCode   手机验证码
     * @param messageType 消息类型
     * @param googleCode  谷歌验证码
     * @param ip          IP地址
     * @param platform    平台
     * @return 服务端返回消息
     */
    public Result getChangeCheck(FUser user, String phoneCode, Integer messageType, String googleCode, String ip
            , Integer platform) {
        Result result = Result.success();
        if (user.getFgooglebind()) {
            result = getGoogleCodeCheck(user.getFgoogleauthenticator(), googleCode, ip);
            if (!result.getSuccess()) {
                return result;
            }
        }
        if (user.getFistelephonebind()) {
            result = getPhoneCodeCheck(user.getFareacode(), user.getFtelephone(), phoneCode, messageType, ip, platform);
            if (!result.getSuccess()) {
                return result;
            }
        }
        return result;
    }

        /**
     * 变更操作验证
     *
     * @param user        用户实体
     * @param phoneCode   手机验证码
     * @param messageType 消息类型
     * @param googleCode  谷歌验证码
     * @param ip          IP地址
     * @param platform    平台
     * @return 服务端返回消息
     */
    public Result getChangeCheckOnlyGoogle(FUser user, String phoneCode, Integer messageType, String googleCode, String ip
            , Integer platform) {
        Result result = Result.success();
        if (user.getFgooglebind()) {
            result = getGoogleCodeCheck(user.getFgoogleauthenticator(), googleCode, ip);
            if (!result.getSuccess()) {
                return result;
            }
        }
    //        if (user.getFistelephonebind()) {
    //            result = getPhoneCodeCheck(user.getFareacode(), user.getFtelephone(), phoneCode, messageType, ip, platform);
    //            if (!result.getSuccess()) {
    //                return result;
    //            }
    //        }
        return result;
    }
}
