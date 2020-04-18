package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

/**
 * ICO奖励
 * @author ZKF
 */
public class FIcoReward implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer fid;
	//奖励类型
    private Integer ftype;
    //ICO项目id
    private Integer ficoid;
    //用户id
    private Integer fuid;
    //奖励数量
    private Integer freward;
    //备注
    private String fremark;

    private Date fcreatetime;
    
    private Date fupdatetime;
    
    private Integer version;

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getFtype() {
        return ftype;
    }

    public void setFtype(Integer ftype) {
        this.ftype = ftype;
    }

    public Integer getFuid() {
        return fuid;
    }

    public void setFuid(Integer fuid) {
        this.fuid = fuid;
    }

    public Integer getFreward() {
        return freward;
    }

    public void setFreward(Integer freward) {
        this.freward = freward;
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

	public Integer getFicoid() {
		return ficoid;
	}

	public void setFicoid(Integer ficoid) {
		this.ficoid = ficoid;
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