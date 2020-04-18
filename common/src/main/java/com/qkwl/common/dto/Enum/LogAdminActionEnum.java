package com.qkwl.common.dto.Enum;

/**
 * 管理員行为
 * @author TT
 */
public enum LogAdminActionEnum {
	LOGIN(1, "登录"),
	SYSTEM_USER_BAN_OK(2, "管理员禁用用户"),
	SYSTEM_USER_BAN_NO(3, "管理员启用用户"),
	SYSTEM_MAIL(4, "管理员修改邮箱"),
	SYSTEM_PHONE(5, "管理员修改手机"),
	SYSTEM_GOOGLE(6, "管理员修改谷歌"),
	SYSTEM_LOGINPWD(7, "管理员修改登录密码"),
	SYSTEM_TRADEPWD(8, "管理员修改交易密码"),
	SYSTEM_IDCARD(9, "管理员修改实名"),
	SYSTEM_VIP6(10, "管理员赠送VIP6"),//
	SYSTEM_USERINFO(11, "管理员修改用户信息"),
	SYSTEM_USER_RMB_WITHDRAW_NO(12, "管理员禁止用户提现"),
	SYSTEM_USER_RMB_WITHDRAW_OK(13, "管理员启用用户提现"),
	SYSTEM_USER_COIN_WITHDRAW_NO(14, "管理员禁止用户虚拟币提现"),
	SYSTEM_USER_COIN_WITHDRAW_OK(15, "管理员启用用户虚拟币提现"),
	ARTICLE_TYPE_ADD(16, "新增文章类型"),
	ARTICLE_TYPE_MODIFY(17, "修改文章类型"),
	ARTICLE_TYPE_DEL(18, "删除文章类型"),
	ARTICLE_ADD(19, "新增文章"),
	ARTICLE_MODIFY(20, "修改文章"),
	ARTICLE_DEL(21, "删除文章"),
	PHONE_ARTICLE_ADD(22, "新增手机文章"),
	PHONE_ARTICLE_MODIFY(23, "修改手机文章"),
	PHONE_ARTICLE_DEL(24, "删除手机文章"),
	COIN_ADD(25, "虚拟币新增"),
	COIN_DISABLE(26, "虚拟币禁用"),
	COIN_ABLE(27, "虚拟币启用"),
	COIN_INFO(28, "虚拟币修改基本信息"),
	COIN_LINK(29, "虚拟币修改链接"),
	COIN_FREES(30, "虚拟币修改手续费"),
	COIN_ADDRESS(31, "虚拟币添加地址"),
	COIN_OPEN(32, "虚拟币开盘"),
	COIN_STOP(33, "虚拟币停盘"),
	CANCEL_RMB_RECHARGE(34, "取消用户人民币充值"),
	ANSWER_QUESTION(35, "问答回复"),
	SYSTEM_ADMIN_ADD(36, "添加后台账号"),
	SYSTEM_ADMIN_MODIFY(37, "修改后台账号信息"),
	SYSTEM_ADMIN_ENABLED(38, "启用后台管理员账号"),
	SYSTEM_ADMIN_DISABLE(39, "禁用后台管理员账号"),
	SYSTEM_ADMIN_MODIFY_ROLE(40, "修改后台账号角色"),
	ARGS_SAVE(41, "新增系统参数"),
	ARGS_MODIFY(42, "修改系统参数"),
	CANCEL_COIN_WITHDRAW(43, "取消用户虚拟币提现"),
	RMB_FREES(44, "人民币修改手续费"),
	SYSTEM_RMB_RECHARGE(45, "审核人民币充值"),
	SYSTEM_MODIFY_RMB_RECHARGE(46, "修改人民币充值金额"),
	SYSTEM_COIN_WITHDRAW(47, "审核虚拟币提现"),
	ADMIN_RMB_RECHARGE(48, "审核人民币手工充值"),
	ADMIN_COIN_RECHARGE(49, "审核虚拟币手工充值"),
	CANCEL_RMB_WITHDRAW(50, "取消用户人民币提现"),
	SYSTEM_RMB_WITHDRAW(51, "审核人民币充值"),
	SYSTEM_API_ENABLED(52, "启用API账号"),
	SYSTEM_API_DISABLE(53, "禁用API账号"),
	SYSTEM_BEAUTIFUL_USE(54, "分配靓号"),
	RechargeAgency_ADD(55, "新增充值代号"),
	RechargeAgency_MODIFY(56, "修改充值代号"),
	RechargeAgency_DEL(57, "删除充值代号"),
	SYSTEM_VIDEO(58, "管理员修改视频认证"),
	ASSET_FREEZE(59, "解除资产冻结"),
	RESET_COIN_WITHDRAW(61, "重置虚拟币提现"),

	SYSTEM_RMB_ONLINE_WITHDRAW(60, "在线提交人民币提现申请"),
	MODIFY_CAPITAL_BALANCE(62,"修改资金平衡"),

	COIN_INFO_ADD(63, "虚拟币新增");

	private Integer code;
	private String value;

	private LogAdminActionEnum(Integer code, String value) {
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
		for (LogAdminActionEnum source : LogAdminActionEnum.values()) {
			if (source.getCode().equals(code)) {
				return source.getValue().toString();
			}
		}
		return null;
	}
}
