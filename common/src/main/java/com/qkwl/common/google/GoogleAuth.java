package com.qkwl.common.google;

import java.util.HashMap;
import java.util.Map;

import com.qkwl.common.util.Constant;


/**
 * 谷歌验证码
 * @author TT
 */
public class GoogleAuth {

	/**
	 * 前缀
	 */
	private static String webName = Constant.GoogleAuthName;

	/**
	 * 注册GOOGLE认证，先传入用户登录名，返回一个MAP，里面有两个KEY，一个是url(value为二维码地址),一个是secret(
	 * 为此用户的地址，此地址用于校验)
	 */
	public static Map<String, String> genSecret(String userName) {
		Map<String, String> map = new HashMap<String, String>();
		String secret = GoogleAuthenticator.generateSecretKey();
		String url = GoogleAuthenticator.getQRBarcodeURL(webName, userName, secret);
		map.put("secret", secret);
		map.put("url", url);
		return map;
	}

	/**
	 * 校验GOOGLE认证是否成功
	 */
	public static boolean auth(long code, String secret) {
		long t = System.currentTimeMillis();
		GoogleAuthenticator ga = new GoogleAuthenticator();
		ga.setWindowSize(5); // should give 5 * 30 seconds of grace...
		boolean r = ga.check_code(secret, code, t);
		return r;
	}

	/**
	 * 我的
	 *
	 * @param args
	 */
	// public static void main(String args[]){
	// 	//2JCC26754QEFIPUV
	// 	boolean res = auth(850161,"2JCC26754QEFIPUV");
	// 	System.out.println(res);

	// }
}