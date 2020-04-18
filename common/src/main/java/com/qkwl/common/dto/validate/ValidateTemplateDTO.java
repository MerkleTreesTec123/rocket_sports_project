package com.qkwl.common.dto.validate;

import com.qkwl.common.Enum.validate.*;

import java.util.Date;

/**
 * 消息模板数据模型
 */
public class ValidateTemplateDTO {
    private Integer id;
    // 模板类型
    private Integer sendType;
    // 业务类型
    private String businessType;
    // 平台类型
    private Integer platform;
    // 语言类型
    private Integer language;
    // 模板内容
    private String template;
    // 创建时间
    private Date gmtCreate;
    // 参数集
    private String params;
    // 修改时间
    private Date gmtModified;
    // 版本号
    private Integer version;

    // 扩展字段
    private String sendType_s;

    private String businessType_s;

    private String platformName;

    private String languageName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template == null ? null : template.trim();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
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

    public String getSendType_s() {
        return SendTypeEnum.getValueByCode(this.getSendType());
    }

    public void setSendType_s(String sendType_s) {
        this.sendType_s = sendType_s;
    }

    public String getBusinessType_s() {
        String[] types = this.getBusinessType().split("#");
        StringBuilder businessType_s = new StringBuilder();
        for(int i = 0; i < types.length; i++){
            businessType_s.append(BusinessTypeEnum.getValueByCode(Integer.valueOf(types[i]))).append(";");
        }
        return businessType_s.toString();
    }

    public void setBusinessType_s(String businessType_s) {
        this.businessType_s = businessType_s;
    }

    public String getPlatformName() {
        return PlatformEnum.getValueByCode(this.getPlatform());
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getLanguageName() {
        return LocaleEnum.getValueByCode(this.getLanguage());
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}