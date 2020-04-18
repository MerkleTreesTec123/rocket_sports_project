package com.qkwl.common.dto.Enum;

/**
 * 众筹流水类型
 * Created by ZKF on 2017/4/27.
 */
public enum IcoRecordTypeEnum {

    ICO(1, "众筹流水"),
    SALE(2, "打折流水"),
    REWARD(3, "奖励流水");

    private Integer code;
    private String value;

    private IcoRecordTypeEnum(Integer code, String value) {
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
        for (IcoRecordTypeEnum e: IcoRecordTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue().toString();
            }
        }
        return null;
    }
}
