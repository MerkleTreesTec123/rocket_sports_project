package com.qkwl.common.dto.validate;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 短信验证
 * @author LY
 */
public class MessageValidate implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// 区号
	private String areaCode;
	// 手机号
	private String phone;
	// 验证码
	private String code;
	// 创建时间
	private Timestamp createTime;

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}
