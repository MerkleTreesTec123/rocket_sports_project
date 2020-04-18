package com.qkwl.common.dto.Enum;

public enum IdentityStatusEnum {
	
	WAIT(0, "等待审核"),		
	PASS(1, "通过审核"),	
	NOTPASS(2, "未通过审核");
	
	private Integer code;
	private String value;
	
	private IdentityStatusEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static String getTypeValueByCode(Integer code) {
		for (IdentityStatusEnum e : IdentityStatusEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getValue().toString();
			}
		}
		return null;
	}

}
