package com.qkwl.common.dto.capital;

import java.math.BigDecimal;

/**
 * PUSH DTO
 */
public class UserPushOrderDTO {
    /**
     * push用户ID
     */
    private Integer pushuId;
    /**
     * 币种ID
     */
    private Integer coinId;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 数量
     */
    private BigDecimal count;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 短信验证码
     */
    private String phoneCode;

    /**
     * 谷歌验证码
     */
    private String googleCode;
    /**
     * 交易密码
     */
    private String tradePass;
    /**
     * 操作IP
     */
    private String ip;
    /**
     * 平台ID
     */
    private Integer platformId;

    public Integer getPushuId() {
        return pushuId;
    }

    public void setPushuId(Integer pushuId) {
        this.pushuId = pushuId;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getGoogleCode() {
        return googleCode;
    }

    public void setGoogleCode(String googleCode) {
        this.googleCode = googleCode;
    }

    public String getTradePass() {
        return tradePass;
    }

    public void setTradePass(String tradePass) {
        this.tradePass = tradePass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    @Override
    public String toString() {
        return "UserPushOrderDTO{" +
                "pushuId=" + pushuId +
                ", coinId=" + coinId +
                ", price=" + price +
                ", count=" + count +
                ", userId=" + userId +
                ", phoneCode='" + phoneCode + '\'' +
                ", googleCode='" + googleCode + '\'' +
                ", tradePass='" + tradePass + '\'' +
                ", ip='" + ip + '\'' +
                ", platformId=" + platformId +
                '}';
    }
}
