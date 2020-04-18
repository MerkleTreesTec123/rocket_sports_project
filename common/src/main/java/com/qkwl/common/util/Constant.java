package com.qkwl.common.util;

import java.math.BigDecimal;

public class Constant {
	
	public static String GoogleAuthName = "Rocket";//谷歌验证前缀

	public static String AesSecretKey="W0TJRNJ2@tYgSrtXy30BMU1XPc4Y1YM6bwxOhR2H";
	
	/*
	 * 分页数量
	 */
	public static int RewordCodePerPage=10;//激活码兑换记录分页
	public static int CapitalRecordPerPage = 10;// 充值记录分页
	public static int CapitalWithdrawPerPage = 12;// 充值记录分页
	public static int QuestionRecordPerPage = 10;// 问题记录分页
	public static int EntrustRecordPerPage = 10;// 委托分页
	public static int TradeRecordPerPage = 5;// 账单明细分页
	public static int VirtualCoinWithdrawTimes = 10;// 虚拟币每天提现次数
	public static int CnyWithdrawTimes = 10;// 人民币每天提现次数
	public static int webPageSize = 10; //web 分页显示条数
	public static int appPageSize = 5; //app 分页显示条数
	public static int appIcoRecordPageSize = 10; //app ICO分页显示条数
	public static int apiPageSize = 50; //app 分页显示条数
	
	public static int apinum = 1;//api申请上限
	public static int apilimit = 60;//api访问上限
	public static int adminPageSize=40;//后台每页容量

	public static String EmailReg = "^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$";// 邮箱正则
	public static String PhoneReg = "^((1[0-9]{2})|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$";
	public static String passwordReg = "^(?![0-9]+$)(?![a-zA-Z]+$)[\\S]{6,}$";//必须包含字母和数字

	public static int ErrorCountLimit = 10;// 错误N次之后需要等待2小时才能重试
	public static int ErrorCountAdminLimit = 30;// 后台登录错误
	
	public static int MaxApiDepth = 10; //最大api市场深度
	public static int LastApiSuccess = 600; //api最新成交记录数
	
	public static int EXPIRETIME = 2 * 60 * 60;
	
	//每众筹一个btc对应的代币数量
	public static int ICO_GAIN = 1000;

	//每众筹一个btc对应的积分
	public static int ICO_SCORE = 1000;
	
	//众筹最小额度
	public static BigDecimal ICO_MIN = new BigDecimal(0.1);
	
	
	/**
	 * 找回密码，redis过期时间
	 */
	public static int RESTPASSEXPIRETIME = 30 * 60;
	
	/**
	 * 图片验证码，redis过期时间
	 */
	public static int IMAGESEXPIRETIME = 30 * 60;
	
	/**
	 * 访问限制时间，redis过期时间
	 */
	public static int limitTime = 60 * 60 * 1;
	
	/**
	 * BTC网络手续费
	 */
	public static final BigDecimal[] BTC_FEES = {new BigDecimal("0.0001"),new BigDecimal("0.0002") , new BigDecimal("0.0003"),
			new BigDecimal("0.0004"), new BigDecimal("0.0005"), new BigDecimal("0.0006"), new BigDecimal("0.0007"),
			new BigDecimal("0.0008"), new BigDecimal("0.0009"), new BigDecimal("0.0010")};

	/**
	 * 
	 */
	public static final int BTC_FEES_MAX = 10;
	
	/**
	 * 活动开始时间
	 */
	public static final String ACTIVITYSTART = "2017-01-23";
	/**
	 * 活动结束时间
	 */
	public static final String ACTIVITYEND = "2017-02-02";

	/**
	 * VIP等级
	 */
	public static final Integer VIP_LEVEL[] = { 0, 1, 2, 3, 4, 5, 6 };
}
