package com.qkwl.common.dto.Enum;

/**
 * 用户行为
 * @author TT
 */
public enum LogUserActionEnum {
	REGISTER(1, "注册"),
	LOGIN(2, "登录"),	
	BIND_MAIL(3, "绑定邮箱"),
	MODIFY_MAIL(4, "修改邮箱"),
	BIND_PHONE(5, "绑定手机"),
	MODIFY_PHONE(6, "修改手机"),
	BIND_GOOGLE(7, "绑定谷歌"),
	MODIFY_GOOGLE(8, "修改谷歌"),
	BIND_LOGINPWD(9, "设置登录密码"),
	MODIFY_LOGINPWD(10, "修改登录密码"),
	BIND_TRADEPWD(11, "设置交易密码"),
	MODIFY_TRADEPWD(12, "重置交易密码"),
	BIND_IDCARD(13, "实名"),
	BUY_VIP6(14, "购买VIP6"),
	USE_CODE(15, "使用充值码"),
	QUESTION_SUBMIT(16, "提交提问"),
	QUESTION_REPLY(17, "回复提问"),
	ADD_BANK(18, "添加银行卡"),
	ADD_ADDRESS_RECHARGE(19, "添加虚拟币充值地址"),
	ADD_ADDRESS_WITHDRAW(20, "添加虚拟币提现地址"),
	RMB_RECHARGE(21, "RMB充值"),
	RMB_WITHDRAW(22, "RMB提现"),
	SYSTEM_RMB_RECHARGE(23, "管理员RMB充值"),
	COIN_RECHARGE(24, "虚拟币充值"),
	COIN_WITHDRAW(25, "虚拟币提现"),
	SYSTEM_COIN_RECHARGE(26, "管理员虚拟币充值"),
	BUY(29, "买"),
	SELL(30, "卖"),
	SEND_SMS(31, "短信"),
	SEND_MAIL(32, "邮件"),
	SCORE(33, "积分"),
	FIND_LOGINPASS(34, "找回密码"),
	RMB_WITHDRAW_WAIT(35, "RMB等待提现"),
	RMB_WITHDRAW_CANCEL(36, "RMB撤销提现"),
	COIN_WITHDRAW_WAIT(37, "虚拟币等待提现"),
	COIN_WITHDRAW_CANCEL(38, "虚拟币撤销提现"),

	WXCONNECT(40,"wx关联用户"),
	QQCONNECT(41,"qq关联用户"),
	MIGRATION(42,"迁移账号"),
	AGENCY_RECHARGE_CODE(43, "代理充值"),
	ADD_MAIL(44, "添加邮箱"),
	MODIFY_BANK(45, "修改银行卡"),

	RMB_WITHDRAW_ONLINE(46, "RMB在线提现申请"),
	ADD_PHONE(47, "添加手机"),
	APP_ADD_EMAIL(48,"App 添加邮件"),
	APP_BIND_EMAIL(49,"App 绑定邮件");

	private Integer code;
	private String value;

	private LogUserActionEnum(Integer code, String value) {
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
		for (LogUserActionEnum source : LogUserActionEnum.values()) {
			if (source.getCode().equals(code)) {
				return source.getValue().toString();
			}
		}
		return null;
	}
	
}
