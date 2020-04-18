package com.qkwl.common.dto.Enum;

public class VirtualCapitalOperationInStatusEnum {
	public static final int WAIT_0 = 0;
	public static final int WAIT_1 = 1;
	public static final int WAIT_2 = 2;
	public static final int SUCCESS = 3;

	public static String getEnumString(int value) {
		String name = "";
		if (value == WAIT_0) {
			name = "0/项确认";
		} else if (value == WAIT_1) {
			name = "1/项确认";
		} else if (value == WAIT_2) {
			name = "2/项确认";
		} else if (value == SUCCESS) {
			name = "充值成功";
		}
		return name;
	}
}
