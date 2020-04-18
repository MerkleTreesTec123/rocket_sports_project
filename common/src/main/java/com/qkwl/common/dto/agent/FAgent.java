package com.qkwl.common.dto.agent;

import java.io.Serializable;
import java.util.Date;

/**
 * 券商实体
 * @author ZKF
 */
public class FAgent implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer fid;
    //姓名
    private String fname;
    //手机
    private String fphone;
    //域名
    private String fdomain;
    //备注
    private String fremark;
    //时间
    private Date fcreatetime;

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname == null ? null : fname.trim();
    }

    public String getFphone() {
        return fphone;
    }

    public void setFphone(String fphone) {
        this.fphone = fphone == null ? null : fphone.trim();
    }

    public String getFdomain() {
        return fdomain;
    }

    public void setFdomain(String fdomain) {
        this.fdomain = fdomain == null ? null : fdomain.trim();
    }

    public String getFremark() {
        return fremark;
    }

    public void setFremark(String fremark) {
        this.fremark = fremark == null ? null : fremark.trim();
    }

    public Date getFcreatetime() {
        return fcreatetime;
    }

    public void setFcreatetime(Date fcreatetime) {
        this.fcreatetime = fcreatetime;
    }
}