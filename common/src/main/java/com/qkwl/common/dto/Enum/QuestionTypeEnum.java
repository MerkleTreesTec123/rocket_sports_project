package com.qkwl.common.dto.Enum;

public enum QuestionTypeEnum {
	
	RechargeWithdraw(1, "充值/提现问题"),
	Bussiness(2, "业务问题"),
	Bind(3,	"绑定问题"),
	Other(4, "其他问题"),
	Activity(5, "活动相关");

	private Integer code;
	private Object value;

	private QuestionTypeEnum(Integer code, Object value) {
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
		for (QuestionTypeEnum entrustsource : QuestionTypeEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource.getValue().toString();
			}
		}
		return null;
	}
}
