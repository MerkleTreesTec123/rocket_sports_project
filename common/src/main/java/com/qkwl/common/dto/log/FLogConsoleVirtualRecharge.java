package com.qkwl.common.dto.log;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.qkwl.common.dto.Enum.OperationlogEnum;

/**
 * 人工充值虚拟币
 * @author TT
 */
public class FLogConsoleVirtualRecharge implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 主键ID
	private Integer fid;
	// 数量
	private BigDecimal famount;
	// 虚拟币ID
	private Integer fcoinid;
	// 类型
	private Integer ftype;	//ArtificialRechargeTypeEnum
	// 状态
	private Integer fstatus;
	private String fstatus_s;
	// 用户ID
	private Integer fuid;
	// 是否发送消息
	private Integer fissendmsg;
	// 审核人
	private Integer fcreatorid;
	// 描述
	private String finfo;
	// 创建时间
	private Date fcreatetime;
	// 版本号
	private Integer version;
	// 活动Id
	private Integer activityId;

	/************ 扩展字段 ***************/

	// 登录名
	private String floginname;
	// 昵称
	private String fnickname;
	// 真实姓名
	private String frealname;
	// 审核人
	private String fadminname;
	// 虚拟币名称
	private String fcoinname;

	public Integer getFid() {
		return fid;
	}

	public void setFid(Integer fid) {
		this.fid = fid;
	}


	public Integer getFcoinid() {
		return fcoinid;
	}

	public void setFcoinid(Integer fcoinid) {
		this.fcoinid = fcoinid;
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	public Integer getFuid() {
		return fuid;
	}

	public void setFuid(Integer fuid) {
		this.fuid = fuid;
	}

	public Integer getFissendmsg() {
		return fissendmsg;
	}

	public void setFissendmsg(Integer fissendmsg) {
		this.fissendmsg = fissendmsg;
	}

	public Integer getFcreatorid() {
		return fcreatorid;
	}

	public void setFcreatorid(Integer fcreatorid) {
		this.fcreatorid = fcreatorid;
	}

	public Date getFcreatetime() {
		return fcreatetime;
	}

	public void setFcreatetime(Date fcreatetime) {
		this.fcreatetime = fcreatetime;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	public String getFstatus_s() {
		return OperationlogEnum.getEnumString(this.fstatus);
	}

	public void setFstatus_s(String fstatus_s) {
		this.fstatus_s = fstatus_s;
	}

	public String getFloginname() {
		return floginname;
	}

	public void setFloginname(String floginname) {
		this.floginname = floginname;
	}

	public String getFnickname() {
		return fnickname;
	}

	public void setFnickname(String fnickname) {
		this.fnickname = fnickname;
	}

	public String getFrealname() {
		return frealname;
	}

	public void setFrealname(String frealname) {
		this.frealname = frealname;
	}

	public String getFadminname() {
		return fadminname;
	}

	public void setFadminname(String fadminname) {
		this.fadminname = fadminname;
	}

	public String getFcoinname() {
		return fcoinname;
	}

	public void setFcoinname(String fcoinname) {
		this.fcoinname = fcoinname;
	}

	public BigDecimal getFamount() {
		return famount;
	}

	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}

	public String getFinfo() {
		return finfo;
	}

	public void setFinfo(String finfo) {
		this.finfo = finfo;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}
}