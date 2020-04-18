package com.qkwl.common.result;

/**
 * Created by ZKF on 2017/4/19.
 */
public enum ResultErrorCode {

    USER_IDENTITYAUTH(10001, "请先完成实名认证！"),
    USER_BIND_PHONEANDGOOGLE(10002, "请先绑定手机或谷歌验证器！"),
    TRADEPASSWORD_SETTING(10003, "请先设置交易密码"),
    PHONE_LIMIT_BEYOND_ERROR(10004,"手机验证码错误多次，请2小时后再试！"),
    PHONE_LIMIT_COUNT_ERROR(10005,"手机验证码错误！您还有%d次机会！"),
    GOOGLE_LIMIT_BEYOND_ERROR(10006,"谷歌验证码错误多次，请2小时后再试！"),
    GOOGLE_LIMIT_COUNT_ERROR(10007,"谷歌验证码错误！您还有%d次机会！"),
    TRADE_LIMIT_BEYOND_ERROR(10008,"交易密码错误多次，请2小时后再试！"),
    TRADE_LIMIT_COUNT_ERROR(10009,"交易密码错误！您还有%d次机会！"),
    TRADEPASSWORD_AVAILABLE(10010, "交易密码修改后24小时内暂停提现！"),
    USER_FORBIDDEN(10011, "账号异常被冻结，如有疑问请联系客服！"),
    USER_FORBIDDEN_CNY(10012, "账号资金操作已冻结，如有疑问请联系客服！"),
    USER_FORBIDDEN_COIN(10013, "账号虚拟币操作已冻结，如有疑问请联系客服！"),
    EMAIL_LIMIT_BEYOND_ERROR(10014,"邮箱验证码错误多次，请2小时后再试！"),
    EMAIL_LIMIT_COUNT_ERROR(10015,"邮箱验证码错误！您还有%d次机会！"),

    PARAM_ERROR(10400,"参数错误");

    private Integer code;
    private String value;

    private ResultErrorCode(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
