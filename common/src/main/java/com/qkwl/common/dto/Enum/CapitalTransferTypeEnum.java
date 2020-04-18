package com.qkwl.common.dto.Enum;

public enum CapitalTransferTypeEnum {

	IN(0, "转入"),		
	OUT(1, "转出");
	
	private Integer code;
	private String value;
	
	private CapitalTransferTypeEnum(int code, String value) {
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
		for (CapitalTransferTypeEnum e : CapitalTransferTypeEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getValue().toString();
			}
		}
		return null;
	}
}
