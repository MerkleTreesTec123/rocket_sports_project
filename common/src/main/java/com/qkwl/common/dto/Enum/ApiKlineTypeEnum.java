package com.qkwl.common.dto.Enum;

/**
 * huobi k线类型
 * @author ZKF
 */
public enum ApiKlineTypeEnum {
	
	m1 ("1min"  , 1),
	m3 ("3min"  , 3),
	m5 ("5min"  , 5),
	m15("15min" , 15),
	m30("30min" , 30),
	h1 ("1hour" , 60),
	h2 ("2hour" , 2*60),
	h4 ("4hour" , 4*60),
	h6 ("6hour" , 5*60),
	h12("12hour", 12*60),
	d1 ("1day"  , 24*60),
	d7 ("7day"  , 7*24*60);

	private String code;
	private int value;

	private ApiKlineTypeEnum(String code, int value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public static int getValueByCode(String type){
		for (ApiKlineTypeEnum klinetype : ApiKlineTypeEnum.values()) {
			if (klinetype.getCode().equals(type)) {
				return klinetype.getValue();
			}
		}
		return 0;
	}

}
