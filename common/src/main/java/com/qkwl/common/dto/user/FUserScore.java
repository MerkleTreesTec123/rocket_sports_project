package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户积分
 * 
 * @author LY
 *
 */
public class FUserScore implements Serializable {
	private static final long serialVersionUID = 1L;
	// 主键ID
	private Integer fid;
	// 积分
	private Long fscore;
	// VIP等级
	private Integer flevel;
	// 版本号
	private Integer version;
	// 每日交易新增积分
	private Integer ftradingqty;
	// VIP6购买时间
	private Date fleveltime;
	// 用户ID
	private Integer fuid;
	
	/***扩展字段***/
	private Integer faddscore;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public Long getFscore() {
		return fscore;
	}

	public void setFscore(Long fscore) {
		this.fscore = fscore;
	}

	public Integer getFlevel() {
		return flevel;
	}

	public void setFlevel(Integer flevel) {
		this.flevel = flevel;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getFtradingqty() {
		return ftradingqty;
	}

	public void setFtradingqty(Integer ftradingqty) {
		this.ftradingqty = ftradingqty;
	}

	public Date getFleveltime() {
		return fleveltime;
	}

	public void setFleveltime(Date fleveltime) {
		this.fleveltime = fleveltime;
	}

	public Integer getFuid() {
		return fuid;
	}

	public void setFuid(Integer fuid) {
		this.fuid = fuid;
	}

	public Integer getFaddscore() {
		return faddscore;
	}

	public void setFaddscore(Integer faddscore) {
		this.faddscore = faddscore;
	}

}