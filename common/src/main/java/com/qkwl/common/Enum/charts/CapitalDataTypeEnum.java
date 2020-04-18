package com.qkwl.common.Enum.charts;

/**
 * Created by ZKF on 2017/7/26.
 */
public enum CapitalDataTypeEnum {

    VOLUME(1, "总量"),
    FEE(2, "手续费");

    private Integer code;
    private String value;

    CapitalDataTypeEnum(Integer code, String value) {
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

    public static String getValueByCode(Integer code){
        for (CapitalDataTypeEnum e : CapitalDataTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
            }
        }
        return null;
    }
}
