package com.qkwl.common.dto.system;

import java.io.Serializable;

/**
 * 提现银行卡列表
 * @author LY
 */
public class FSystemBankinfoWithdraw implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 中文名称
	private String fcnname;
	// 繁体名称
	private String ftwname;
	// 英文名称
	private String fenname;
	// 银行logo
	private String flogo;
	// 类型
	private Integer ftype;
	// 排序
	private Integer fsort;
	// 状态
	private Boolean fstate;
	// 银行行号
	private String bankCode;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getFcnname() {
		return fcnname;
	}

	public void setFcnname(String fcnname) {
		this.fcnname = fcnname == null ? null : fcnname.trim();
	}

	public String getFtwname() {
		return ftwname;
	}

	public void setFtwname(String ftwname) {
		this.ftwname = ftwname == null ? null : ftwname.trim();
	}

	public String getFenname() {
		return fenname;
	}

	public void setFenname(String fenname) {
		this.fenname = fenname == null ? null : fenname.trim();
	}

	public String getFlogo() {
		return flogo;
	}

	public void setFlogo(String flogo) {
		this.flogo = flogo == null ? null : flogo.trim();
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	public Integer getFsort() {
		return fsort;
	}

	public void setFsort(Integer fsort) {
		this.fsort = fsort;
	}

	public Boolean getFstate() {
		return fstate;
	}

	public void setFstate(Boolean fstate) {
		this.fstate = fstate;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
}