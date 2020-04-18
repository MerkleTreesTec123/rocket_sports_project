package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

/**
 * 连续登陆
 * 
 * @author LY
 *
 */
public class FUserContinuous implements Serializable {
	private static final long serialVersionUID = 1L;

	// 主键id
	private Integer fid;
	// 用户id
	private Integer fuid;
	// 最后登录时间
	private Date flasttime;
	// 连续登陆次数
	private Integer fcontinuouscount;
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

	public Date getFlasttime() {
		return flasttime;
	}

	public void setFlasttime(Date flasttime) {
		this.flasttime = flasttime;
	}

	public Integer getFcontinuouscount() {
		return fcontinuouscount;
	}

	public void setFcontinuouscount(Integer fcontinuouscount) {
		this.fcontinuouscount = fcontinuouscount;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}