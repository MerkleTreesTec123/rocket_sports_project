package com.qkwl.common.dto.web;

import java.io.Serializable;

import com.qkwl.common.dto.common.HTMLSpirit;

/**
 * 关于我们
 * @author LY
 */
public class FAbout implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 标题
	private String ftitle;
	// 简写
	private String fshortname;
	// 类型
	private Integer fabouttype;
	private String fabouttype_s;
	// 排序
	private Integer fsort;
	// 内容
	private String fcontent;
	// 内容
	private String fcontent_s;
	// 显示ID
	private Integer fshowid;
	
	public FAbout(){
		getFcontent_s();
	}
	
	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}

	public String getFtitle() {
		return ftitle;
	}

	public void setFtitle(String ftitle) {
		this.ftitle = ftitle == null ? null : ftitle.trim();
	}

	public String getFshortname() {
		return fshortname;
	}

	public void setFshortname(String fshortname) {
		this.fshortname = fshortname == null ? null : fshortname.trim();
	}

	public Integer getFabouttype() {
		return fabouttype;
	}

	public void setFabouttype(Integer fabouttype) {
		this.fabouttype = fabouttype;
	}

	public Integer getFsort() {
		return fsort;
	}

	public void setFsort(Integer fsort) {
		this.fsort = fsort;
	}

	public String getFcontent() {
		return fcontent;
	}

	public void setFcontent(String fcontent) {
		this.fcontent = fcontent == null ? null : fcontent.trim();
	}
	
	public String getFcontent_s() {
		if(getFcontent()==null){
			return "";
		}
		fcontent_s=HTMLSpirit.delHTMLTag(getFcontent());
		return fcontent_s;
	}

	public void setFcontent_s(String fcontent_s) {
		this.fcontent_s = fcontent_s;
	}

	public String getFabouttype_s() {
		return fabouttype_s;
	}

	public void setFabouttype_s(String fabouttype_s) {
		this.fabouttype_s = fabouttype_s;
	}

	public Integer getFshowid() {
		return fshowid;
	}

	public void setFshowid(Integer fshowid) {
		this.fshowid = fshowid;
	}
}