package com.qkwl.common.dto.capital;

import com.qkwl.common.Enum.validate.PlatformEnum;

/**
 * 用户银行卡列表
 *
 * @author LY
 */
public class UserBankinfoDTO {

    // Id
    private Integer id;
    // 用户ID
    private Integer userId;
    // 银行卡类型
    private Integer systemBankId;
    // 银行卡号
    private String bankNumber;
    // 开户名
    private String realName;
    // 省
    private String prov;
    // 市
    private String city;
    // 区
    private String dist;
    // 开户地址
    private String address;
    // 类型
    private Integer type;
    //IP
    private String ip;
    // 短信验证码
    private String phoneCode;
    // 谷歌验证码
    private String totpCode;
    // 平台
    private PlatformEnum platform;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSystemBankId() {
        return systemBankId;
    }

    public void setSystemBankId(Integer systemBankId) {
        this.systemBankId = systemBankId;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getTotpCode() {
        return totpCode;
    }

    public void setTotpCode(String totpCode) {
        this.totpCode = totpCode;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformEnum platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "UserBankinfoDTO{" +
                "userId=" + userId +
                ", systemBankId=" + systemBankId +
                ", bankNumber='" + bankNumber + '\'' +
                ", realName='" + realName + '\'' +
                ", prov='" + prov + '\'' +
                ", city='" + city + '\'' +
                ", dist='" + dist + '\'' +
                ", address='" + address + '\'' +
                ", type=" + type +
                ", ip='" + ip + '\'' +
                ", phoneCode='" + phoneCode + '\'' +
                ", totpCode='" + totpCode + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }

}