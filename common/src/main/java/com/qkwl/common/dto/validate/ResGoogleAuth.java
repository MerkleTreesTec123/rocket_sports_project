package com.qkwl.common.dto.validate;

import java.io.Serializable;

/**
 * 谷歌验证
 * @author LY
 */
public class ResGoogleAuth implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 验证码
	private String qecode;
	// 谷歌key
	private String totpKey;

	public String getQecode() {
		return qecode;
	}

	public void setQecode(String qecode) {
		this.qecode = qecode;
	}

	public String getTotpKey() {
		return totpKey;
	}

	public void setTotpKey(String totpKey) {
		this.totpKey = totpKey;
	}

}
