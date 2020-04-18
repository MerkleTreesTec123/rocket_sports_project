package com.qkwl.common.dto.admin;

import java.io.Serializable;
import java.util.Date;

public class FCsQuestion implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Integer fid;

    private String fquestion;

    private String foperation;

    private Integer fstatus;

    private Date fcreatetime;

    private Date fupdatetime;

    private Integer version;

    private String fdetail;

    private String fresult;

    private Integer fuid;

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public String getFquestion() {
        return fquestion;
    }

    public void setFquestion(String fquestion) {
        this.fquestion = fquestion;
    }

    public String getFoperation() {
        return foperation;
    }

    public void setFoperation(String foperation) {
        this.foperation = foperation;
    }

    public Integer getFstatus() {
        return fstatus;
    }

    public void setFstatus(Integer fstatus) {
        this.fstatus = fstatus;
    }

    public Date getFcreatetime() {
        return fcreatetime;
    }

    public void setFcreatetime(Date fcreatetime) {
        this.fcreatetime = fcreatetime;
    }

    public Date getFupdatetime() {
        return fupdatetime;
    }

    public void setFupdatetime(Date fupdatetime) {
        this.fupdatetime = fupdatetime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getFdetail() {
        return fdetail;
    }

    public void setFdetail(String fdetail) {
        this.fdetail = fdetail == null ? null : fdetail.trim();
    }

    public String getFresult() {
        return fresult;
    }

    public void setFresult(String fresult) {
        this.fresult = fresult == null ? null : fresult.trim();
    }

    public Integer getFuid() {
        return fuid;
    }

    public void setFuid(Integer fuid) {
        this.fuid = fuid;
    }
}