package com.qkwl.common.dto.daylog;

import java.util.Date;
import java.math.BigDecimal;
/**
 * 交易天日志
 * @author TT
 */
public class FDayTradeCoin {
	// 主键ID
    private Integer fid;
    // 虚拟币ID
    private Integer fcoinid;
    // 买
    private BigDecimal fbuy;
    // 卖
    private BigDecimal fsell;
    // 买手续费
    private BigDecimal fbuyfees;
    // 卖手续费
    private BigDecimal fsellfees;
    // 买单交易人数
    private Integer fbuyperson;
    // 卖单交易人数
    private Integer fsellperson;
    // 买单笔数
    private Integer fbuyentrust;
    // 卖单笔数
    private Integer fsellentrust;
    // 更新时间
    private Date fupdatetime;
    // 创建时间
    private Date fcreatetime;
    
    private String fcoinname;
    // 券商id
    private Integer fagentid;
    // 券商名称
    private String fagentname;

    public Integer getFagentid() {
		return fagentid;
	}

	public void setFagentid(Integer fagentid) {
		this.fagentid = fagentid;
	}

	public String getFagentname() {
		return fagentname;
	}

	public void setFagentname(String fagentname) {
		this.fagentname = fagentname;
	}
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

    public BigDecimal getFbuy() {
        return fbuy;
    }

    public void setFbuy(BigDecimal fbuy) {
        this.fbuy = fbuy;
    }

    public BigDecimal getFsell() {
        return fsell;
    }

    public void setFsell(BigDecimal fsell) {
        this.fsell = fsell;
    }

	public BigDecimal getFbuyfees() {
		return fbuyfees;
	}

	public void setFbuyfees(BigDecimal fbuyfees) {
		this.fbuyfees = fbuyfees;
	}

	public BigDecimal getFsellfees() {
		return fsellfees;
	}

	public void setFsellfees(BigDecimal fsellfees) {
		this.fsellfees = fsellfees;
	}

	public Integer getFbuyperson() {
		return fbuyperson;
	}

	public void setFbuyperson(Integer fbuyperson) {
		this.fbuyperson = fbuyperson;
	}

	public Integer getFsellperson() {
		return fsellperson;
	}

	public void setFsellperson(Integer fsellperson) {
		this.fsellperson = fsellperson;
	}

	public Integer getFbuyentrust() {
		return fbuyentrust;
	}

	public void setFbuyentrust(Integer fbuyentrust) {
		this.fbuyentrust = fbuyentrust;
	}

	public Integer getFsellentrust() {
		return fsellentrust;
	}

	public void setFsellentrust(Integer fsellentrust) {
		this.fsellentrust = fsellentrust;
	}

	public Date getFupdatetime() {
		return fupdatetime;
	}

	public void setFupdatetime(Date fupdatetime) {
		this.fupdatetime = fupdatetime;
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public String getFcoinname() {
		return fcoinname;
	}

	public void setFcoinname(String fcoinname) {
		this.fcoinname = fcoinname;
	}
}