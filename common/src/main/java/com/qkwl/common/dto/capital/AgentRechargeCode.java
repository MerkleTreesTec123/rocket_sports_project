package com.qkwl.common.dto.capital;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 代理充值
 */
public class AgentRechargeCode implements Serializable{
    private Integer id;
    // 用户id
    private Integer uid;
    // 币种
    private Integer coinId;
    // 充值码
    private String code;
    // 充值量
    private BigDecimal amount;
    // 状态
    private Integer status;
    // 创建时间
    private Date gmtCreate;
    // 修改时间
    private Date gmtModified;
    // 有效期
    private Date gmtExpiry;
    // 版本号
    private Integer version;
    // 管理员Id
    private Integer adminId;


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

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getGmtExpiry() {
        return gmtExpiry;
    }

    public void setGmtExpiry(Date gmtExpiry) {
        this.gmtExpiry = gmtExpiry;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}