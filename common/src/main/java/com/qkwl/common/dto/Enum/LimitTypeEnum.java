package com.qkwl.common.dto.Enum;

/**
 * 访问限制类型
 * Created by ZKF on 2017/7/5.
 */
public enum LimitTypeEnum {

    LoginPassword(1, "登录密码"),
    TradePassword(2, "交易密码"),
    GoogleCode(3, "谷歌验证码"),
    PhoneCode(4, "手机验证码"),
    EmailCode(5, "邮箱验证码"),
    AdminLogin(6,"后台登陆"),
    RealName(7,"实名认证");

    private Integer code;
    private String value;

    private LimitTypeEnum(int code, String value) {
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

    public static String getValueByCode(Integer code) {
        for (LimitTypeEnum e : LimitTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue().toString();
            }
        }
        return null;
    }

}
