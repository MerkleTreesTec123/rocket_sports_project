package com.qkwl.common.Enum.validate;

/**
 * 模板类型枚举
 * Created by ZKF on 2017/5/4.
 */
public enum TemplateTypeEnum {

    VALIDATE(1, "验证类型"),
    NOTICE(2, "通知类型");

    private Integer code;
    private String value;


    TemplateTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(Integer code) {
        for (TemplateTypeEnum e : TemplateTypeEnum.values()) {
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
