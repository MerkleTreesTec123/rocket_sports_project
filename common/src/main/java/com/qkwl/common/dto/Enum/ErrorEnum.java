package com.qkwl.common.dto.Enum;

public enum ErrorEnum {
	SUCCEED(0),			// 成功
	PRM_ERR(1),			// 参数错误
	USER_NULL(2);		// 用户为空
	
	private int value = 0;
	
	private ErrorEnum(int value) {
		this.value = value;
	}
	
	public int value() {
		return this.value;
	}
	
	public static ErrorEnum valueOf(int value) {
		switch (value) {
		case 0:
			return SUCCEED;
		case 1:
			return PRM_ERR;
		case 2:
			return USER_NULL;
		default:
			return null;
		}
	}
}
