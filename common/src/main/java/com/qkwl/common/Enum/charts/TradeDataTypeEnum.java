package com.qkwl.common.Enum.charts;

import com.qkwl.common.dto.Enum.RechargeMatchStatusEnum;
import org.apache.poi.hssf.record.common.FeatFormulaErr2;

/**
 * Created by ZKF on 2017/7/25.
 */
public enum TradeDataTypeEnum {

    VOLUME(1, "成交量"),
    TURNVOLUME(2, "成交额"),
    FEE(3, "手续费");

    private Integer code;
    private String value;

    TradeDataTypeEnum(Integer code, String value) {
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

    public static String getValueByCode(Integer code){
        for (TradeDataTypeEnum e : TradeDataTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
            }
        }
        return null;
    }
}
