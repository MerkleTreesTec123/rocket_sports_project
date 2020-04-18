package com.qkwl.common.dto.Enum;

/**
 * 充值匹配状态枚举
 * Created by ZKF on 2017/6/17.
 */
public enum RechargeMatchStatusEnum {


    WAIT(1, "待审核"),
    AUDITED(2, "已审核"),
    FAILED(3, "已失效"),
    NOMATCH(4, "无法匹配"),
    IDNULL(5, "UID异常")
    ;

    private Integer code;
    private Object value;

    private RechargeMatchStatusEnum(Integer code, Object value) {
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

    public static String getValueByCode(Integer code) {
        for (RechargeMatchStatusEnum  e: RechargeMatchStatusEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue().toString();
            }
        }
        return null;
    }
}
