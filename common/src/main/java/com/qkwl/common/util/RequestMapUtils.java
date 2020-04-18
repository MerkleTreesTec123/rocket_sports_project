package com.qkwl.common.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求参数转化成Map
 * @author ZKF
 */
public class RequestMapUtils {
	/**
	 * 获取Request中的参数并返回Map对象 @Title: getMap @Description: TODO @param
	 * request @return @return Map<String,Object> @throws
	 */
	@SuppressWarnings("rawtypes")
	public static SortedMap<String, String> getMap(HttpServletRequest request) {
		// 参数Map
		Map<String, String[]> properties = request.getParameterMap();
		// 返回值Map
		SortedMap<String, String> map = new TreeMap<String, String>();
		Iterator entries = properties.entrySet().iterator();
		Map.Entry entry;
		String name = "";
		String value = "";
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (null == valueObj) {
				value = "";
			} else if (valueObj instanceof String[]) {
				String[] values = (String[]) valueObj;
				for (int i = 0; i < values.length; i++) {
					value = values[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
			} else {
				value = valueObj.toString();
			}
			map.put(name, value);
		}
		return map;
	}

	/**
	 * 获取查询参数 返回格式 ：key1=value1&key2=value2... @Title:
	 * getUrlParamers @Description: TODO @param request @return @return
	 * String @throws
	 */
	public static String getUrlParamers(HttpServletRequest request) {
		SortedMap<String, String> map = getMap(request);
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append("&");
		}
		if (sb.length() > 0) {
			String temp = sb.toString().substring(0, sb.toString().length() - 1);
			return temp;
		}
		return "";
	}
}
