package com.qkwl.common.dto.finances;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.qkwl.common.dto.Enum.UserFinancesStateEnum;
/**
 * 存币理财
 * @author LY
 *
 */
public class FUserFinancesDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// 主键ID
    private Integer fid;
    // 用户ID
    private Integer fuid;
    // 币种ID
    private Integer fcoinid;
    private String fcoin_s;
    // 理财类型
    private String fname;
    // 理财利率
    private BigDecimal frate;
    // 存入金额
    private BigDecimal famount;
    // 预计收益
    private BigDecimal fplanamount;
    // 状态
    private Integer fstate;
    private String fstate_s;
    // 创建时间
    private Date fcreatetime;
    // 预计发放时间
    private Date fupdatetime;
    // 版本号
    private Integer version;

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

    public BigDecimal getFrate() {
        return frate;
    }

    public void setFrate(BigDecimal frate) {
        this.frate = frate;
    }

    public BigDecimal getFamount() {
        return famount;
    }

    public void setFamount(BigDecimal famount) {
        this.famount = famount;
    }

    public BigDecimal getFplanamount() {
        return fplanamount;
    }

    public void setFplanamount(BigDecimal fplanamount) {
        this.fplanamount = fplanamount;
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

	public String getFcoin_s() {
		return fcoin_s;
	}

	public void setFcoin_s(String fcoin_s) {
		this.fcoin_s = fcoin_s;
	}

	public String getFstate_s() {
		this.fstate_s = UserFinancesStateEnum.getValueByCode(this.fstate);
		return fstate_s;
	}

	public void setFstate_s(String fstate_s) {
		this.fstate_s = fstate_s;
	}
    
}