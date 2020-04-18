package com.qkwl.common.dto.user;

import java.io.Serializable;

/**
 * 登录后响应的用户信息
 * @author ZKF
 */
public class LoginResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// token令牌
	private String token;
	// 用户信息
	private FUser userinfo;
	// 签名key
	private String secretKey;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public FUser getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(FUser userinfo) {
		this.userinfo = userinfo;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
