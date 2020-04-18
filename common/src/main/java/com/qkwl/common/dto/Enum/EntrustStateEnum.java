package com.qkwl.common.dto.Enum;

public enum EntrustStateEnum {
	Submit(0,"提交等待"), // 用于聚合交易
	Going(1, "未成交"),				// 未成交
	PartDeal(2, "部分成交"),			// 部分成交
	AllDeal(3, "完全成交"),			// 完全成交
	WAITCancel(4, "撤单处理中"),		// 等待撤单
	Cancel(5, "已撤销"),				// 撤销
	GroupMatchLock(6,"聚合交易撮合锁定"), // 还未提交到火币
    SubmitCancel(7,"提交取消"), //	等待处理
    GroupCancelLock(8,"聚合交易取消锁定"); // 还未提交到火币
	
	private Integer code;
	private String value;
	
	private EntrustStateEnum(int code, String value) {
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
	
	public static String getEntrustStateValueByCode(Integer code) {
		for (EntrustStateEnum entrustsource : EntrustStateEnum.values()) {
			if (entrustsource.getCode().equals(code)) {
				return entrustsource.getValue().toString();
			}
		}
		return null;
	}
}
