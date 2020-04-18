package com.qkwl.common.dto.Enum;

/**
 * 活动状态枚举
 * Created by ZKF on 2017/8/26.
 */
public enum ActivityStatusEnum {

    NotStart(0, "未开始"),
    Start(1, "进行中"),
    End(2, "已结束");

    private Integer code;
    private String value;

    private ActivityStatusEnum(Integer code, String value) {
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

    public static String getValueByCode(String type){
        for (ActivityStatusEnum e: ActivityStatusEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }

}
