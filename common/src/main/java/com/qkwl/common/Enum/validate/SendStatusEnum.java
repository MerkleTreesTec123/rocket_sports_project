package com.qkwl.common.Enum.validate;

/**
 * 发送状态枚举
 */
public enum SendStatusEnum {

    SEND_NOT(1, "未发送"),
    SEND_SUCCESS(2,"发送成功"),
    SEND_FAILURE(3,"发送失败");

    private Integer code;
    private String value;


    SendStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(Integer code) {
        for (SendStatusEnum e : SendStatusEnum.values()) {
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
