package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户众筹钱包
 * @author ZKF
 */
public class FIcoWallet implements Serializable{
	private static final long serialVersionUID = 1L;
	//众筹钱包id
	private Integer fid;
	//众筹项目id
    private Integer ficoid;
    //用户id
    private Integer fuid;
    //众筹总量
    private BigDecimal famount;
    //代币/股份
    private Integer fshares;
    //奖励数
    private Integer freward;
    //变更次数
    private Integer version;
    //更新时间
    private Date fupdatetime;
    /************扩张字段************/
    
    //用户名
    private String floginname;
    //项目名
    private String ficoname;
    //众筹币种单位
    private String fcoinunit;
    //代币单位
    private String fsharesunit;
    

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getFicoid() {
        return ficoid;
    }

    public void setFicoid(Integer ficoid) {
        this.ficoid = ficoid;
    }

    public Integer getFuid() {
        return fuid;
    }

    public void setFuid(Integer fuid) {
        this.fuid = fuid;
    }

    public BigDecimal getFamount() {
        return famount;
    }

    public void setFamount(BigDecimal famount) {
        this.famount = famount;
    }

    public Integer getFshares() {
        return fshares;
    }

    public void setFshares(Integer fshares) {
        this.fshares = fshares;
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

	public String getFloginname() {
		return floginname;
	}

	public void setFloginname(String floginname) {
		this.floginname = floginname;
	}

	public String getFiconame() {
		return ficoname;
	}

	public void setFiconame(String ficoname) {
		this.ficoname = ficoname;
	}

	public String getFcoinunit() {
		return fcoinunit;
	}

	public void setFcoinunit(String fcoinunit) {
		this.fcoinunit = fcoinunit;
	}

	public String getFsharesunit() {
		return fsharesunit;
	}

	public void setFsharesunit(String fsharesunit) {
		this.fsharesunit = fsharesunit;
	}

	public Integer getFreward() {
		return freward;
	}

	public void setFreward(Integer freward) {
		this.freward = freward;
	}
    
    
}