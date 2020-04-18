package com.qkwl.common.util;

/**
 * 积分常量
 * @author TT
 */
public class ScoreConstant {

	/**
	 * 登录
	 */
	public static int LOGIN = 100;

	/**
	 * 实名认证
	 */
	public static int REAL_NAME = 500;
	
	/**
	 * 手机认证
	 */
	public static int AUTH_PHONE = 500;
	
	/**
	 * 邮箱认证
	 */
	public static int AUTH_MAIL = 500;
	
	/**
	 * 谷歌认证
	 */
	public static int AUTH_GOOGLE = 500;
	
	/**
	 * 首充
	 */
	public static int FIRST_RECHARGE = 500;
	
	/**
	 * 充值 (充值10元或价值10元的BTC/LTC/ETC)
	 */
	public static int RECHARGE = 1;
	
	/**
	 * 交易 (买入或卖出100元的BTC/LTC/ETC)
	 */
	public static int TRADE = 1;
	
	/**
	 * 交易最大 (每天最多获得10000积分)
	 */
	public static int TRADE_MAX = 10000;

	/**
	 * 净资产额 (当日账户净资产折合人民币)
	 */
	public static double NET_ASSET = 0.001;
}
