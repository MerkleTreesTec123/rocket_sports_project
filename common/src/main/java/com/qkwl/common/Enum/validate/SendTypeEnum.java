package com.qkwl.common.Enum.validate;

/**
 * 发送类型枚举
 * Created by ZKF on 2017/5/4.
 */
public enum SendTypeEnum {

    SMS_TEXT(1, "普通短信"),
    SMS_VOICE(2,"语音短信"),
    SMS_INTERNATIONAL(3,"国际短信"),
    EMAIL(4,"邮件");

    private Integer code;
    private String value;


    SendTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(Integer code) {
        for (SendTypeEnum  e: SendTypeEnum.values()) {
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
