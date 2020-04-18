package com.qkwl.common.dto.common;

import com.qkwl.common.Enum.validate.PlatformEnum;

/**
 * Created by ZKF on 2017/7/7.
 */
public class BaseParamDTO {

    // 手机验证码
    private String phoneCode;
    // 谷歌验证码
    private String googleCode;
    // ip
    private String ip;
    // password
    private String password;
    // 平台
    private PlatformEnum platform;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformEnum platform) {
        this.platform = platform;
    }
}
