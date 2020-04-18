package com.qkwl.common.util;

import java.util.StringTokenizer;

public class Ip2Long {

	/**
	 * IP地址转化为长整型
	 * @param ip
	 * @return IP转化为长整形数值
	 */
	public static String ip2Long(String ip) {
//		long result = 0;
//		StringTokenizer token = new StringTokenizer(ip, ".");
//		result += Long.parseLong(token.nextToken()) << 24;
//		result += Long.parseLong(token.nextToken()) << 16;
//		result += Long.parseLong(token.nextToken()) << 8;
//		result += Long.parseLong(token.nextToken());
//		return result;
		return ip;
	}

	/**
	 * 长整型解析为IP地址
	 * @param ipLong 待解析的长整形数值
	 * @return ip串
	 */
	public static String long2ip(String ipLong){
//
//	      StringBuilder sb = new StringBuilder();
//
//	      sb.append(ipLong>>>24);sb.append(".");
//	      sb.append(String.valueOf((ipLong&0x00FFFFFF)>>>16));sb.append(".");
//	      sb.append(String.valueOf((ipLong&0x0000FFFF)>>>8));sb.append(".");
//	      sb.append(String.valueOf(ipLong&0x000000FF));
//
//	      return sb.toString();

		return ipLong;
	}  
	
	public static void main(String[] args) {
//		String ip = "171.111.45.162";
//		System.out.println("测试ip:"+ip);
//		long time = System.currentTimeMillis();
//		Long num = ip2Long(ip);
//		time = System.currentTimeMillis() - time;
//		System.out.println("转化为Long:"+ num);
//		System.out.println("耗时："+time);
//		time = System.currentTimeMillis();
//		ip = long2ip(num);
//		time = System.currentTimeMillis() - time;
//		System.out.println("解析为ip:"+ ip);
//		System.out.println("耗时："+time);
	}

}
