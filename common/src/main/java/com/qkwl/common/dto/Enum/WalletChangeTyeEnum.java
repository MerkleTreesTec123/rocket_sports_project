package com.qkwl.common.dto.Enum;

/**
 * 资金日志操作类型
 * @author tr
 * @date 17-4-22.
 */
public enum WalletChangeTyeEnum {

    RECHARGE(1, "充值"),
    WITHDRAW(2, "提现"),
    BUY(3, "买"),
    SELL(4, "卖"),
    ADMIN(5, "系统"),
    FINANCING(6, "理财"),
    ACTIVITY(7, "活动");

    private Integer code;
    private String value;

    WalletChangeTyeEnum(int code, String value) {
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
