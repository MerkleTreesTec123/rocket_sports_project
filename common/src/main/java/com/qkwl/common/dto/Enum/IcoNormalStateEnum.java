package com.qkwl.common.dto.Enum;

/**
 * 普通众筹状态
 * @author ZKF
 * 
 */
public enum IcoNormalStateEnum {
	ICO(1, "预热中"),				// 预热中
	ICOING(2, "众筹中"),				// 众筹中
	ICOEND(3, "众筹结束");			// 众筹结束

	private Integer code;
	private String value;

	private IcoNormalStateEnum(Integer code, String value) {
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
		for (IcoNormalStateEnum e : IcoNormalStateEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getValue().toString();
			}
		}
		return null;
	}
}
