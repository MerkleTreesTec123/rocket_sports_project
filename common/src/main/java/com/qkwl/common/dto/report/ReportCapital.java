package com.qkwl.common.dto.report;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充提统计
 */
public class ReportCapital {
    private Integer id;
    // 币种Id
    private Integer coinId;
    // 充提类型
    private Integer type;
    // 充提数量/金额
    private BigDecimal amount;
    // 手续费
    private BigDecimal fee;
    // 小时索引
    private Integer hourIndex;
    // 统计开始时间
    private Date gmtBegin;
    // 统计结束时间
    private Date gmtEnd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getHourIndex() {
        return hourIndex;
    }

    public void setHourIndex(Integer hourIndex) {
        this.hourIndex = hourIndex;
    }

    public Date getGmtBegin() {
        return gmtBegin;
    }

    public void setGmtBegin(Date gmtBegin) {
        this.gmtBegin = gmtBegin;
    }

    public Date getGmtEnd() {
        return gmtEnd;
    }

    public void setGmtEnd(Date gmtEnd) {
        this.gmtEnd = gmtEnd;
    }
}