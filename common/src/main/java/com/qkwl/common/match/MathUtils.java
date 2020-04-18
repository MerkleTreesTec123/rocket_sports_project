package com.qkwl.common.match;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 精度计算器
 * 
 * @author ZKF
 */
public class MathUtils {

	/**
	 * 默认加减乘除运算精度
	 */
	public static final int DEF_DIV_SCALE = 10;
	/**
	 * 人民币显示小数位
	 */
	public static final int DEF_CNY_SCALE = 8;
	/**
	 * 虚拟币显示小数位
	 */
	public static final int DEF_COIN_SCALE = 4;
	/**
	 * 手续费显示小数位
	 */
	public static final int DEF_FEE_SCALE = 4;
	/**
	 * 人民币输入小数位
	 */
	public static final int ENTER_CNY_SCALE = 2;
	/**
	 * 虚拟币输入小数位
	 */
	public static final int ENTER_COIN_SCALE = 4;
	/**
	 * 其它小数位
	 */
	public static final int OTHER_SCALE = 2;
	/**
	 * 整型小数位
	 */
	public static final int INTEGER_SCALE = 0;

	/**
	 * 加法
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static BigDecimal add(BigDecimal a, BigDecimal b) {
		return a.add(b).setScale(DEF_DIV_SCALE, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 减法
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static BigDecimal sub(BigDecimal a, BigDecimal b) {
		return a.subtract(b).setScale(DEF_DIV_SCALE, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 乘法
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static BigDecimal mul(BigDecimal a, BigDecimal b) {
		return a.multiply(b).setScale(DEF_DIV_SCALE, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 除法
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static BigDecimal div(BigDecimal a, BigDecimal b) {
		if (a.compareTo(BigDecimal.ZERO) == 0 || b.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		return a.divide(b, DEF_DIV_SCALE, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 将一个数转负数
	 * @param a	 需要转的数
	 * @return
	 */
	public static BigDecimal positive2Negative(BigDecimal a) {
		if (a.compareTo(BigDecimal.ZERO) == -1) {
			return a;
		}
		return a.multiply(new BigDecimal(-1));
	}

	/**
	 * 将一个数转正数
	 * @param a	 需要转的数
	 * @return
	 */
	public static BigDecimal negative2Positive(BigDecimal a) {
		if (a.compareTo(BigDecimal.ZERO) == 1) {
			return a;
		}
		return a.multiply(new BigDecimal(-1));
	}

	/**
	 * 比较
	 * 
	 * @param a
	 * @param b
	 * @return 1(大于) 0(等于) -1(小于)
	 */
	public static int compareTo(BigDecimal a, BigDecimal b) {
		return a.compareTo(b);
	}

	/**
	 * 自定义精度保留(向下取值)
	 * 
	 * @param a
	 * @param scale
	 * @return
	 */
	public static BigDecimal toScaleNum(BigDecimal a, int scale) {
		if(a == null){
			return BigDecimal.ZERO;
		}
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return a.setScale(scale, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 自定义精度保留（向上取值）
	 * 
	 * @param a
	 * @param scale
	 * @return
	 */
	public static BigDecimal toScaleNumUp(BigDecimal a, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return a.setScale(scale, BigDecimal.ROUND_UP);
	}
	
	/**
	 * 格式化BigDecimal(用户短信，邮件发送)
	 * @param a
	 * @return
	 */
	public static String decimalFormat(BigDecimal a) {
		DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
		return decimalFormat.format(a);
	}
}
