package com.qkwl.common.dto.Enum;

public class QuestionIsAnswerEnum {
	
	public static final int NOT = 0;// 未追问
	public static final int YES = 1;// 已追问

	public static String getEnumString(int value) {
		String str = "";
		if (value == NOT) {
			str = "未追问";
		} else if (value == YES) {
			str = "已追问";
		}

		return str;
	}
}
