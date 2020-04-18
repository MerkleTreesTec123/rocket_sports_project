package com.qkwl.common.dto.web;

import java.io.Serializable;
import java.util.Date;

import com.qkwl.common.dto.Enum.LinkTypeEnum;

/**
 * 友情链接
 * @author LY
 */
public class FFriendLink implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 名称
	private String fname;
	// 描述
	private String fdescription;
	// 排序
	private Integer forder;
	// 创建时间
	private Date fcreatetime;
	// 链接地址
	private String furl;
	// 类型
	private Integer ftype;
	// 类型
	private String ftype_s;
	// 版本
	private Integer version;

	public FFriendLink(){
		getFtype_s();
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

	public String getFdescription() {
		return fdescription;
	}

	public void setFdescription(String fdescription) {
		this.fdescription = fdescription == null ? null : fdescription.trim();
	}

	public Integer getForder() {
		return forder;
	}

	public void setForder(Integer forder) {
		this.forder = forder;
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public String getFurl() {
		return furl;
	}

	public void setFurl(String furl) {
		this.furl = furl == null ? null : furl.trim();
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}
	
	public String getFtype_s() {
		if(this.getFtype()==null){
			return "";
		}
		ftype_s=LinkTypeEnum.getEnumString(this.getFtype());
		return ftype_s;
	}

	public void setFtype_s(String ftype_s) {
		this.ftype_s = ftype_s;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}