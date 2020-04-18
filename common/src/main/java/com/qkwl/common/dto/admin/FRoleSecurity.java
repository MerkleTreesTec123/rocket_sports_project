package com.qkwl.common.dto.admin;

import java.io.Serializable;

/**
 * 管理员权限
 * @author TT
 */
public class FRoleSecurity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 权限ID
	private Integer fsecurityid;
	// 角色ID
	private Integer froleid;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public Integer getFsecurityid() {
		return fsecurityid;
	}

	public void setFsecurityid(Integer fsecurityid) {
		this.fsecurityid = fsecurityid;
	}

	public Integer getFroleid() {
		return froleid;
	}

	public void setFroleid(Integer froleid) {
		this.froleid = froleid;
	}
}