package com.qkwl.common.dto.Enum;

public class CapitalOperationTypeEnum {
	
	public static final int RMB_IN = 1;
	public static final int RMB_OUT = 2;
	public static final int OLRMB_INT = 3 ;
	public static final int OLRMB_OUT = 4 ;
	public static final int ALIPAY_INT = 5 ;
	public static final int WECHAT_INT = 6 ;
	public static final int ALIPAY_OUT = 7;
	public static final int WECHAT_OUT = 8;
	public static final int ZSJF_IN = 9;
	public static final int ZSJF_OUT = 10;


	public static String getEnumString(int value) {
		String name = "";
		if (value == RMB_IN) {
			name = "人民币充值";
		} else if (value == RMB_OUT) {
			name = "人民币提现";
		}else if(value == OLRMB_INT){
			name = "人民币在线充值";
		}else if(value == OLRMB_OUT){
			name = "人民币在线提现";
		}else if(value == ALIPAY_INT){
			name = "支付宝转账";
		}else if(value == WECHAT_INT){
			name = "微信转账";
		}else if(value == ALIPAY_OUT){
			name = "支付宝提现";
		}else if(value == WECHAT_OUT){
			name = "微信提现";
		}else if(value == ZSJF_IN){
			name = "质数金服充值";
		}else if(value == ZSJF_OUT){
			name = "质数金服提现";
		}
		return name;
	}
}
