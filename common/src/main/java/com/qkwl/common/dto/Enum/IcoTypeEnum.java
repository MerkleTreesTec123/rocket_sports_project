package com.qkwl.common.dto.Enum;

/**
 * 众筹类型
 * Created by ZKF on 2017/4/27.
 */
public enum IcoTypeEnum {

    NORMAL(1, "普通ICO"),
    PRODUCT(2, "产品ICO");

    private Integer code;
    private String value;

    private IcoTypeEnum(Integer code, String value) {
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
        for (IcoTypeEnum  e: IcoTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue().toString();
            }
        }
        return null;
    }
}
