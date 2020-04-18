package com.qkwl.common.dto.Enum;

public enum MessageStatusEnum {

	NOREAD(1, "未读"),
	READ(2, "已读");

	private Integer code;
	private Object value;

	private MessageStatusEnum(Integer code, Object value) {
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
		for (EntrustSourceEnum entrustsource : EntrustSourceEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource.getValue().toString();
			}
		}
		return null;
	}
}
