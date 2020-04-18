package com.qkwl.web.utils;

import java.math.BigDecimal;

public class WebConstant {

    public static int BCAgentId = 0;

    /**
     * 资产图片上传路径
     */
    public static String QUALITYASSETS = "hk/upload/assets/";

    /**
     * 最小充值金额
     */
    public static final BigDecimal MINRECHARGECNY = new BigDecimal("100");
    /**
     * 最大充值金额
     */
    public static final BigDecimal MAXRECHARGECNY = new BigDecimal("1000000");

    public static final int[] My_Symbol = new int[]{35,36};

}
