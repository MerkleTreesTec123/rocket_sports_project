package com.qkwl.common.dto.Enum;

public class TradeRecordTypeEnum {
	public static final int CNY_RECHARGE = 1;// 人民币充值
	public static final int CNY_WITHDRAW = 2;// 人民币提现
	public static final int BTC_RECHARGE = 3;// 虚拟币充值
	public static final int BTC_WITHDRAW = 4;// 虚拟币提现
	public static final int BTC_BUY = 5;// 虚拟币买入
	public static final int BTC_SELL = 6;// 虚拟币卖出

	public static String getEnumString(int value) {
		String name = "";
		switch (value) {
		case CNY_RECHARGE:
			name = "CNY充值";
			break;
		case CNY_WITHDRAW:
			name = "CNY提现";
			break;
		case BTC_RECHARGE:
			name = "充值";
			break;
		case BTC_WITHDRAW:
			name = "提现";
			break;
		case BTC_BUY:
			name = "买入";
			break;
		case BTC_SELL:
			name = "卖出";
			break;
		}
		return name;
	}

}
