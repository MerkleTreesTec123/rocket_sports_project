package com.qkwl.common.dto.news;

import java.io.Serializable;
import java.util.Date;

/**
 * 新闻类型
 * @author LY
 */
public class FArticleType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 名称
	private String fname;
	// 关键字
	private String fkeywords;
	// 描述
	private String fdescription;
	// 创建时间
	private Date fcreatetime;
	// 修改时间
	private Date fupdatetime;
	// 语言ID
	private Integer flanguageid;
	// 语言名称
	private String flanguagename;
	// 类型ID
	private Integer ftypeid;

	private String keyword;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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

	public String getFkeywords() {
		return fkeywords;
	}

	public void setFkeywords(String fkeywords) {
		this.fkeywords = fkeywords == null ? null : fkeywords.trim();
	}

	public String getFdescription() {
		return fdescription;
	}

	public void setFdescription(String fdescription) {
		this.fdescription = fdescription == null ? null : fdescription.trim();
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

	public Integer getFlanguageid() {
		return flanguageid;
	}

	public void setFlanguageid(Integer flanguageid) {
		this.flanguageid = flanguageid;
	}

	public Integer getFtypeid() {
		return ftypeid;
	}

	public void setFtypeid(Integer ftypeid) {
		this.ftypeid = ftypeid;
	}

	public String getFlanguagename() {
		return flanguagename;
	}

	public void setFlanguagename(String flanguagename) {
		this.flanguagename = flanguagename;
	}
}