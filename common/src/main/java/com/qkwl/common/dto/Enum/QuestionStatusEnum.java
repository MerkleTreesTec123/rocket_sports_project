package com.qkwl.common.dto.Enum;

public enum QuestionStatusEnum {
	
	NOT_SOLVED(1, "未解决"),
	SOLVED(2, "解决"),
	DEL(3, "删除");
	
	private Integer code;
	private Object value;

	private QuestionStatusEnum(Integer code, Object value) {
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
		for (QuestionStatusEnum entrustsource : QuestionStatusEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource.getValue().toString();
			}
		}
		return null;
	}

}
