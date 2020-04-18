package com.qkwl.common.dto.pkg;

import java.math.BigDecimal;

/**
 * 卡包
 */
public class UserPackage {

    private Integer id;

    private Integer uid;

    private BigDecimal total;

    // php 的时间戳
    private Integer inputtime;
    private Integer updatetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getInputtime() {
        return inputtime;
    }

    public void setInputtime(Integer inputtime) {
        this.inputtime = inputtime;
    }

    public Integer getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Integer updatetime) {
        this.updatetime = updatetime;
    }
}
