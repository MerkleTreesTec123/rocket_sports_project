package com.qkwl.common.dto.api;

import com.qkwl.common.dto.Enum.UserStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户ApiAuth
 * @author ZKF
 */
public class FApiAuth implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer fid;

    private Integer fuid;

    //访问key
    private String fapikey;

    //访问密钥
    private String fsecretkey;

    //访问ip
    private String fip;

    //状态
    private Integer fstatus;

    //自定义的手续费比例
    private BigDecimal frate;

    //是否开启自定义手续费
    private Integer fopenrate;

    //api的访问次数
    private Integer fcount;

    //请求的api
    private String frqip;

    private Date fcreatetime;

    private Date fupdatetime;

    public BigDecimal getFrate() {
        return frate;
    }

    public void setFrate(BigDecimal frate) {
        this.frate = frate;
    }

    public Integer getFopenrate() {
        return fopenrate;
    }

    public void setFopenrate(Integer fopenrate) {
        this.fopenrate = fopenrate;
    }

    public Integer getFcount() {
        return fcount;
    }

    public void setFcount(Integer fcount) {
        this.fcount = fcount;
    }

    public String getFrqip() {
        return frqip;
    }

    public void setFrqip(String frqip) {
        this.frqip = frqip;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getFuid() {
        return fuid;
    }

    public void setFuid(Integer fuid) {
        this.fuid = fuid;
    }

    public String getFapikey() {
        return fapikey;
    }

    public void setFapikey(String fapikey) {
        this.fapikey = fapikey == null ? null : fapikey.trim();
    }

    public String getFsecretkey() {
        return fsecretkey;
    }

    public void setFsecretkey(String fsecretkey) {
        this.fsecretkey = fsecretkey == null ? null : fsecretkey.trim();
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

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	public String getFip() {
		return fip;
	}

	public void setFip(String fip) {
		this.fip = fip;
	}

	public boolean isValid(){
        return fstatus == UserStatusEnum.NORMAL_VALUE;
    }

}