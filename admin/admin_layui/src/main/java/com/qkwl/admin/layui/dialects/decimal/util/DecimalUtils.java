package com.qkwl.admin.layui.dialects.decimal.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 格式化BigDecimal帮助类
 */
public class DecimalUtils {

    /**
     * 格式化
     *
     * @param decimal 数据
     * @return string
     */
    public String format(BigDecimal decimal) {
        if(decimal == null){
            return "";
        }
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        return decimalFormat.format(decimal);
    }

    /**
     * 格式化
     *
     * @param decimal           数据
     * @param minIntegerDigits  最小整数位
     * @param maxFractionDigits 最大整数位
     * @return String
     */
    public String format(BigDecimal decimal, Integer minIntegerDigits, Integer maxFractionDigits) {
        if(decimal == null){
            return "";
        }
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        decimalFormat.setMinimumIntegerDigits(minIntegerDigits.intValue());
        decimalFormat.setMaximumFractionDigits(maxFractionDigits.intValue());
        return decimalFormat.format(decimal);
    }
}
