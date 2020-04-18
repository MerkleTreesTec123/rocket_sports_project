package com.qkwl.common.dto.Enum;

/**
 * 产品众筹状态
 * @author ZKF
 * 
 */
public enum IcoProductStateEnum {
	ICO(1, "预热中"),
	ICOING(2, "预售中"),
	ICOEND(3, "预售结束");

	private Integer code;
	private String value;

	private IcoProductStateEnum(Integer code, String value) {
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
	
	public static String getValueByCode(Integer code) {
		for (IcoProductStateEnum e : IcoProductStateEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getValue().toString();
			}
		}
		return null;
	}
}
