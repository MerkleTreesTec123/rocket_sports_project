package com.qkwl.common.dto.Enum;

public class RemittanceTypeEnum {// 汇款方式：柜台、网银、atm
	public static final int Type1 = 1;// 柜台 转款
	public static final int Type2 = 2;// ATM机
	public static final int Type3 = 3;// 网上银行

	public static String getEnumString(int value) {
		String name = "";
		if (value == Type1) {
			name = "柜台转款";
		} else if (value == Type2) {
			name = "ATM机";
		} else if (value == Type3) {
			name = "网上银行";
		}
		return name;
	}
}
