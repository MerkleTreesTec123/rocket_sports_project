package com.qkwl.common.dto.Enum;

public enum ZsjfCapitalOperationInStatus {
	Succeed(1,"充值成功"),
	Fail(2,"充值失败"),
	WaitForComing(3,"等待审核"),
	AuditSuccess(4,"审核成功");

	private Integer code;
	private String value;

	ZsjfCapitalOperationInStatus(int code, String value) {
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
		for (ZsjfCapitalOperationInStatus item : ZsjfCapitalOperationInStatus.values()) {
			if (item.getCode().equals(code)) {
				return item.getValue().toString();
			}
		}
		return null;
	}
}
