package com.qkwl.common.dto.Enum;

public class IdentityTypeEnum {// 身份证件类型
	public static final int IDCARD = 0;// 身份证
	public static final int PASSPORT = 1;// 护照
	public static final int PASS = 2;// 通行证
	

	public static String getEnumString(int value) {
		String name = "";
		switch (value) {
		case IDCARD:
			name = "身份证";
			break;
		case PASSPORT:
			name = "护照";
			break;
		case PASS:
			name = "通行证";
			break;
		}
		return name;
	}

}
