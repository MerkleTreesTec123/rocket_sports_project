package com.qkwl.common.dto.Enum;

public enum FinanceRecordStatusEnum {

    SUCCESS(1, "成功"),
    Fail(2, "失败"),
    Processing(3,"处理中"),
    Cancel(4,"取消");

    private Integer code;

    private String value;

    FinanceRecordStatusEnum(Integer code, String value) {
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
        for (FinanceRecordStatusEnum e : FinanceRecordStatusEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue().toString();
            }
        }
        return "";
    }

}
