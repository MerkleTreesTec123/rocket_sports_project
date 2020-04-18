package com.qkwl.service.user.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值码
 * @author LY
 */
public class FRewardCodeDO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 用户ID
	private Integer fuid;
	// 登录名
	private String floginname;
	// 充值码充值类型 0 RMB >0虚拟币ID
	private Integer ftype;
	// 充值类型
	private String ftype_s;
	// 充值码
	private String fcode;
	// 金额或数量
	private BigDecimal famount;
	// 状态
	private Boolean fstate;
	// 状态
	private String fstate_s;
	// 创建时间
	private Date fcreatetime;
	// 更新时间
	private Date fupdatetime;
	// 版本号
	private Integer version;
	// 批次
	private Integer fbatch;
	// 限制用户
	private Boolean fislimituser;
	// 限制使用
	private Boolean fislimituse;
	// 使用数量
	private Integer fusenum;
	// 使用时间
	private Date fusedate;

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

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	public String getFcode() {
		return fcode;
	}

	public void setFcode(String fcode) {
		this.fcode = fcode == null ? null : fcode.trim();
	}

	public BigDecimal getFamount() {
		return famount;
	}

	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}

	public Boolean getFstate() {
		return fstate;
	}

	public void setFstate(Boolean fstate) {
		this.fstate = fstate;
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

	public Integer getFbatch() {
		return fbatch;
	}

	public void setFbatch(Integer fbatch) {
		this.fbatch = fbatch;
	}

	public Boolean getFislimituser() {
		return fislimituser;
	}

	public void setFislimituser(Boolean fislimituser) {
		this.fislimituser = fislimituser;
	}

	public Boolean getFislimituse() {
		return fislimituse;
	}

	public void setFislimituse(Boolean fislimituse) {
		this.fislimituse = fislimituse;
	}

	public Integer getFusenum() {
		return fusenum;
	}

	public void setFusenum(Integer fusenum) {
		this.fusenum = fusenum;
	}

	public Date getFusedate() {
		return fusedate;
	}

	public void setFusedate(Date fusedate) {
		this.fusedate = fusedate;
	}

	public String getFloginname() {
		return floginname;
	}

	public void setFloginname(String floginname) {
		this.floginname = floginname;
	}

	public String getFtype_s() {
		return ftype_s;
	}

	public void setFtype_s(String ftype_s) {
		this.ftype_s = ftype_s;
	}

	public String getFstate_s() {
		return fstate_s;
	}

	public void setFstate_s(String fstate_s) {
		this.fstate_s = fstate_s;
	}
}