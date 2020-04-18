package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息表
 * 
 * @author LY
 *
 */
public class FUserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	// 主键ID
	private Integer fid;
	// 用户ID
	private Integer fuid;
	// 姓名
	private String fname;
	// 联系电话
	private String fphone;
	// 邮编
	private String fzipcode;
	// 省份
	private String fprov;
	// 城市
	private String fcity;
	// 区县
	private String fdist;
	// 详细地址
	private String faddress;
	// 创建时间
	private Date fcreatetime;
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

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname == null ? null : fname.trim();
	}

	public String getFphone() {
		return fphone;
	}

	public void setFphone(String fphone) {
		this.fphone = fphone == null ? null : fphone.trim();
	}

	public String getFzipcode() {
		return fzipcode;
	}

	public void setFzipcode(String fzipcode) {
		this.fzipcode = fzipcode == null ? null : fzipcode.trim();
	}

	public String getFprov() {
		return fprov;
	}

	public void setFprov(String fprov) {
		this.fprov = fprov == null ? null : fprov.trim();
	}

	public String getFcity() {
		return fcity;
	}

	public void setFcity(String fcity) {
		this.fcity = fcity == null ? null : fcity.trim();
	}

	public String getFdist() {
		return fdist;
	}

	public void setFdist(String fdist) {
		this.fdist = fdist == null ? null : fdist.trim();
	}

	public String getFaddress() {
		return faddress;
	}

	public void setFaddress(String faddress) {
		this.faddress = faddress == null ? null : faddress.trim();
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}