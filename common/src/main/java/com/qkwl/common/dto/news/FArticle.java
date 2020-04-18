package com.qkwl.common.dto.news;

import java.io.Serializable;
import java.util.Date;

import com.qkwl.common.dto.common.HTMLSpirit;

/**
 * 新闻
 * @author LY
 */
public class FArticle implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 发布人
	private Integer fcreateadmin;
	// 发布人
	private String fcreateadmin_s;
	// 修改人
	private Integer fmodifyadmin;
	// 修改人
	private String fmodifyadmin_s;
	// 新闻类型
	private Integer farticletype;
	// 新闻类型
	private String farticletype_s;
	// 标题
	private String ftitle;
	// 关键字
	private String fkeyword;
	// 描述
	private String fdescription;
	// 创建日期
	private Date fcreatedate;
	// 更新日期
	private Date fupdatetime;
	// 版本
	private Integer version;
	// 浏览数
	private Integer flookcount;
	// 是否顶部滚动
	private Boolean fistop;
	// 新闻首页图片
	private String findeximg;
	// 内容
	private String fcontent;
	// 劵商ID
	private Integer fagentid;
	// 显示类型 0不限制，1手机端，2电脑端
	private Integer ftype;

	/* 辅助字段 */
	private String ftitle_short;

	private String fcontent_short;

	private String fabstract;

	private String fcontent_indexShort;

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

	public Integer getFcreateadmin() {
		return fcreateadmin;
	}

	public void setFcreateadmin(Integer fcreateadmin) {
		this.fcreateadmin = fcreateadmin;
	}

	public Integer getFmodifyadmin() {
		return fmodifyadmin;
	}

	public void setFmodifyadmin(Integer fmodifyadmin) {
		this.fmodifyadmin = fmodifyadmin;
	}

	public Integer getFarticletype() {
		return farticletype;
	}

	public void setFarticletype(Integer farticletype) {
		this.farticletype = farticletype;
	}

	public String getFtitle() {
		return ftitle;
	}

	public void setFtitle(String ftitle) {
		this.ftitle = ftitle == null ? null : ftitle.trim();
	}

	public String getFkeyword() {
		return fkeyword;
	}

	public void setFkeyword(String fkeyword) {
		this.fkeyword = fkeyword == null ? null : fkeyword.trim();
	}

	public String getFdescription() {
		return fdescription;
	}

	public void setFdescription(String fdescription) {
		this.fdescription = fdescription == null ? null : fdescription.trim();
	}

	public Date getFcreatedate() {
		return fcreatedate;
	}

	public void setFcreatedate(Date fcreatedate) {
		this.fcreatedate = fcreatedate;
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

	public Integer getFlookcount() {
		return flookcount;
	}

	public void setFlookcount(Integer flookcount) {
		this.flookcount = flookcount;
	}

	public Boolean getFistop() {
		return fistop;
	}

	public void setFistop(Boolean fistop) {
		this.fistop = fistop;
	}

	public String getFindeximg() {
		return findeximg;
	}

	public void setFindeximg(String findeximg) {
		this.findeximg = findeximg == null ? null : findeximg.trim();
	}

	public String getFcontent() {
		return fcontent;
	}

	public void setFcontent(String fcontent) {
		this.fcontent = fcontent == null ? null : fcontent.trim();
	}

	/* 辅助字段 */
	public String getFtitle_short() {
		String t = this.getFtitle();
		if (t == null) {
			return t;
		}
		if (t.length() > 15) {
			t = t.substring(0, 15);
			t += "...";
		}

		return t;
	}

	public void setFtitle_short(String ftitle_short) {
		this.ftitle_short = ftitle_short;
	}

	public String getFabstract() {
		String content = HTMLSpirit.delHTMLTag(this.getFcontent());
		String ret = null;
		if (content != null) {
			if (content.length() > 75) {
				ret = content.substring(0, 75);
			} else {
				ret = content;
			}
		}
		return ret;
	}

	public void setFabstract(String fabstract) {
		this.fabstract = fabstract;
	}

	public String getFcontent_short() {
		String content = this.getFcontent();
		String retf = "";
		if (content != null) {
			content = HTMLSpirit.delHTMLTag(content);
			if (content.length() < 300) {
				retf = content + "...";
			} else {
				retf = content.substring(0, 160) + "...";
			}
		}

		return retf;
	}

	public void setFcontent_short(String fcontent_short) {
		this.fcontent_short = fcontent_short;
	}

	public String getFcontent_indexShort() {
		String content = this.getFcontent();
		String retf = "";
		if (content != null) {
			content = HTMLSpirit.delHTMLTag(content);
			if (content.length() < 35) {
				retf = content + "...";
			} else {
				retf = content.substring(0, 35) + "...";
			}
		}

		return retf;
	}

	public void setFcontent_indexShort(String fcontent_indexShort) {
		this.fcontent_indexShort = fcontent_indexShort;
	}

	public String getFarticletype_s() {
		return farticletype_s;
	}

	public void setFarticletype_s(String farticletype_s) {
		this.farticletype_s = farticletype_s;
	}

	public String getFcreateadmin_s() {
		return fcreateadmin_s;
	}

	public void setFcreateadmin_s(String fcreateadmin_s) {
		this.fcreateadmin_s = fcreateadmin_s;
	}

	public String getFmodifyadmin_s() {
		return fmodifyadmin_s;
	}

	public void setFmodifyadmin_s(String fmodifyadmin_s) {
		this.fmodifyadmin_s = fmodifyadmin_s;
	}

	public Integer getFagentid() {
		return fagentid;
	}

	public void setFagentid(Integer fagentid) {
		this.fagentid = fagentid;
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

}