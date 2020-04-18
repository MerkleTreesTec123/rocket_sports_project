package com.qkwl.common.dto.daylog;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 人民币充值&杠杆天日志
 * @author TT
 */
public class FDayCapitalRmb {
	// 主键ID
    private Integer fid;
    // 银行转账
    private BigDecimal fbank;
    // 支付宝充值
    private BigDecimal fzfb;
    // 微信充值
    private BigDecimal fwx;
    // 丰付网银直冲
    private BigDecimal fsuma;
    // 提现
    private BigDecimal fwithdraw;
    // 提现-自动提现
    private BigDecimal fwithdrawother;
    // 等待提现
    private BigDecimal fwithdrawwait;
    // 手续费
    private BigDecimal ffees;
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

    public BigDecimal getFbank() {
        return fbank;
    }

    public void setFbank(BigDecimal fbank) {
        this.fbank = fbank;
    }

    public BigDecimal getFzfb() {
        return fzfb;
    }

    public void setFzfb(BigDecimal fzfb) {
        this.fzfb = fzfb;
    }

    public BigDecimal getFwx() {
        return fwx;
    }

    public void setFwx(BigDecimal fwx) {
        this.fwx = fwx;
    }

    public BigDecimal getFsuma() {
        return fsuma;
    }

    public void setFsuma(BigDecimal fsuma) {
        this.fsuma = fsuma;
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

    public BigDecimal getFwithdrawother() {
        return fwithdrawother;
    }

    public void setFwithdrawother(BigDecimal fwithdrawother) {
        this.fwithdrawother = fwithdrawother;
    }

    public BigDecimal getFfees() {
        return ffees;
    }

    public void setFfees(BigDecimal ffees) {
        this.ffees = ffees;
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
}