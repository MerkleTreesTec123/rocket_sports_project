package com.qkwl.service.user.constant;

import java.math.BigDecimal;

/**
 * 系统常量
 */
public class ConfigConsts {
    /**
     * 人民币最小充值金额
     */
    public static final BigDecimal RMB_RECHANGE_MIN = new BigDecimal("100");
    /**
     * 人民币最大充值金额
     */
    public static final BigDecimal RMB_RECHANGE_MAX = new BigDecimal("1000000");
}
