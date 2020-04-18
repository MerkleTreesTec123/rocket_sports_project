package com.qkwl.common.dto.Enum;

/**
 * 众筹状态
 * @author LY
 * 
 */
public enum IcoStateEnum {
	ICO(1, "预热中"),				// 预热中
	ICOING(2, "众筹中"),				// 众筹中
	ICOFAILURE(3, "众筹失败"),		// 众筹失败
	ICOSUCCEED(4, "众筹成功"),		// 众筹成功
	ICOEND(5, "众筹结束");			// 众筹结束
	
	private Integer code;
	private String value;
	
	private IcoStateEnum(Integer code, String value) {
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
	
	public static String getIcoStateValueByCode(Integer code) {
		for (IcoStateEnum icoState : IcoStateEnum.values()) {
			if (icoState.getCode().equals(code)) {
				return icoState.getValue().toString();
			}
		}
		return null;
	}
}
