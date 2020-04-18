package com.qkwl.common.dto.log;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 资金日志
 * @author work
 * @date 2017-04-22 15:05:01
 */
public class LogWalletDo implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer type;
    private BigDecimal total;
    private BigDecimal frozen;
    private BigDecimal change;
    private BigDecimal fee;
    private String info;
    private Date gmtModified;
    private Date gmtCreate;

    public LogWalletDo() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getFrozen() {
        return frozen;
    }

    public void setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "LogWalletDo{" +
                "id=" + id +
                ", type=" + type +
                ", total=" + total +
                ", frozen=" + frozen +
                ", change=" + change +
                ", fee=" + fee +
                ", info='" + info + '\'' +
                ", gmtModified=" + gmtModified +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}