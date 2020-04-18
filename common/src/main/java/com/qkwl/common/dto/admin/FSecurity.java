package com.qkwl.common.dto.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限
 * @author TT
 */
public class FSecurity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 描述
	private String fdescription;
	// 权限名称
	private String fname;
	// 排序
	private Integer fpriority;
	// 父级ID
	private Integer fparentid;
	// 权限URL
	private String furl;
	
	private List<FSecurity> fsecurities = new ArrayList<FSecurity>();

	public List<FSecurity> getFsecurities() {
		return fsecurities;
	}

	public void setFsecurities(List<FSecurity> fsecurities) {
		this.fsecurities = fsecurities;
	}

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

	public Integer getFpriority() {
		return fpriority;
	}

	public void setFpriority(Integer fpriority) {
		this.fpriority = fpriority;
	}

	public Integer getFparentid() {
		return fparentid;
	}

	public void setFparentid(Integer fparentid) {
		this.fparentid = fparentid;
	}

	public String getFurl() {
		return furl;
	}

	public void setFurl(String furl) {
		this.furl = furl == null ? null : furl.trim();
	}
}