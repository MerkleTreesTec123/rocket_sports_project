package com.qkwl.common.dto.Enum;

public class LinkTypeEnum {
	public static final int LINK_VALUE = 1;
	public static final int QQ_VALUE = 2;

	public static String getEnumString(int value) {
		String name = "";
		if (value == LINK_VALUE) {
			name = "友情链接";
		} else if (value == QQ_VALUE) {
			name = "QQ群";
		}
		return name;
	}

}
