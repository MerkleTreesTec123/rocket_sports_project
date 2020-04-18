package com.qkwl.common.dto.report;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易统计
 */
public class ReportTrade {
    private Integer id;
    // 交易id
    private Integer tradeId;
    // 买方币种
    private Integer buyCoinId;
    // 卖方币种
    private Integer sellCoinId;
    // 交易额
    private BigDecimal tradeAmount;
    // 交易量
    private BigDecimal tradeCount;
    // 手续费/收益
    private BigDecimal tradeFee;
    // 交易类型
    private Integer type;
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

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public Integer getBuyCoinId() {
        return buyCoinId;
    }

    public void setBuyCoinId(Integer buyCoinId) {
        this.buyCoinId = buyCoinId;
    }

    public Integer getSellCoinId() {
        return sellCoinId;
    }

    public void setSellCoinId(Integer sellCoinId) {
        this.sellCoinId = sellCoinId;
    }

    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigDecimal tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public BigDecimal getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(BigDecimal tradeCount) {
        this.tradeCount = tradeCount;
    }

    public BigDecimal getTradeFee() {
        return tradeFee;
    }

    public void setTradeFee(BigDecimal tradeFee) {
        this.tradeFee = tradeFee;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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