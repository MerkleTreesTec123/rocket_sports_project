package com.qkwl.common.dto.Enum;

/**
 * 分享送币活动状态
 */
public enum ShareCoinTypeStatusEnum {

    Normal(1, "正常"),
    Activate(2, "当前活动中"),
    Forbid(3, "禁用");

    private Integer code;
    private String value;

    private ShareCoinTypeStatusEnum(Integer code, String value) {
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
        for (ShareCoinTypeStatusEnum item : ShareCoinTypeStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item.getValue();
            }
        }
        return null;
    }

}
