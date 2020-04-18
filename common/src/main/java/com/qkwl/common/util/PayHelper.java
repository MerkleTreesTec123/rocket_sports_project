package com.qkwl.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class PayHelper {
	
	/**
	 * 获取交易流水号（提交的时间+数字随机数）
	 * @return
	 */
	public static String getOrderID() {
		// 构造订单号 (形如:20101201102322159111111)
		StringBuffer tradeNoBuffer = new StringBuffer();
		tradeNoBuffer.append(getDateTimeLongString());
		tradeNoBuffer.append(createRandom(8, 1, 0, 0, 0, ""));

		return tradeNoBuffer.toString();
	}

	/**
	 * 获取交易流水号（前缀+提交的时间+随机数）
	 * @param prefix 订单标志字母
	 * @return
	 */
	public static String getOrderIDByPrefix(String prefix) {
		return getOrderIDByPrefix(prefix, 32);
	}

	/**
	 * 获取交易流水号（前缀+提交的时间+随机数）
	 * @param prefix 订单标志字母
	 * @param orderIDLength 订单长度
	 * @return
	 */
	public static String getOrderIDByPrefix(String prefix, int orderIDLength) {
		// 构造订单号 (形如:20101201102322159111111)
		// 随机数的长度
		int randomLength = 6;
		StringBuffer tradeNoBuffer = new StringBuffer();
		tradeNoBuffer.append(prefix);
		tradeNoBuffer.append(getDateTimeLongString());// 货到当前时间
		// 如果充值缓冲的长度加随机数的长度大于构造订单长度，就从新设置随机数的长度
		if ((tradeNoBuffer.length() + randomLength) > orderIDLength) {
			randomLength = orderIDLength - tradeNoBuffer.length();
		}
		tradeNoBuffer.append(createRandom(randomLength, 1, 0, 0, 0, ""));

		return tradeNoBuffer.toString();
	}

	/**
	 * 获取长日期字符串表示 yyyyMMddHHmmss000
	 * @return
	 */
	public static String getDateTimeLongString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return (sdf.format(new Date()));
	}

	/**
	 * 获取长日期字符串表示 yyyyMMddHHmmss000,可以添加前缀
	 * @param prefix
	 * @return
	 */
	public static String getDateTimeLongString(String prefix) {
		if (prefix == null || prefix.isEmpty()) {
			prefix = "";
		}
		return (prefix + getDateTimeLongString());
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length 目标字符串的长度
	 * @param useNum 是否包含数字，1=包含，默认为包含
	 * @param useLow 是否包含小写字母，1=包含，默认为包含
	 * @param useUpp 是否包含大写字母，1=包含，默认为包含
	 * @param useSpe 是否包含特殊字符，1=包含，默认为不包含
	 * @param custom 要包含的自定义字符，直接输入要包含的字符列表
	 * @return 指定长度的随机字符串
	 */
	public static String createRandom(int length, int useNum, int useLow, int useUpp, int useSpe, String custom) {
		Random random = new Random();
		String str = "";
		String str2 = custom;
		if (useNum == 1) {
			str2 = str2 + "0123456789";
		}
		if (useLow == 1) {
			str2 = str2 + "abcdefghijklmnopqrstuvwxyz";
		}
		if (useUpp == 1) {
			str2 = str2 + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		}
		if (useSpe == 1) {
			str2 = str2 + "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
		}
		int randomLength=str2.length();
		for (int i = 0; i < length; i++) {
			str += str2.charAt(random.nextInt(randomLength));
		}
		return str;
	}
}
