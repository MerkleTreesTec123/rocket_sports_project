package com.qkwl.service.user.utils;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.util.GUIDUtils;
import com.qkwl.common.dto.api.FApiAuth;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 用户api生成工具
 */
public class ApiAuthGenerator {

	private static String domain = "www.xxx.com";

	private static String getApiKey(){
		return GUIDUtils.getGUIDString().toLowerCase();
	}
	
	public static FApiAuth getApiSecretKey(){
		
		//获取Apikey
		String apikey = getApiKey();
		
		//加密串 = 域名 + apikey+当前时间 转大写
		String str = domain + apikey.toUpperCase()+System.currentTimeMillis();

		//获取SecretKey
		String secretkey = MD5Util.md5(str).toUpperCase();
		
		FApiAuth auth = new FApiAuth();
		auth.setFapikey(apikey);
		auth.setFsecretkey(secretkey);
		
		return auth;
	}

//	public static void main(String args[]){
//		//FApiAuth apiSecretKey = getApiSecretKey();
//		//System.out.println(apiSecretKey.getFapikey());
//		//System.out.println(apiSecretKey.getFsecretkey());
//	}
//
	
	
}
