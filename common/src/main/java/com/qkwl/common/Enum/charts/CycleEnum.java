package com.qkwl.common.Enum.charts;

/**
 * 周期枚举
 * Created by ZKF on 2017/7/25.
 */
public enum CycleEnum {

    DAY(1, "天"),
    WEEK(2, "周"),
    MONTH(3, "月");

    private Integer code;
    private String value;

    CycleEnum(Integer code, String value) {
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
