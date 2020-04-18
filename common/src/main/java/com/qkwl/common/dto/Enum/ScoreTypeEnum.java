package com.qkwl.common.dto.Enum;

public enum ScoreTypeEnum {
	LOGIN			(1,	100, "登录"),		// 登录
	RECHARGE		(2,	10, "充值"),			// 充值
	TRADING			(3,	100, "交易额"),		// 交易额
	LEVERAGELINES	(4,	500, "申请额度"),	// 申请额度
	ASSETLIMIT		(5,	1000, "净资产额度"),	// 净资产额度
	REALNAME		(6,	500, "实名认证"),	// 实名认证
	PHONE			(7,	500, "手机认证"),	// 手机认证
	EMAIL			(8,	500, "邮箱认证"),	// 邮箱认证
	GOOGLE			(9,	500, "谷歌认证"),	// 谷歌认证
	FIRSTCHARGE		(10,500, "首次充值"),	// 首次充值
	ACTIVITY		(11, 0, "活动"); 		// 活动
	
	private Integer code;
	private Integer score;
	private Object value;

	private ScoreTypeEnum(Integer code,Integer score, Object value) {
		this.code = code;
		this.score = score;
		this.value = value;
	}
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public static String getValueByCode(Integer code) {
		for (ScoreTypeEnum type : ScoreTypeEnum.values()) {
			if (type.getCode().equals(code)) {
                return type.getValue().toString();
            }
		}
		return null;
	}
}
