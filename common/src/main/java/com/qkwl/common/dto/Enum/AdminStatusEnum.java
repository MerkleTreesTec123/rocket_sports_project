package com.qkwl.common.dto.Enum;

public class AdminStatusEnum {
	public static final int NORMAL_VALUE = 1;// 正常
	public static final int FORBBIN_VALUE = 2;// 禁用

	public static String getEnumString(int value) {
		String name = "";
		if (value == NORMAL_VALUE) {
			name = "正常";
		} else if (value == FORBBIN_VALUE) {
			name = "禁用";
		}
		return name;
	}

}
