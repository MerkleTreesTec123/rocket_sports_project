package com.qkwl.common.Enum.validate;

/**
 * 模板业务类型枚举
 */
public enum BusinessTypeEnum {

    /**
     * 短信
     */
    SMS_APPLY_API(101, "API申请"),
    SMS_BING_MOBILE(102, "绑定手机"),
    SMS_UNBIND_MOBILE(103, "解绑手机"),
    SMS_CNY_WITHDRAW(104, "人民币提现"),
    SMS_COIN_WITHDRAW(105, "虚拟币提现"),
    SMS_MODIFY_LOGIN_PASSWORD(106, "修改登录密码"),
    SMS_MODIFY_TRADE_PASSWORD(107, "修改交易密码"),
    SMS_COIN_WITHDRAW_ACCOUNT(108, "虚拟币提现地址设置"),
    SMS_FIND_PHONE_PASSWORD(109, "找回登录密码-手机找回"),
    SMS_CNY_ACCOUNT_WITHDRAW(110, "设置人民币提现账号"),
    SMS_WEB_REGISTER(111, "网页端注册帐号"),
    SMS_APP_REGISTER(112, "手机端注册帐号"),
    SMS_PUSHASSET(113, "PUSH资产"),
    SMS_FINANCES(114, "存币理财"),
    SMS_TRANSFER(115, "资产转移"),
    SMS_PRICE_CLOCK(116, "价格闹钟"),
    SMS_NEW_IP_LOGIN(117, "新IP登陆"),
    SMS_CNY_RECHARGE(118, "人民币充值"),
    SMS_RISKMANAGE(119, "风控短信"),
    SMS_FIND_EMAIL_PASSWORD(120, "找回登录密码-邮箱找回"),
    SMS_MODIFY_LOGIN_REMIND(121, "修改登录密码-短信提醒"),
    SMS_MODIFY_TRADE_REMIND(122, "修改交易密码-短信提醒"),
    SMS_AUDIT_RISKMANAGE(123, "审核风控短信"),

    /**
     * 邮件
     */
    EMAIL_VALIDATE_BING(201, "绑定邮件"),
    EMAIL_FIND_PASSWORD(202, "找回登录密码"),
    EMAIL_REGISTER_CODE(203, "注册验证码"),
    EMAIL_PRICE_CLOCK(204, "价格闹钟"),
    EMAIL_NEW_IP_LOGIN(205, "登录IP异常"),
    FINANCE_BALANCE(206, "对账邮件"),
    EMAIL_APP_BIND(207,"手机端绑定邮件");

    private Integer code;
    private String value;

    BusinessTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(Integer code) {
        for (BusinessTypeEnum e : BusinessTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
