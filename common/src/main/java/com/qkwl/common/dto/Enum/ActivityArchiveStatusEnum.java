package com.qkwl.common.dto.Enum;

/**
 * 活动归档状态枚举
 * Created by ZKF on 2017/8/26.
 */
public enum ActivityArchiveStatusEnum {

    NotArchive(0, "未归档"),
    Archive(1, "已归档");

    private Integer code;
    private String value;

    private ActivityArchiveStatusEnum(Integer code, String value) {
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
        for (ActivityArchiveStatusEnum e: ActivityArchiveStatusEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }

}
