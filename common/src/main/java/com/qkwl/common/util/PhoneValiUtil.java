package com.qkwl.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValiUtil {
	
	/**
	 * 手机号验证
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String mobile) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(mobile);
		b = m.matches();
		return b;
	}

	/**
	 * 验证电话号码是否有效 true：有效 false：无效
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone) {
		String regExp = "^(([1][3,4,5,7,8][0-9]{9})|([0]{1}[0-9]{2,3}-[0-9]{7,8}))$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = null;
		boolean b = false;
		m = p.matcher(phone);
		b = m.matches();
		return b;
	}

	public static void main(String[] args) {
		boolean isPhone = isMobile("17052222222");
		System.out.println(isPhone);
	}
}
