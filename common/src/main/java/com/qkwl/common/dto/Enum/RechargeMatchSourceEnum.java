package com.qkwl.common.dto.Enum;

/**
 * 充值匹配来源枚举
 * Created by ZKF on 2017/6/17.
 */
public enum RechargeMatchSourceEnum {

    PrivateAlipay(1, "个人支付宝"),
    PublicAlipay(2, "企业支付宝"),
    CMB(3, "招商银行"),
    CCB (4,	"建设银行")
   ;

    private Integer code;
    private Object value;

    RechargeMatchSourceEnum(Integer code, Object value) {
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
        for (RechargeMatchSourceEnum  e: RechargeMatchSourceEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue().toString();
            }
        }
        return null;
    }

    public static Integer getCodeByValue(String value) {
        for (RechargeMatchSourceEnum  e: RechargeMatchSourceEnum.values()) {
            if (value.equals(e.getValue())) {
                return e.getCode();
            }
        }
        return null;
    }
}
