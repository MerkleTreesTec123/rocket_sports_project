package com.qkwl.common.dto.Enum;

/**
 * PUSH资产状态
 * 
 * @author LY
 *
 */
public enum UserPushStateEnum {
	PAYWAIT(1, "等待付款"), PAYSUCCEED(2, "完成"), PAYCANCEL(3, "取消");

	private Integer code;
	private Object value;

	private UserPushStateEnum(Integer code, Object value) {
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
		for (UserPushStateEnum userPushState : UserPushStateEnum.values()) {
			if (userPushState.getCode().equals(code)) {
				return userPushState.getValue().toString();
			}
		}
		return null;
	}
}
