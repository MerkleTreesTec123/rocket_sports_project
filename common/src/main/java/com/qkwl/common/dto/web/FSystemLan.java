package com.qkwl.common.dto.web;

import java.io.Serializable;

/**
 * 语言配置
 * @author LY
 */
public class FSystemLan implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 名称
	private String fname;
	// 缩写
	private String fshortname;
	// 包名
	private String fpackagename;
	// 描述
	private String fdescription;
	// 状态
	private Integer fstatus;
	// 版本
	private Integer version;
	// 排序
	private Integer fsortid;
	// 类型
	private String ftype;
	// 是否需要实名
	private Byte fisrealname;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname == null ? null : fname.trim();
	}

	public String getFshortname() {
		return fshortname;
	}

	public void setFshortname(String fshortname) {
		this.fshortname = fshortname == null ? null : fshortname.trim();
	}

	public String getFpackagename() {
		return fpackagename;
	}

	public void setFpackagename(String fpackagename) {
		this.fpackagename = fpackagename == null ? null : fpackagename.trim();
	}

	public String getFdescription() {
		return fdescription;
	}

	public void setFdescription(String fdescription) {
		this.fdescription = fdescription == null ? null : fdescription.trim();
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getFsortid() {
		return fsortid;
	}

	public void setFsortid(Integer fsortid) {
		this.fsortid = fsortid;
	}

	public String getFtype() {
		return ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype == null ? null : ftype.trim();
	}

	public Byte getFisrealname() {
		return fisrealname;
	}

	public void setFisrealname(Byte fisrealname) {
		this.fisrealname = fisrealname;
	}
}