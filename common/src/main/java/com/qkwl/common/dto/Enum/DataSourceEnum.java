package com.qkwl.common.dto.Enum;

/**
 * 数据来源枚举
 * @author LY
 *
 */
public enum DataSourceEnum {
	WEB(1, "WEB"), // Web
	APP(2, "APP"), // APP
	API(3, "API"); // API
	
	private Integer code;
	private Object value;

	private DataSourceEnum(Integer code, Object value) {
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
		for (DataSourceEnum dataSource : DataSourceEnum.values()) {
			if (dataSource.getCode().equals(code)) {
				return dataSource.getValue().toString();
			}
		}
		return null;
	}

}
