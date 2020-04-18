package com.qkwl.common.dto.Enum;

public class CapitalOperationOutStatus {
	public static final int WaitForOperation = 1;// 等待提现
	public static final int OperationLock = 2;// 锁定，等待处理
	public static final int OperationSuccess = 3;// 提现成功
	public static final int Cancel = 4;// 用户取消
	public static final int OnLineLock = 5;// 等待银行到账

	public static String getEnumString(int value) {
		String name = "";
		if (value == WaitForOperation) {
			name = "等待提现";
		} else if (value == OperationLock) {
			name = "正在处理";
		} else if (value == OperationSuccess) {
			name = "提现成功";
		} else if (value == Cancel) {
			name = "用户撤销";
		} else if (value == OnLineLock) {
			name = "锁定";
		}
		return name;
	}
}
