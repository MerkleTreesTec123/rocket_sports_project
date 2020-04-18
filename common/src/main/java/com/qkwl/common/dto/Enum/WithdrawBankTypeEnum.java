package com.qkwl.common.dto.Enum;

/**
 * 提现银行类型枚举
 * @author ZKF
 */
public enum WithdrawBankTypeEnum {

	Bank(0, "网银转账"),
	Alipay(1, "支付宝");

	private Integer code;
	private Object value;

	private WithdrawBankTypeEnum(Integer code, Object value) {
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
}
