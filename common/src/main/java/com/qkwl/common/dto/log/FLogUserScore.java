package com.qkwl.common.dto.log;

import com.qkwl.common.dto.Enum.ScoreTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户积分日志（流水）
 * @author ZKF
 */
public class FLogUserScore implements Serializable {
	private static final long serialVersionUID = 1L;

	// fid
	private Integer fid;
	// 用户id
	private Integer fuid;
	// 积分
	private Integer fscore;
	// 积分类型
	private Integer ftype;
	// 备注
	private String fremark;
	// 时间
	private Date fcreatetime;

	/****** 扩展字段 ********/
	private BigDecimal amount;
	private String ftype_s;

	public FLogUserScore() {}

    public FLogUserScore(Integer fuid, BigDecimal amount, Integer ftype, String fremark) {
        this.fuid = fuid;
        this.ftype = ftype;
        this.fremark = fremark;
        this.amount = amount;
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

	public Integer getFscore() {
		return fscore;
	}

	public void setFscore(Integer fscore) {
		this.fscore = fscore;
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
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

	public BigDecimal getAmount() {
		return amount;
	}

    public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public String getFtype_s() {
		this.ftype_s = ScoreTypeEnum.getValueByCode(this.ftype);
		return ftype_s;
	}

	public void setFtype_s(String ftype_s) {
		this.ftype_s = ftype_s;
	}
}