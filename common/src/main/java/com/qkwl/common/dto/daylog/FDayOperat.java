package com.qkwl.common.dto.daylog;

import java.util.Date;

/**
 * 运营天日志
 * @author TT
 */
public class FDayOperat {
	// 主键ID
    private Integer fid;
    // 登录
    private Integer flogin;
    // 注册
    private Integer fregister;
    // 实名
    private Integer frealname;
    // 短信
    private Integer fsms;
    // 邮件
    private Integer fmail;
    // VIP6
    private Integer fvip6;
    // 充值码
    private Integer fcode;
    // 积分
    private Integer fscore;
    // 提问
    private Integer fsubmitquestion;
    // 回复提问
    private Integer freplyquestion;
    // 更新时间
    private Date fupdatetime;
    // 创建时间
    private Date fcreatetime;
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

    public Integer getFlogin() {
		return flogin;
	}

	public void setFlogin(Integer flogin) {
		this.flogin = flogin;
	}

	public Integer getFregister() {
		return fregister;
	}

	public void setFregister(Integer fregister) {
		this.fregister = fregister;
	}

	public Integer getFrealname() {
		return frealname;
	}

	public void setFrealname(Integer frealname) {
		this.frealname = frealname;
	}

	public Integer getFsms() {
        return fsms;
    }

    public void setFsms(Integer fsms) {
        this.fsms = fsms;
    }

    public Integer getFmail() {
        return fmail;
    }

    public void setFmail(Integer fmail) {
        this.fmail = fmail;
    }

    public Integer getFvip6() {
        return fvip6;
    }

    public void setFvip6(Integer fvip6) {
        this.fvip6 = fvip6;
    }

    public Integer getFcode() {
        return fcode;
    }

    public void setFcode(Integer fcode) {
        this.fcode = fcode;
    }

    public Integer getFscore() {
		return fscore;
	}

	public void setFscore(Integer fscore) {
		this.fscore = fscore;
	}

	public Integer getFsubmitquestion() {
        return fsubmitquestion;
    }

    public void setFsubmitquestion(Integer fsubmitquestion) {
        this.fsubmitquestion = fsubmitquestion;
    }

    public Integer getFreplyquestion() {
        return freplyquestion;
    }

    public void setFreplyquestion(Integer freplyquestion) {
        this.freplyquestion = freplyquestion;
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
}