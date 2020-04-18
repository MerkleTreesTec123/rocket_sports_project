package com.qkwl.common.dto.Enum;

public enum FinanceRecordOperationEnum {

    In(1, "充值"),
    Out(2, "提现"),
    Transfer(3,"转账"), // 以前是转账，现在改为 CNY 提现
    Receive(4,"接受转账"), // 以前是接受转账，现在改为 CNY 充值
    InnerTransferOut(7,"划转到C2C账户"),
    InnerTransferIn(8,"划转到币币账户"),
    Exchange(9,"8折兑换"),
    PoolOut(10,"矿池投入"),
    PoolIn(11,"矿池收益"),
    PoolRelease(12,"矿池释放"),
    InviteFrozen(13,"直推收益冻结"),
    TeamReward(14,"团队奖励"),
    Other(6,"其他");
            
    private Integer code;

    private String value;

    FinanceRecordOperationEnum(Integer code, String value) {
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
        for (FinanceRecordOperationEnum e : FinanceRecordOperationEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue().toString();
            }
        }
        return "";
    }

}
