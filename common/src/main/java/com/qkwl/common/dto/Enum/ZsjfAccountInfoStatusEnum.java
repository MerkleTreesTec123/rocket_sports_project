package com.qkwl.common.dto.Enum;

/**
 * Created by wangchen on 2017-08-14.
 */
public enum ZsjfAccountInfoStatusEnum {

    Enable(1, "启用"),
    Disable(2, "禁用");

    private Integer code;
    private Object value;

    private ZsjfAccountInfoStatusEnum(Integer code, Object value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public static String getAccountInfoStatusByCode(Integer code) {
        for (ZsjfAccountInfoStatusEnum item : ZsjfAccountInfoStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item.getValue().toString();
            }
        }
        return null;
    }
}
