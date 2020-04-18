package com.qkwl.common.dto.admin;

import java.io.Serializable;
import java.util.Date;

import com.qkwl.common.dto.Enum.AdminStatusEnum;

/**
 * 管理员列表
 * @author TT
 */
public class FAdmin implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 登录名
	private String fname;
	// 登录密码
	private String fpassword;
	// 注册时间
	private Date fcreatetime;
	// 状态
	private Integer fstatus;
	// 角色ID
	private Integer froleid;
	// 谷歌密匙
	private String fgoogleauthenticator;
	// 谷歌URL
	private String fgoogleurl;
	// 是否谷歌绑定
	private Boolean fgooglebind;
	// 是否谷歌验证
	private Boolean fopengooglevalidate;
	// 谷歌绑定时间
	private Boolean fgooglevalidate;
	// 乐观锁版本
	private Integer version;
	
	private String rolename;
	
	private String fstatus_s;
	
	/*********券商********/
	private int fagentid;
	
	private String fagentname;

	public int getFagentid() {
		return fagentid;
	}

	public void setFagentid(int fagentid) {
		this.fagentid = fagentid;
	}

	public String getFagentname() {
		return fagentname;
	}

	public void setFagentname(String fagentname) {
		this.fagentname = fagentname;
	}

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

	public String getFpassword() {
		return fpassword;
	}

	public void setFpassword(String fpassword) {
		this.fpassword = fpassword == null ? null : fpassword.trim();
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
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

	public Integer getFroleid() {
		return froleid;
	}

	public void setFroleid(Integer froleid) {
		this.froleid = froleid;
	}

	public String getFgoogleauthenticator() {
		return fgoogleauthenticator;
	}

	public void setFgoogleauthenticator(String fgoogleauthenticator) {
		this.fgoogleauthenticator = fgoogleauthenticator == null ? null : fgoogleauthenticator.trim();
	}

	public String getFgoogleurl() {
		return fgoogleurl;
	}

	public void setFgoogleurl(String fgoogleurl) {
		this.fgoogleurl = fgoogleurl == null ? null : fgoogleurl.trim();
	}

	public Boolean getFgooglebind() {
		return fgooglebind;
	}

	public void setFgooglebind(Boolean fgooglebind) {
		this.fgooglebind = fgooglebind;
	}

	public Boolean getFopengooglevalidate() {
		return fopengooglevalidate;
	}

	public void setFopengooglevalidate(Boolean fopengooglevalidate) {
		this.fopengooglevalidate = fopengooglevalidate;
	}

	public Boolean getFgooglevalidate() {
		return fgooglevalidate;
	}

	public void setFgooglevalidate(Boolean fgooglevalidate) {
		this.fgooglevalidate = fgooglevalidate;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getFstatus_s() {
		return AdminStatusEnum.getEnumString(fstatus);
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}
	
}