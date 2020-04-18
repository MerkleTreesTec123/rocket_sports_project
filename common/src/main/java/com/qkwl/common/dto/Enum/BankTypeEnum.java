package com.qkwl.common.dto.Enum;

/**
 * 银行类型枚举
 * @author ZKF
 */
public enum BankTypeEnum {

	Transfer(0, "网银转账"),
	Alipay(1, "支付宝"),
	Recharge(2, "网银直充"),
	ZsjfOnLine(5, "质数金服");

	private Integer code;
	private Object value;

	private BankTypeEnum(Integer code, Object value) {
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

	public static String getTypeValueByCode(Integer code) {
		for (BankTypeEnum e : BankTypeEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getValue().toString();
			}
		}
		return null;
	}
}
