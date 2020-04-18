package com.qkwl.common.dto.Enum;

/**
 * 存币理财状态
 * @author LY
 *
 */
public enum UserFinancesStateEnum {
	FROZEN(1, "冻结"), SEND(2, "已发放"), CANCEL(3, "已取消"), REDEEM(4, "已赎回");

	private Integer code;
	private Object value;

	private UserFinancesStateEnum(Integer code, Object value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public static String getValueByCode(Integer code) {
		for (UserFinancesStateEnum financesState : UserFinancesStateEnum.values()) {
			if (financesState.getCode().equals(code)) {
				return financesState.getValue().toString();
			}
		}
		return null;
	}
}
