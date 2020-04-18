package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

/**
 * 身份证号记录
 * @author LY
 */
public class FIdentityInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 姓名
	private String fusername;
	// 身份证号
	private String fidentityno;
	// 创建时间
	private Date fcreatetime;
	// 是否验证通过
	private Integer fisok;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getFusername() {
		return fusername;
	}

	public void setFusername(String fusername) {
		this.fusername = fusername == null ? null : fusername.trim();
	}

	public String getFidentityno() {
		return fidentityno;
	}

	public void setFidentityno(String fidentityno) {
		this.fidentityno = fidentityno == null ? null : fidentityno.trim();
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public Integer getFisok() {
		return fisok;
	}

	public void setFisok(Integer fisok) {
		this.fisok = fisok;
	}
}