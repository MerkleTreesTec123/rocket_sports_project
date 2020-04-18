package com.qkwl.common.framework.pre;

import com.qkwl.common.dto.Enum.UserStatusEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.result.Result;
import com.qkwl.common.result.ResultErrorCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class PreValidationHelper {

    /**
     * 验证用户禁用状态
     * true:禁用，false:正常
     */
    public Boolean validateUserStatus(FUser user) {
        return user.getFstatus() == UserStatusEnum.FORBBIN_VALUE;
    }

    /**
     * 验证交易密码修改后可用性
     * true:不可用，false:可用
     */
    public Boolean validateUserTradePasswordIsAvailable(FUser user) {
        return user.getFtradepwdtime() != null && ((new Date().getTime() - user.getFtradepwdtime().getTime())/1000) <= 24 * 60 * 60;
    }

    /**
     * 验证手机或谷歌是否都未绑定
     * true:未绑定，false:已存在绑定
     */
    public Boolean validatePhoneBindAndGoogleBind(FUser user) {
        return !user.getFistelephonebind() && !user.getFgooglebind();
    }

    /**
     * 验证用户是否实名认证
     * true:未实名，false:已实名
     */
    public Boolean validateUserIdentityAuth(FUser user) {
        return !user.getFhasrealvalidate();
    }

    /**
     * 验证交易密码是否设置
     * true:未设置，false:已设置
     */
    public Boolean validateUserTradePasswordIsSetting(FUser user) {
        return StringUtils.isEmpty(user.getFtradepassword());
    }

    /**
     * 验证用户禁止提现
     */
    public Boolean validateUserWithdrawCny(FUser user) {
        return user.getFiscny() == UserStatusEnum.FORBBIN_VALUE;
    }

    /**
     * 验证用户禁止提币Coin
     */
    public Boolean validateUserWithdraw(FUser user) {
        return user.getFiscoin() == UserStatusEnum.FORBBIN_VALUE;
    }

    /**
     * 资金操作验证
     */
    public Result validateCapital(FUser user, String type) {
        if(validatePhoneBindAndGoogleBind(user)){
            return Result.failure(ResultErrorCode.USER_BIND_PHONEANDGOOGLE.getCode(), ResultErrorCode.USER_BIND_PHONEANDGOOGLE.getValue());
        }

        if(validateUserTradePasswordIsSetting(user)){
            return Result.failure(ResultErrorCode.TRADEPASSWORD_SETTING.getCode(), ResultErrorCode.TRADEPASSWORD_SETTING.getValue());
        }

        if(validateUserIdentityAuth(user)){
            return Result.failure(ResultErrorCode.USER_IDENTITYAUTH.getCode(), ResultErrorCode.USER_IDENTITYAUTH.getValue());
        }

        if(validateUserTradePasswordIsAvailable(user)){
            return Result.failure(ResultErrorCode.TRADEPASSWORD_AVAILABLE.getCode(), ResultErrorCode.TRADEPASSWORD_AVAILABLE.getValue());
        }

        if(type.equals("coin")){
            if(validateUserWithdraw(user)){
                return Result.failure(ResultErrorCode.USER_FORBIDDEN_COIN.getCode(), ResultErrorCode.USER_FORBIDDEN_COIN.getValue());
            }
        } else if(type.equals("cny")){
            if(validateUserWithdrawCny(user)){
                return Result.failure(ResultErrorCode.USER_FORBIDDEN_CNY.getCode(), ResultErrorCode.USER_FORBIDDEN_CNY.getValue());
            }
        }

        return Result.success();
    }
}
