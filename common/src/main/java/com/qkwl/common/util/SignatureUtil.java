package com.qkwl.common.util;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.crypto.SHA1Util;


public class SignatureUtil {

	public static String SIGN_TYPE = "MD5";

	/**
	 * @Description: ：sign签名
	 * @param params  请求参数
	 * @param signType 签名类型
	 * @return
	 */
	public static String sign(SortedMap<String, String> params, String signType, String secretKey) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key != null && (key.equals("created") || key.equals("sign"))){
				continue;
			}
			if (null != value && !"".equals(value)) {
				if(!params.lastKey().equals(key)){
					sb.append(key + "=" + value + "&");
				}else{
					sb.append(key + "=" + value);
				}
			}
		}
		sb.append("&secret_key=" + secretKey);
		String sign = "";
		if (signType.equalsIgnoreCase("MD5")) {
			sign = MD5Util.md5(sb.toString()).toUpperCase();
		} else if (signType.equalsIgnoreCase("SHA1")) {
			sign = SHA1Util.getSha1(sb.toString());
		}
		return sign;
	}
	
	/**
	 * 除去数组中的空值和签名参数
	 * @param params 签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> params) {
		Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")) {
				it.remove(); // 删除value为空的和key=sign的
			}
		}
		return params;
	}
}
