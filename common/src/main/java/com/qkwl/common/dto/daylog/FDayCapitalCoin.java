package com.qkwl.common.dto.daylog;

import java.util.Date;
import java.math.BigDecimal;
/**
 * 虚拟币充值&杠杆天日志
 * @author TT
 */
public class FDayCapitalCoin {
	// 主键ID
    private Integer fid;
    // 虚拟币ID
    private Integer fcoinid;
    // 虚拟币名称（非数据库字段）
    private String fcoinname;
    // 充值
    private BigDecimal frecharge;
    // 提现
    private BigDecimal fwithdraw;
    // 等待提现
    private BigDecimal fwithdrawwait;
    // 手续费
    private BigDecimal ffees;
    // 网络手续费
    private BigDecimal fnetfees;
    // 杠杆申请
    private BigDecimal fleverborrow;
    // 杠杆归还
    private BigDecimal fleverrepay;
    // 创建时间
    private Date fcreatetime;
    // 更新时间
    private Date fupdatetime;
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

    public BigDecimal getFrecharge() {
        return frecharge;
    }

    public void setFrecharge(BigDecimal frecharge) {
        this.frecharge = frecharge;
    }

    public BigDecimal getFwithdraw() {
        return fwithdraw;
    }

    public void setFwithdraw(BigDecimal fwithdraw) {
        this.fwithdraw = fwithdraw;
    }

    public BigDecimal getFwithdrawwait() {
		return fwithdrawwait;
	}

	public void setFwithdrawwait(BigDecimal fwithdrawwait) {
		this.fwithdrawwait = fwithdrawwait;
	}

	public BigDecimal getFfees() {
        return ffees;
    }

    public void setFfees(BigDecimal ffees) {
        this.ffees = ffees;
    }

    public BigDecimal getFnetfees() {
        return fnetfees;
    }

    public void setFnetfees(BigDecimal fnetfees) {
        this.fnetfees = fnetfees;
    }

	public BigDecimal getFleverborrow() {
		return fleverborrow;
	}

	public void setFleverborrow(BigDecimal fleverborrow) {
		this.fleverborrow = fleverborrow;
	}

	public BigDecimal getFleverrepay() {
		return fleverrepay;
	}

	public void setFleverrepay(BigDecimal fleverrepay) {
		this.fleverrepay = fleverrepay;
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

	public String getFcoinname() {
		return fcoinname;
	}

	public void setFcoinname(String fcoinname) {
		this.fcoinname = fcoinname;
	}
	
	
}