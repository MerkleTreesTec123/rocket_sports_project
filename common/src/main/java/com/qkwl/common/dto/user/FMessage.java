package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.sql.Timestamp;

import com.qkwl.common.dto.Enum.MessageStatusEnum;

/**
 * 用户消息表
 * @author LY
 */
public class FMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	// 主键ID
	private Integer fid;
	// 状态
	private Integer fstatus;// MessageStatusEnum
	private String fstatus_s;
	// 标题
	private String ftitle;
	// 接受用户ID
	private Integer freceiverid;
	// 创建用户ID
	private Integer fcreatorid;
	// 创建时间
	private Timestamp fcreatetime;
	// 内容
	private String fcontent;

	public FMessage() {
		this.getFstatus_s();
	}

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	public String getFtitle() {
		return ftitle;
	}

	public void setFtitle(String ftitle) {
		this.ftitle = ftitle == null ? null : ftitle.trim();
	}

	public Integer getFreceiverid() {
		return freceiverid;
	}

	public void setFreceiverid(Integer freceiverid) {
		this.freceiverid = freceiverid;
	}

	public Integer getFcreatorid() {
		return fcreatorid;
	}

	public void setFcreatorid(Integer fcreatorid) {
		this.fcreatorid = fcreatorid;
	}

	public Timestamp getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Timestamp fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public String getFcontent() {
		return fcontent;
	}

	public void setFcontent(String fcontent) {
		this.fcontent = fcontent == null ? null : fcontent.trim();
	}

	public String getFstatus_s() {
		fstatus_s = MessageStatusEnum.getValueByCode(this.getFstatus());
		return fstatus_s;
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}
}