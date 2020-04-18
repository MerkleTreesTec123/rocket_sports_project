/**   
* @Title: DateUtils.java 
* @author ZKF  
*/
package com.qkwl.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 系统时间公共类
 * @author ZKF
 */
public class DateUtils {
	public static final SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat YYYY_MM = new SimpleDateFormat("yyyy-MM");
	public static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 取得昨天此时的时间
	 * 
	 * @return 昨天日期（Date）
	 */
	public static Date getYesterdayDate() {
		return new Date(System.currentTimeMillis() - 0x5265c00L);
	}
	
	
	public static Date getSomeDate(int days) {
		return new Date(System.currentTimeMillis() - 0x5265c00L * days);
	}

	public static Date parse(String date, SimpleDateFormat format) {

		try {
			return format.parse(date);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @Title: parse @Description: 将字符串格式的日期转换为日期 @param date @return @return
	 * Date @throws
	 */
	public static Date parse(String date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.parse(date);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取得去过i天的时间
	 * 
	 * @param i
	 *            过去时间天数
	 * @return 昨天日期（Date）
	 */
	public static Date getPastdayDate(int i) {
		return new Date(System.currentTimeMillis() - 0x5265c00L * i);
	}

	public static Date getPastdayDate(Date date, int i) {
		return new Date(date.getTime() - 0x5265c00L * i);
	}

	/**
	 * 取得某日期时间的特定表示格式的字符串
	 * 
	 * @param format
	 *            时间格式
	 * @param date
	 *            某日期（Date）
	 * @return 某日期的字符串
	 */
	public static synchronized String format(Date date, String format) {
		SimpleDateFormat ft = new SimpleDateFormat(format);
		return ft.format(date);
	}

	/**
	 * 取得某日期时间的特定表示格式的字符串
	 * 
	 * @param format
	 *            时间格式
	 * @param date
	 *            某日期（Date）
	 * @return 某日期的字符串
	 */
	public static synchronized String format(Date date, DateFormat format) {
		return format.format(date);
	}
	
	
	/**
	 * 以分钟的形式表示两个长整型数表示的时间间隔
	 * 
	 * @param from
	 *            开始的长整型数据
	 * @param to
	 *            结束的长整型数据
	 * @return 相隔的分钟数
	 */
	public static String getOffDayHours_abs(Date from, Date to) {
		long mid = from.getTime() - to.getTime();
		if(mid < 0){
			return "0";
		}
		int seconds = (int) (mid / 1000L );
		int hours = (int) (mid / 3600000L );
		int days = hours / 24;
		int lefthours = hours % 24;
		int leftmins = seconds / 60;
		int leftseconds = seconds % 60;
		if(days > 0){
			return days + "天";
		}else if(days == 0 && lefthours > 0){
			return lefthours + "小时";
		}else if(days == 0 && lefthours == 0 && leftmins > 0){
			return leftmins + "分钟";
		}else if(days == 0 && lefthours == 0 && leftmins == 0 && leftseconds > 0){
			return leftseconds + "秒";
		}else{
			return "已结束";
		}
	}
	
	
	/**
	 * 以分钟的形式表示两个长整型数表示的时间间隔
	 * 
	 * @param from
	 *            开始的长整型数据
	 * @param to
	 *            结束的长整型数据
	 * @return 相隔的分钟数
	 */
	public static int getOffHours_abs(Date from, Date to) {
		return Math.abs((int) ((from.getTime() - to.getTime()) / 3600000L ));
	}

	/**
	 * 以分钟的形式表示两个长整型数表示的时间间隔
	 * 
	 * @param from
	 *            开始的长整型数据
	 * @param to
	 *            结束的长整型数据
	 * @return 相隔的分钟数
	 */
	public static int getOffMinutes_abs(Date from, Date to) {
		return Math.abs((int) ((from.getTime() - to.getTime()) / 60000L));
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @return 相隔的秒钟
	 */
	public static int getOffSeconds_abs(Date from, Date to) {
		return Math.abs((int) ((from.getTime() - to.getTime()) / 1000L));
	}

	/**
	 * 以天的形式表示两个长整型数表示的时间间隔
	 * 
	 * @param from
	 *            开始的长整型数据
	 * @param to
	 *            结束的长整型数据
	 * @return 相隔的天数
	 */
	public static int getOffDays_abs(Date from, Date to) {
		return Math.abs((int) ((from.getTime() - to.getTime()) / (60000L * 60 * 24)));
	}

	/**
	 * 以分钟的形式表示两个长整型数表示的时间间隔
	 * 
	 * @param from
	 *            开始时间
	 * @param to
	 *            结束时间
	 * @return 相隔的分钟数
	 */
	public static int getOffMinutes(Date from, Date to) {
		return (int) ((to.getTime() - from.getTime()) / (60 * 1000L));
	}

	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = DateUtils.YYYY_MM_DD;
		smdate = sdf.parse(DateUtils.YYYY_MM_DD.format(smdate));
		bdate = sdf.parse(DateUtils.YYYY_MM_DD.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}
}
