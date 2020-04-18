package com.qkwl.common.dto.validate;

import com.qkwl.common.Enum.validate.SendTypeEnum;

import java.util.Date;

/**
 * 账号数据模型
 */
public class ValidateAccountDTO {
    private Integer id;
    // 账号名称
    private String name;
    // 发送类型 SendTypeEnum
    private Integer type;
    // 账号
    private String accessKey;
    // 密码
    private String secretKey;
    // url
    private String url;
    // 创建时间
    private Date gmtCreate;
    // 修改时间
    private Date gmtModified;
    // 版本号
    private Integer version;

    // 扩展字段
    private String type_s;

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
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey == null ? null : accessKey.trim();
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey == null ? null : secretKey.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
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

    public String getType_s() {
        return SendTypeEnum.getValueByCode(this.getType());
    }

    public void setType_s(String type_s) {
        this.type_s = type_s;
    }
}