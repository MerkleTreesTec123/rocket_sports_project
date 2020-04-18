package com.qkwl.common.dto.validate;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by wangchen on 2017-07-03.
 */
public class ValidateParamInfo implements Serializable{

    static final long serialVersionUID = 124202L;

    // 区号
    private String areaCode;
    // 手机号
    private String phone;
    // 验证码
    private String code;
    // 创建时间
    private Timestamp createTime;

    // 第二次验证的区号
    private String secondAreaCode;
    // 第二次验证的手机号
    private String secondPhone;
    // 第二次验证的验证码
    private String secondCode;
    // 第二次验证的创建时间
    private Timestamp secondCreateTime;

    // 验证码
    private String totpCode;
    // 谷歌key
    private String totpKey;
    // 请求的地址
    private String ip;
    // 业务类型
    private BusinessTypeEnum businessType;
    // 来源平台
    private PlatformEnum platform;
    // 语言类型
    private LocaleEnum locale;
    // 邮箱唯一标识
    private String uuid;
    // 原始登陆密码
    private String originLoginPwd;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getSecondAreaCode() {
        return secondAreaCode;
    }

    public void setSecondAreaCode(String secondAreaCode) {
        this.secondAreaCode = secondAreaCode;
    }

    public String getSecondPhone() {
        return secondPhone;
    }

    public void setSecondPhone(String secondPhone) {
        this.secondPhone = secondPhone;
    }

    public String getSecondCode() {
        return secondCode;
    }

    public void setSecondCode(String secondCode) {
        this.secondCode = secondCode;
    }

    public Timestamp getSecondCreateTime() {
        return secondCreateTime;
    }

    public void setSecondCreateTime(Timestamp secondCreateTime) {
        this.secondCreateTime = secondCreateTime;
    }

    public String getTotpCode() {
        return totpCode;
    }

    public void setTotpCode(String totpCode) {
        this.totpCode = totpCode;
    }

    public String getTotpKey() {
        return totpKey;
    }

    public void setTotpKey(String totpKey) {
        this.totpKey = totpKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformEnum platform) {
        this.platform = platform;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LocaleEnum getLocale() {
        return locale;
    }

    public void setLocale(LocaleEnum locale) {
        this.locale = locale;
    }

    public String getOriginLoginPwd() {
        return originLoginPwd;
    }

    public void setOriginLoginPwd(String originLoginPwd) {
        this.originLoginPwd = originLoginPwd;
    }
}
