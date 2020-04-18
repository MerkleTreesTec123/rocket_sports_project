package com.qkwl.common.dto.entrust;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 委单日志
 * @author TT
 */
public class FEntrustLog implements Serializable{
    private static final long serialVersionUID = 1L;
    // 自增ID
    private BigInteger fid;
    // 交易ID
    private Integer ftradeid;
    // 委单类型
    private Integer fentrusttype;
    // 委单ID
    private BigInteger fentrustid;
    // 成交ID
    private BigInteger fmatchid;
    // 成交价
    private BigDecimal famount;
    // 价格
    private BigDecimal fprize;
    // 数量
    private BigDecimal fcount;
    // 是否活跃
    private Boolean fisactive;
    // 更新时间
    private Date flastupdattime;
    // 创建时间
    private Date fcreatetime;
    // 版本号
    private Integer version;
    // 备注
    private String fremark;
    // 用户ID
    private Integer fuid;

    /**扩展字段**/

    // 手续费
    private BigDecimal ffees;
    private Integer coinid;
    private Integer deduction;

    public Integer getDeduction() {
        return deduction;
    }

    public void setDeduction(Integer deduction) {
        this.deduction = deduction;
    }

    public Integer getCoinid() {
        return coinid;
    }

    public void setCoinid(Integer coinid) {
        this.coinid = coinid;
    }

    public BigDecimal getFfees() {
        return ffees;
    }

    public void setFfees(BigDecimal ffees) {
        this.ffees = ffees;
    }

    public BigInteger getFid() {
        return fid;
    }

    public void setFid(BigInteger fid) {
        this.fid = fid;
    }

    public BigInteger getFentrustid() {
        return fentrustid;
    }

    public void setFentrustid(BigInteger fentrustid) {
        this.fentrustid = fentrustid;
    }

    public BigInteger getFmatchid() {
        return fmatchid;
    }

    public void setFmatchid(BigInteger fmatchid) {
        this.fmatchid = fmatchid;
    }

    public BigDecimal getFamount() {
        return famount;
    }

    public void setFamount(BigDecimal famount) {
        this.famount = famount;
    }

    public Date getFcreatetime() {
        return fcreatetime;
    }

    public void setFcreatetime(Date fcreatetime) {
        this.fcreatetime = fcreatetime;
    }

    public BigDecimal getFprize() {
        return fprize;
    }

    public void setFprize(BigDecimal fprize) {
        this.fprize = fprize;
    }

    public BigDecimal getFcount() {
        return fcount;
    }

    public void setFcount(BigDecimal fcount) {
        this.fcount = fcount;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getFisactive() {
        return fisactive;
    }

    public void setFisactive(Boolean fisactive) {
        this.fisactive = fisactive;
    }

    public Integer getFtradeid() {
        return ftradeid;
    }

    public void setFtradeid(Integer ftradeid) {
        this.ftradeid = ftradeid;
    }

    public Integer getFentrusttype() {
        return fentrusttype;
    }

    public void setFentrusttype(Integer fentrusttype) {
        this.fentrusttype = fentrusttype;
    }

    public Date getFlastupdattime() {
        return flastupdattime;
    }

    public void setFlastupdattime(Date flastupdattime) {
        this.flastupdattime = flastupdattime;
    }

    public String getFremark() {
        return fremark;
    }

    public void setFremark(String fremark) {
        this.fremark = fremark;
    }

    public Integer getFuid() {
        return fuid;
    }

    public void setFuid(Integer fuid) {
        this.fuid = fuid;
    }

}