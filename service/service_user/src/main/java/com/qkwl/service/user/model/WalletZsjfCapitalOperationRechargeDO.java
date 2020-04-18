package com.qkwl.service.user.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 质数金服 人民币资金操作实体
 */
public class WalletZsjfCapitalOperationRechargeDO implements Serializable{
    private static final long serialVersionUID = 102510L;


    private Integer id;
    private Integer uid;        //用户id
    private Integer coinId;     //币种id
    private String phone;       //用户电话号码
    private String email;       //邮件地址
    private Integer adminId;    //管理员id
    private Integer userBankId;//用户银行卡id
    private String serialNumber;//来账流水号
    private String realName;    //付款人姓名
    private String bankName;    //付款行行名
    private String bankNumber;  //付款人银行卡号
    private BigDecimal amount;  //金额
    private BigDecimal fees;    //手续费
    private Integer status;     //状态
    private String postscript;  //附言
    private Date gmtCreate;     //创建时间
    private Date gmtModified;   //更新时间
    private Integer source;     //来源
    private Integer platform;   //来源
    private Integer version;    //乐观锁

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getUserBankId() {
        return userBankId;
    }

    public void setUserBankId(Integer userBankId) {
        this.userBankId = userBankId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPostscript() {
        return postscript;
    }

    public void setPostscript(String postscript) {
        this.postscript = postscript;
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

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "WalletZsjfCapitalOperationRechargeDO{" +
                "id=" + id +
                ", uid=" + uid +
                ", coinId=" + coinId +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", adminId=" + adminId +
                ", userBankId=" + userBankId +
                ", serialNumber='" + serialNumber + '\'' +
                ", realName='" + realName + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankNumber='" + bankNumber + '\'' +
                ", amount=" + amount +
                ", fees=" + fees +
                ", status=" + status +
                ", postscript='" + postscript + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", source=" + source +
                ", platform=" + platform +
                ", version=" + version +
                '}';
    }
}
