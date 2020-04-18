package com.qkwl.common.dto.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员类别
 * @author TT
 */
public class FRole implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 描述
	private String fdescription;
	// 名称
	private String fname;
	
	private List<FRoleSecurity> froleSecurities = new ArrayList<FRoleSecurity>();

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getFdescription() {
		return fdescription;
	}

	public void setFdescription(String fdescription) {
		this.fdescription = fdescription == null ? null : fdescription.trim();
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname == null ? null : fname.trim();
	}

	public List<FRoleSecurity> getFroleSecurities() {
		return froleSecurities;
	}

	public void setFroleSecurities(List<FRoleSecurity> froleSecurities) {
		this.froleSecurities = froleSecurities;
	}
}