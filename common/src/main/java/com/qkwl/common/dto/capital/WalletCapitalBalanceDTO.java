package com.qkwl.common.dto.capital;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by wangchen on 2017-08-02.
 */
public class WalletCapitalBalanceDTO implements Serializable{

    private static final long serialVersionUID = -3996348637744696086L;

    private Integer fuid ;
    private BigDecimal result;
    private BigDecimal total;
    private BigDecimal diff;
    private String description;


    public Integer getFuid() {
        return fuid;
    }

    public void setFuid(Integer fuid) {
        this.fuid = fuid;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getDiff() {
        return diff;
    }

    public void setDiff(BigDecimal diff) {
        this.diff = diff;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "WalletCapitalBalanceDTO{" +
                "fuid=" + fuid +
                ", result=" + result +
                ", total=" + total +
                ", diff=" + diff +
                ", description='" + description + '\'' +
                '}';
    }
}
