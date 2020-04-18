package com.qkwl.common.dto.validate;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 消息发送数据传输对象
 * Created by ZKF on 2017/5/4.
 */
public class ValidateSendDTO {

    // 平台
    private Integer platformType;
    // 发送类型
    private Integer sendType;
    // 业务类型
    private Integer businessType;
    // 语言类型
    private Integer languageType;
    // UUID
    private String uuid;
    // 验证码
    private String code;

    // 扩展参数
    // 价格
    private BigDecimal price;
    // 用户价格
    private BigDecimal userPrice;
    // 数量
    private BigDecimal amount;
    // 用户id
    private Integer uid;
    // 充提类型
    private String type;
    // 币种
    private String coin;
    // 手机
    private String phone;
    // 邮箱
    private String email;
    // 用户登录名
    private String username;
    // 用户IP
    private String ip;
    // 时间缩写
    private String abbrDate;
    // 自定义URL
    private String customUrl;

    //扩展字段
    private String areaCode;


    public ValidateSendDTO() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getLanguageType() {
        return languageType;
    }

    public void setLanguageType(Integer languageType) {
        this.languageType = languageType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getUserPrice() {
        return userPrice;
    }

    public void setUserPrice(BigDecimal userPrice) {
        this.userPrice = userPrice;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAbbrDate() {
        return abbrDate;
    }

    public void setAbbrDate(String abbrDate) {
        this.abbrDate = abbrDate;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    @Override
    public String toString() {
        return "ValidateSendDTO{" +
                "platformType=" + platformType +
                ", sendType=" + sendType +
                ", businessType=" + businessType +
                ", languageType=" + languageType +
                ", uuid='" + uuid + '\'' +
                ", code='" + code + '\'' +
                ", price=" + price +
                ", userPrice=" + userPrice +
                ", amount=" + amount +
                ", uid=" + uid +
                ", type='" + type + '\'' +
                ", coin='" + coin + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", abbrDate='" + abbrDate + '\'' +
                ", customUrl='" + customUrl + '\'' +
                ", areaCode='" + areaCode + '\'' +
                '}';
    }
}
