package com.qkwl.common.dto.market;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 深度
 *
 * @author TT
 */
public class DepthData implements Serializable {

    private static final long serialVersionUID = 1L;

    // 价格
    private BigDecimal prize;
    // 数量
    private BigDecimal count;
    // 时间
    private Date time;
    // 类型 0买1卖
    private Integer type;
    // uuid
    private Integer uuid;


    public DepthData(BigDecimal prize, BigDecimal count) {
        this.prize = prize;
        this.count = count;
    }

    public DepthData(BigDecimal prize, BigDecimal count, Date time, Integer type, Integer uuid) {
        this.prize = prize;
        this.count = count;
        this.time = time;
        this.type = type;
        this.uuid = uuid;
    }

    public BigDecimal getPrize() {
        return prize;
    }

    public void setPrize(BigDecimal prize) {
        this.prize = prize;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUuid() {
        return uuid;
    }

    public void setUuid(Integer uuid) {
        this.uuid = uuid;
    }

}
