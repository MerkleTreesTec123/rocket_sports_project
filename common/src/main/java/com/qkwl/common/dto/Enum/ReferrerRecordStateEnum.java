package com.qkwl.common.dto.Enum;

/**
 * PUSH资产状态
 *
 */
public enum ReferrerRecordStateEnum {
	Non_Release(1, "未释放"),
	Released(2, "已释放");

	private Integer code;
	private Object value;

	private ReferrerRecordStateEnum(Integer code, Object value) {
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

	public static String getUserPushStateByCode(Integer code) {
		for (ReferrerRecordStateEnum userPushState : ReferrerRecordStateEnum.values()) {
			if (userPushState.getCode().equals(code)) {
				return userPushState.getValue().toString();
			}
		}
		return null;
	}
}
