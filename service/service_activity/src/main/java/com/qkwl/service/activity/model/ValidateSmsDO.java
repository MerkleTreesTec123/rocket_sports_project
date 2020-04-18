package com.qkwl.service.activity.model;

import java.util.Date;

/**
 * 短信流水数据模型
 */
public class ValidateSmsDO {
    private Integer id;
    // 用户id
    private Integer uid;
    // 手机号
    private String phone;
    // 发送类型
    private Integer sendType;
    // 平台枚举
    private Integer platform;
    // 模板id
    private Integer templateId;
    // 发送状态
    private Integer status;
    // 创建时间
    private Date gmtCreate;
    // 发送时间
    private Date gmtSend;
    // 版本号
    private Integer version;
    // 内容
    private String content;
    // 发送次数
    private Integer times;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtSend() {
        return gmtSend;
    }

    public void setGmtSend(Date gmtSend) {
        this.gmtSend = gmtSend;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }
}