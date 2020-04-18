package com.qkwl.common.dto.system;

import java.io.Serializable;

import com.qkwl.common.dto.common.HTMLSpirit;

/**
 * 系统参数
 * @author LY
 */
public class FSystemArgs implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 唯一key
	private String fkey;
	// key值
	private String fvalue;
	// key值
	private String fvalue_s;
	// 描述
	private String fdescription;
	// 版本
	private Integer version;
	// 类型
	private Integer ftype;
	// 地址
	private String furl;

	public FSystemArgs(){
		getFvalue_s();
	}
	
	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getFkey() {
		return fkey;
	}

	public void setFkey(String fkey) {
		this.fkey = fkey == null ? null : fkey.trim();
	}

	public String getFvalue() {
		return fvalue;
	}

	public void setFvalue(String fvalue) {
		this.fvalue = fvalue == null ? null : fvalue.trim();
	}
	
	public String getFvalue_s() {
		if(getFvalue()==null){
			return "";
		}
		fvalue_s=HTMLSpirit.delHTMLTag(getFvalue());
		return fvalue_s;
	}

	public void setFvalue_s(String fvalue_s) {
		this.fvalue_s = fvalue_s;
	}

	public String getFdescription() {
		return fdescription;
	}

	public void setFdescription(String fdescription) {
		this.fdescription = fdescription == null ? null : fdescription.trim();
	}

	public Integer getVersion() {
		return version;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getFurl() {
		return furl;
	}

	public void setFurl(String furl) {
		this.furl = furl == null ? null : furl.trim();
	}
}