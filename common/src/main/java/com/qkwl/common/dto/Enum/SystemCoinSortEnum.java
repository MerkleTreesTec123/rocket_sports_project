package com.qkwl.common.dto.Enum;

public enum SystemCoinSortEnum {
	CNY(1, "法币类"),
	BTC(2, "比特币类"), 
	ETH(3, "以太坊类"),
	ICS(4, "小企链资产"),
	ETC(5, "以太经典类"),
	ETP(6, "熵类"),
	GXS(7, "公信宝类"),
	MIC(8, "小米链资产"),
	EOS(9, "EOS"),
	USDT(10,"USDT");

	private Integer code;
	private Object value;

	private SystemCoinSortEnum(Integer code, Object value) {
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
		for (SystemCoinSortEnum coinType : SystemCoinSortEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
