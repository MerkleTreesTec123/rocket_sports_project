package com.qkwl.service.activity.model;

import java.util.Date;

/**
 * 邮件流水数据模型
 */
public class ValidateEmailDO {
    private Integer id;
    // 用户id
    private Integer uid;
    // 邮箱
    private String email;
    // 平台类型 PlatformEnum
    private Integer platform;
    // 模板id
    private Integer templateId;
    // 标题
    private String title;
    // 验证码
    private String code;
    // uuid
    private String uuid;
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
    private String alias;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}