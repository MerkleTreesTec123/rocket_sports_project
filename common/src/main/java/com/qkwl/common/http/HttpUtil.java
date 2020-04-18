package com.qkwl.common.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * 工具类
 * @author ZKF
 */
public class HttpUtil {

	/**
	 * @Title: getNameValuePairs
	 * @Description: TODO(Map转NameValuePair数组)
	 * @param params POST传入参数
	 * @return NameValuePair[] 返回类型
	 */
	public static List<NameValuePair> getNameValuePairs(Map<String, String> params) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			nameValuePair.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return nameValuePair;
	}

	/**
	 * @Title: getLinkString
	 * @Description: TODO(把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串)
	 * @param params 	需要排序并参与字符拼接的参数组
	 * @param sort 		true排序，false不排序
	 * @param marks 	true加双引号，false不加双引号
	 * @return String  	拼接后字符串
	 */
	public static String getLinkString(Map<String, String> params, boolean sort, boolean marks) {
		String prestr = "";
		List<String> keys = new ArrayList<String>(params.keySet());
		if(!sort){
			Collections.sort(keys);
		}
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if(marks){
				key 	= "\"" + key 	+ "\"";
				value 	= "\"" + value 	+ "\"";
			}
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}
	
	/**
	 * @Title: loadKeyStore
	 * @Description: TODO(加载keyStory，例：tomcat.keystore)
	 * @param keyStoreFilePath
	 * @param password
	 * @param type KeyStore.getDefaultType()
	 * @throws IOException KeyStore  返回类型
	 */
	public static KeyStore loadKeyStore(String keyStoreFilePath, String password, String type){
		FileInputStream is 	= null;
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(type);
			is 	= new FileInputStream(new File(keyStoreFilePath));
			trustStore.load(is, password.toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return trustStore;
	}
	
}
