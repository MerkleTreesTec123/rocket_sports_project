package com.qkwl.common.dto.Enum;

public enum SystemTradeTypeEnum {

	//ETC(3,"ETH交易区"),
	//ETH(2, "BTC交易区"),

	USDT(1,"对USDT交易区","USDT",3),
	BTC(2, "对BTC交易区","BTC",1),
	ETH(3, "对ETH交易区","ETH",2);


	private Integer code;
	private Object value;
	private String symbol;
	private Integer coinId;

	private SystemTradeTypeEnum(Integer code, Object value,String symbol,Integer coinId) {
		this.code = code;
		this.value = value;
		this.symbol = symbol;
		this.coinId = coinId;
	}

	public Integer getCoinId() {
		return coinId;
	}

	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
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

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public static String getValueByCode(Integer code) {
		for (SystemTradeTypeEnum coinType : SystemTradeTypeEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
