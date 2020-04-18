package com.qkwl.service.activity.model;

import java.util.Date;

/**
 * 平台数据模型
 */
public class ValidatePlatformDO {
    private Integer id;
    // 平台名称
    private String name;
    // 域名
    private String domain;
    // 签名
    private String sign;
    // 普通短信账号id
    private Integer smsId;
    // 语音短信账号id
    private Integer voiceSmsId;
    // 国际短信账号id
    private Integer internationalSmsId;
    // 邮件账号id
    private Integer emailId;
    // 创建时间
    private Date gmtCreate;
    // 修改时间
    private Date gmtModified;
    // 版本号
    private Integer version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain == null ? null : domain.trim();
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign == null ? null : sign.trim();
    }

    public Integer getSmsId() {
        return smsId;
    }

    public void setSmsId(Integer smsId) {
        this.smsId = smsId;
    }

    public Integer getVoiceSmsId() {
        return voiceSmsId;
    }

    public void setVoiceSmsId(Integer voiceSmsId) {
        this.voiceSmsId = voiceSmsId;
    }

    public Integer getInternationalSmsId() {
        return internationalSmsId;
    }

    public void setInternationalSmsId(Integer internationalSmsId) {
        this.internationalSmsId = internationalSmsId;
    }

    public Integer getEmailId() {
        return emailId;
    }

    public void setEmailId(Integer emailId) {
        this.emailId = emailId;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}