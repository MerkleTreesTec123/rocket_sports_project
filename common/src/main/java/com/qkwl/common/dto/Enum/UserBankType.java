package com.qkwl.common.dto.Enum;

/**
 * 体现卡的类型
 */
public class UserBankType {
	public static final int Bank = 0;		// 银行卡
	public static final int Alipay = 1;		// 支付宝
	public static final int Wechatpay = 2;	// 微信

	public static String getEnumString(int value) {
		String name = "";
		if (value == Bank) {
			name = "银行卡";
		} else if (value == Alipay) {
			name = "支付宝";
		} else if (value == Wechatpay) {
			name = "微信";
		}
		return name;
	}
}
