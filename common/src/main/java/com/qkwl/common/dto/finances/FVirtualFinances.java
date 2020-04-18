package com.qkwl.common.dto.finances;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 存币理财配置
 * @author LY
 *
 */
public class FVirtualFinances implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// 主键ID
    private Integer fid;
    // 币种ID
    private Integer fcoinid;
    // 理财类型
    private String fname;
    // 冻结天数
    private Integer fdays;
    // 利率
    private BigDecimal frate;
    // 状态
    private Integer fstate;
    // 创建时间
    private Date fcreatetime;
    // 更新时间
    private Date fupdatetime;
    // 版本号
    private Integer version;

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getFcoinid() {
		return fcoinid;
	}

	public void setFcoinid(Integer fcoinid) {
		this.fcoinid = fcoinid;
	}

	public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname == null ? null : fname.trim();
    }

    public Integer getFdays() {
		return fdays;
	}

	public void setFdays(Integer fdays) {
		this.fdays = fdays;
	}

	public BigDecimal getFrate() {
        return frate;
    }

    public void setFrate(BigDecimal frate) {
        this.frate = frate;
    }

    public Integer getFstate() {
        return fstate;
    }

    public void setFstate(Integer fstate) {
        this.fstate = fstate;
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
}