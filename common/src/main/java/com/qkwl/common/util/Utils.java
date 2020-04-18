package com.qkwl.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qkwl.common.exceptions.BCException;


public class Utils {
	
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	/**
	 * 获得随机字符串
	 * @param count 字符串长度
	 * @return
	 */
	public static String randomString(int count) {
		String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
		int size = str.length();
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		while (count > 0) {
			sb.append(String.valueOf(str.charAt(random.nextInt(size))));
			count--;
		}
		return sb.toString();
	}

	/**
	 * 获取随机数字
	 * @param length 随机数长度
	 * @return
	 */
	public static String randomInteger(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(new Random().nextInt(10));
		}
		return sb.toString();
	}

	/**
	 * 获取随机图像名称
	 * @return
	 */
	public static String getRandomImageName() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
		String path = simpleDateFormat.format(new Date());
		path += "_" + randomString(5);
		return path;
	}

	/**
	 * 保存文件
	 * @param dir 保存文件的目录
	 * @param fileName 文件名称
	 * @param inputStream 保存的数据流
	 * @return 保存状态（是否成功）
	 */
	public static boolean saveFile(String dir, String fileName, InputStream inputStream) {
		boolean flag = false;
		File directory = new File(dir);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (!directory.isDirectory()) {
			logger.debug("Not a directory!");
			return flag;
		}
		if (!directory.exists()) {
			directory.mkdirs();
		}

		if (inputStream == null) {
			logger.debug("InputStream null.");
			return flag;
		}

		File realFile = new File(directory, fileName);
		FileOutputStream fileOutputStream = null;
		int tmp = 0;
		try {
			fileOutputStream = new FileOutputStream(realFile);
			while ((tmp = inputStream.read()) != -1) {
				fileOutputStream.write(tmp);
			}

			if (fileOutputStream != null) {
				fileOutputStream.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}

			flag = true;

		} catch (Exception e) {
			logger.debug("Read InputStream fail.");
		} finally {
			fileOutputStream = null;
			inputStream = null;
		}

		return flag;
	}

	/**
	 * md5码生成（先使用MD5加密，再使用Base64加密）
	 * @param content 需要加密的字符串
	 * @return
	 * @throws Exception
	 */
	public static String MD5(String content) throws BCException {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BCException();
		}
		sun.misc.BASE64Encoder baseEncoder = new sun.misc.BASE64Encoder();
		String retString = baseEncoder.encode(md5.digest(content.getBytes()));
		return retString;
	}

	/**
	 * 获取时间撮
	 * 
	 * @return
	 */
	public static Timestamp getTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * 后台页数
	 * @return
	 */
	public static int getNumPerPage() {
		return 40;
	}

	/**
	 * 通用唯一识别码
	 * 
	 * @return
	 */
	public static synchronized String UUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 计算两个时间差
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static long timeMinus(Timestamp t1, Timestamp t2) {
		return (t1.getTime() - t2.getTime()) / 1000;
	}

	/**
	 * 获得今天0点
	 * @return
	 */
	public static long getTimesNowZero() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	/**
	 * 以当前时间为起点,增加天数
	 * @return
	 */
	public static long getTimesAdd(int day) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, day);
		return cal.getTimeInMillis();
	}

	/**
	 * 获得指定日期的0点
	 * @return
	 */
	public static long getTimesByDay(int day) {
		Calendar cal = Calendar.getInstance();
		long nowTime = new Date().getTime();
		long dayTime = day * 24 * 60 * 60 * 1000;
		cal.setTime(new Date(nowTime + dayTime));
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	/**
	 * 获得指定日期的0点
	 * @return
	 */
	public static long getDayByMonth(int day) {
		Calendar cal = Calendar.getInstance();
		long nowTime = new Date().getTime();
		cal.setTime(new Date(nowTime));
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	/**
	 * 获取指定时间
	 * @return
	 */
	public static Timestamp getYYYYMMDDHHMM() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * 获取当前时间字符串
	 * @return
	 */
	public static String getCurTimeString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	/**
	 * 将数字转换成字符串
	 * @param f
	 * @return
	 */
	public static String number4String(BigDecimal f, Integer digit) {
		DecimalFormat df = new DecimalFormat();
		String style = "0.";// 定义要显示的数字的格式
		for (int i = 0; i < digit; i++) {
			style += "0";
		}
		if (digit.equals(0)) {
			style = "0";
		}
		df.applyPattern(style);
		return df.format(f);
	}
	
	/**
	 * 填补位数
	 * @param f
	 * @return
	 */
	public static String number2String(BigDecimal f) {
		DecimalFormat df = new DecimalFormat();
		String style = "0.00";// 定义要显示的数字的格式
		df.applyPattern(style);
		return df.format(f);
	}
	
	/**
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
	 * @param request
	 * @return
	 */
	public final static String getIpAddr(HttpServletRequest request) {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("X-Real-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
			
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	}
	
	/**
	 * 格式化时间
	 * @param timestamp
	 * @return
	 */
	public static String dateFormatYYYYMMDD(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(timestamp);
	}

	/**
	 * 格式化时间
	 * @param timestamp
	 * @return
	 */
	public static String dateFormat(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(timestamp);
	}

	/**
	 * 格式化时间
	 * @param timestamp
	 * @return
	 */
	public static String dateFormat(Timestamp timestamp,String formatsString) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatsString);
		return sdf.format(timestamp);
	}

	/**
	 * 格式化时间
	 * @param timestamp
	 * @return
	 */
	public static String dateFormatYYYYMMDDHHMM(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(timestamp);
	}

	/**
	 * 格式化时间
	 * @param date
	 * @return
	 */
	public static String dateFormatHHMMSS(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(date);
	}
	
	/**
	 * 检测参数是否为数值
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str == null || str.trim().length() == 0)
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 获取随机数字
	 * @param length 生成字符串的长度
	 * @return
	 */
	public static String getRandomString(int length) {
		String base = "01234567890123456789012345678901234567890123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
	
	/**
	 * 获取时间String,yyyy-MM-dd,天数+-
	 * @param day
	 * @return
	 */
	public static String getCurTimeString(int day) {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
	/**
	 * 获取时间对象,yyyy-MM-dd,天数+-
	 * @param day
	 * @return
	 */
	public static Timestamp getCurTimestamp(int day) {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
		return new Timestamp(calendar.getTime().getTime()); // 这个时间就是日期往后推一天的结果
	}
	
	/**
	 * 获取指定时间的   天数 + -
	 * @param day 加减天数
	 * @param curday 当前时间
	 * @return 需要时间
	 */
	public static Timestamp getCurTimestampCS(int day,Timestamp curday) {		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(curday);
		calendar.add(Calendar.DATE, day);
		return new Timestamp(calendar.getTime().getTime());
	}
	
	/**
	 * 获取时间Date，yyyy-MM-dd,天数+-
	 * @param date
	 * @return
	 */
	public static Date getCurTimeString(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
			
		}
	}
	
	/**
	 * 获取指定日期的Timestamp
	 * @param datesString
	 * @return
	 */
	public static Timestamp getSpecifyDateTimestamp(String datesString,int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = sdf.parse(datesString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
		return new Timestamp(calendar.getTime().getTime()); // 这个时间就是日期往后推一天的结果
	}
	
	/**
	 * 获取指定日期的Timestamp
	 * @param datesString
	 * @return
	 */
	public static Timestamp getDateForHHMMSS(String datesString) {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		Date date = new Date();
		try {
			date = sdf.parse(datesString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return new Timestamp(calendar.getTime().getTime()); 
	}
	
	/**
	 * 隐藏登录名
	 * @param loginName
	 * @return
	 */
	public static String formatloginName(String loginName) {
		if(StringUtils.isEmpty(loginName)){
			return "";
		}
		if (PhoneValiUtil.isMobile(loginName)) {
			loginName = loginName.substring(0, 3) + "****" + loginName.substring(loginName.length() - 4, loginName.length());
		} else if (loginName.matches(Constant.EmailReg)) {
			loginName = loginName.substring(0, 3) + "****" + loginName.substring(loginName.length() - 4, loginName.length());
		} else {
			int endSub = loginName.length() < 6 ? loginName.length() : 6;
			loginName = loginName.substring(0, endSub) + "****";
		}
		return loginName;
	}
	
	public static boolean isIp(String ip){
		if(ip.length() < 7 || ip.length() > 15 || "".equals(ip))
		{
			return false;
		}
		/**
		 * 判断IP格式和范围
		 */
		String rexp = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		Pattern pat = Pattern.compile(rexp);  
		Matcher mat = pat.matcher(ip);  
		boolean ipAddress = mat.find();
		return ipAddress;
	}
	
	/**
	 * 过滤特殊字符
	 */
	public static String filtChart(String str){
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\"]"; 
		Pattern p = Pattern.compile(regEx); 
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}
}
