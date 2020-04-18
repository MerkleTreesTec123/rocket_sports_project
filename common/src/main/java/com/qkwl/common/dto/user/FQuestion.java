package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

import com.qkwl.common.dto.Enum.QuestionStatusEnum;
import com.qkwl.common.dto.Enum.QuestionTypeEnum;

/**
 * 问题列表
 * @author LY
 */
public class FQuestion implements Serializable {
	private static final long serialVersionUID = 1L;
	// 主键ID
	private Integer fid;
	// 类型
	private Integer ftype;
	private String ftype_s;
	// 手机号
	private String ftelephone;
	// 用户ID
	private Integer fuid;
	// 状态
	private Integer fstatus;
	private String fstatus_s;
	// 创建时间
	private Date fcreatetime;
	// 更新时间
	private Date fupdatetime;
	// 回复管理员ID
	private Integer faid;
	//
	private String fname;
	// 版本号
	private Integer version;

	private Integer fcid;
	// 是否回复
	private Integer fisanswer;
	// 描述
	private String fdesc;
	// 回复内容
	private String fanswer;
	
	private String fadmin;

	public FQuestion() {
		getFtype_s();
		getFstatus_s();
	}

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	public String getFtelephone() {
		return ftelephone;
	}

	public void setFtelephone(String ftelephone) {
		this.ftelephone = ftelephone == null ? null : ftelephone.trim();
	}

	public Integer getFuid() {
		return fuid;
	}

	public void setFuid(Integer fuid) {
		this.fuid = fuid;
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
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

	public Integer getFaid() {
		return faid;
	}

	public void setFaid(Integer faid) {
		this.faid = faid;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname == null ? null : fname.trim();
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getFcid() {
		return fcid;
	}

	public void setFcid(Integer fcid) {
		this.fcid = fcid;
	}

	public Integer getFisanswer() {
		return fisanswer;
	}

	public void setFisanswer(Integer fisanswer) {
		this.fisanswer = fisanswer;
	}

	public String getFdesc() {
		return fdesc;
	}

	public void setFdesc(String fdesc) {
		this.fdesc = fdesc == null ? null : fdesc.trim();
	}

	public String getFanswer() {
		return fanswer;
	}

	public void setFanswer(String fanswer) {
		this.fanswer = fanswer == null ? null : fanswer.trim();
	}

	public String getFtype_s() {
		ftype_s = QuestionTypeEnum.getValueByCode(this.getFtype());
		return ftype_s;
	}

	public void setFtype_s(String ftype_s) {
		this.ftype_s = ftype_s;
	}

	public String getFstatus_s() {
		fstatus_s = QuestionStatusEnum.getValueByCode(this.getFstatus());
		return fstatus_s;
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}

	public String getFadmin() {
		return fadmin;
	}

	public void setFadmin(String fadmin) {
		this.fadmin = fadmin;
	}

}