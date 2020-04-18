package com.qkwl.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** 
 * 资源工具类
 * @author ZKF
 */
public class PropertiesUtils {
	/**
	 * 获取资源文件中属性名称的值
	 * @Title: getValueByPropertyName 
	 * @param ResourceName
	 * @param propertyName
	 * @return
	 * @throws IOException
	 * @return String    
	 * @throws
	 */
	public static String getValueByPropertyName(String ResourceName,String propertyName) throws IOException{ 
		Properties properties = getProperties(ResourceName); 
		return properties.getProperty(propertyName);
	}
	
	/**
	 * 根据资源名称获取资源对象
	 * @Title: getProperties 
	 * @param ResourceName
	 * @return
	 * @throws IOException
	 * @return Properties    
	 * @throws
	 */
	public static Properties getProperties(String ResourceName) throws IOException{
		String pathString=PropertiesUtils.class.getClassLoader().getResource(ResourceName).getPath();
		FileInputStream fis = new FileInputStream(pathString);
		Properties properties = new Properties();
		properties.load(fis); 
		return properties;
	}
}
