package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

/**
 * 靓号
 * 
 * @author ZKF
 */
public class FBeautiful implements Serializable {
	private static final long serialVersionUID = 1L;
	// 主键id
	private Integer fid;
	// 靓号替换用户ID
	private Integer fuid;
	// 靓号ID
	private Integer fbid;
	// 创建时间
	private Date fcreatetime;
	// 使用时间
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

	public Integer getFbid() {
		return fbid;
	}

	public void setFbid(Integer fbid) {
		this.fbid = fbid;
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

}