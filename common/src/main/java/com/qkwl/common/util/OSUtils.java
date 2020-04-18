package com.qkwl.common.util;

/**
 * 系统环境工具类
 * @author TT
 */
public class OSUtils {
	/**
	 * 判断当前操作系统是否是Linux
	* @Title: OsIsLinux 
	* @Description: TODO
	* @return
	* @return boolean    
	* @throws
	 */
	public static boolean OsIsLinux(){
		String os=getOsName();
		if(os.toLowerCase().startsWith("win")){
			return false;
		}
		return true;
	}
	
	/**
	 * 得到操作系统的名称
	* @Title: getOsName 
	* @Description: TODO
	* @return
	* @return String    
	* @throws
	 */
	public static String getOsName(){
		String os = System.getProperty("os.name"); 
		return os;
	}
	/**
	 * 获取用户的账户名称
	* @Title: getUserName 
	* @Description: TODO
	* @return
	* @return String    
	* @throws
	 */
	public static String getUserName(){
		String userName=System.getProperty("user.name"); 
		return userName;
	}
	/**
	 *获取用户的主目录
	* @Title: getUserHome 
	* @Description: TODO
	* @return
	* @return String    
	* @throws
	 */
	public static String getUserHome(){
		String userHome=System.getProperty("user.home"); 
		return userHome;
	}
	/**
	 * 获取用户的当前工作目录
	* @Title: getUserDir 
	* @Description: TODO
	* @return
	* @return String    
	* @throws
	 */
	public static String getUserDir(){
		String userDir=System.getProperty("user.dir"); 
		return userDir;
	}
	/**
	 * 获取文件分隔符（在 UNIX 系统中是“/”）
	* @Title: getFileSeparator 
	* @Description: TODO
	* @return
	* @return String    
	* @throws
	 */
	public static String getFileSeparator(){
		String fileSeparator=System.getProperty("file.separator"); 
		return fileSeparator;
	}
	/**
	 * 获取路径分隔符（在 UNIX 系统中是“:”）
	* @Title: getPathSeparator 
	* @Description: TODO
	* @return
	* @return String    
	* @throws
	 */
	public static String getPathSeparator(){
		String pathSeparator=System.getProperty("path.separator"); 
		return pathSeparator;
	}
	/**
	 * 获取行分隔符（在 UNIX 系统中是“/n”）
	* @Title: getLineSeparator 
	* @Description: TODO
	* @return
	* @return String    
	* @throws
	 */
	public static String getLineSeparator(){
		String lineSeparator=System.getProperty("line.separator"); 
		return lineSeparator;
	}
}
