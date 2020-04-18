package com.qkwl.common.dto.activity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FActivityRecord implements Serializable {
    
	private static final long serialVersionUID = -561557086533647024L;

	private Integer fid;

    private Integer fuid;
    
    private Integer fintrouid;
    
    private Integer ftype;

    private BigDecimal famount;
    
    private BigDecimal frecharge;

    private String fremark;

    private Date fcreatetime;

    private Integer version;
    
    private Integer fstate;
    
    private Integer fcoinid;

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

    public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	public BigDecimal getFamount() {
        return famount;
    }

    public void setFamount(BigDecimal famount) {
        this.famount = famount;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

	public Integer getFintrouid() {
		return fintrouid;
	}

	public void setFintrouid(Integer fintrouid) {
		this.fintrouid = fintrouid;
	}

	public BigDecimal getFrecharge() {
		return frecharge;
	}

	public void setFrecharge(BigDecimal frecharge) {
		this.frecharge = frecharge;
	}

	public Integer getFstate() {
		return fstate;
	}

	public void setFstate(Integer fstate) {
		this.fstate = fstate;
	}

	public Integer getFcoinid() {
		return fcoinid;
	}

	public void setFcoinid(Integer fcoinid) {
		this.fcoinid = fcoinid;
	}
    
}