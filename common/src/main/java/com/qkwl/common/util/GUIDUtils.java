package com.qkwl.common.util;

import java.util.UUID;

/** 
 * GUID生成工具类
 * @author ZKF
 */
public class GUIDUtils {
	/**
	 * 得到GUID 对象
	 * @Title: getUUID 
	 * @return
	 * @return UUID    
	 * @throws
	 */
	public static UUID getGUID(){
		return  UUID.randomUUID();
	}
	
	/**
	 * 得到GUID字符串
	 * @Title: getGUIDString 
	 * @return
	 * @return String    
	 * @throws
	 */
	public static String getGUIDString(){
		  // 创建 GUID 对象
	      UUID uuid = getGUID();
	      // 得到对象产生的ID
	      String guid = uuid.toString();
	      // 转换为大写
	      guid = guid.toUpperCase();
	      // 替换 -
	      guid = guid.replaceAll("-", "");
	      return guid;
	}
}
