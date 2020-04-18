package com.qkwl.common.dto.Enum;

/**
 * 虚拟币操作类型
 * 
 * @author LY
 *
 */
public enum VirtualCapitalOperationTypeEnum {

	COIN_IN(1, "充值"), 
	COIN_OUT(2, "提现");

	private Integer code;
	private String value;

	private VirtualCapitalOperationTypeEnum(Integer code, String value) {
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
		for (VirtualCapitalOperationTypeEnum operationType : VirtualCapitalOperationTypeEnum.values()) {
			if (operationType.getCode().equals(code)) {
				return operationType.getValue();
			}
		}
		return null;
	}
}
