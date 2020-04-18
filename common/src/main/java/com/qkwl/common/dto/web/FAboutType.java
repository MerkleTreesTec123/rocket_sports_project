package com.qkwl.common.dto.web;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 关于我们分类
 * @author LY
 */
public class FAboutType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 标题
	private String ftitle;
	// 语言ID
	private Integer flanguageid;
	// 描述
	private String fdescribe;
	// 状态
	private Boolean fstate;
	// 排序
	private Integer fsort;
	//扩展字段-子集
	private Map<Integer, String> child = new LinkedHashMap<Integer, String>();

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

	public Integer getFlanguageid() {
		return flanguageid;
	}

	public void setFlanguageid(Integer flanguageid) {
		this.flanguageid = flanguageid;
	}

	public String getFdescribe() {
		return fdescribe;
	}

	public void setFdescribe(String fdescribe) {
		this.fdescribe = fdescribe == null ? null : fdescribe.trim();
	}

	public Boolean getFstate() {
		return fstate;
	}

	public void setFstate(Boolean fstate) {
		this.fstate = fstate;
	}

	public Integer getFsort() {
		return fsort;
	}

	public void setFsort(Integer fsort) {
		this.fsort = fsort;
	}

	public Map<Integer, String> getChild() {
		return child;
	}

	public void setChild(Map<Integer, String> child) {
		this.child = child;
	}
}