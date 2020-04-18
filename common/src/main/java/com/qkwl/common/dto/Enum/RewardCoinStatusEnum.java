package com.qkwl.common.dto.Enum;

public enum RewardCoinStatusEnum {
	
	NOREWARD(0, "未发放"),		
	REWARD(1, "已发放");
	
	private Integer code;
	private String value;
	
	private RewardCoinStatusEnum(int code, String value) {
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
		for (RewardCoinStatusEnum e : RewardCoinStatusEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getValue().toString();
			}
		}
		return null;
	}

}
