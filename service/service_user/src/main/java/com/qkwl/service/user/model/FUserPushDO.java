package com.qkwl.service.user.model;

import com.qkwl.common.dto.Enum.UserPushStateEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * PUSH资产
 * 
 * @author LY
 *
 */
public class FUserPushDO implements Serializable {

	private static final long serialVersionUID = -8155378435817054589L;

	// 主键ID
	private Integer fid;
	// 用户ID
	private Integer fuid;
	// PUSH用户ID
	private Integer fpushuid;
	// PUSH资产ID
	private Integer fcoinid;
	private String fcoin_s;
	// 价格
	private BigDecimal fprice;
	// 数量
	private BigDecimal fcount;
	// 金额
	private BigDecimal famount;
	// 状态
	private Integer fstate;
	private String fstate_s;
	// 创建时间
	private Date fcreatetime;
	// 更新时间
	private Date fupdatetime;
	// 备注
	private String fremark;
	// 版本号
	private Integer version;

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

	public Integer getFpushuid() {
		return fpushuid;
	}

	public void setFpushuid(Integer fpushuid) {
		this.fpushuid = fpushuid;
	}

	public Integer getFcoinid() {
		return fcoinid;
	}

	public void setFcoinid(Integer fcoinid) {
		this.fcoinid = fcoinid;
	}

	public String getFcoin_s() {
		return fcoin_s;
	}

	public void setFcoin_s(String fcoin_s) {
		this.fcoin_s = fcoin_s;
	}

	public BigDecimal getFprice() {
		return fprice;
	}

	public void setFprice(BigDecimal fprice) {
		this.fprice = fprice;
	}

	public BigDecimal getFcount() {
		return fcount;
	}

	public void setFcount(BigDecimal fcount) {
		this.fcount = fcount;
	}

	public BigDecimal getFamount() {
		return famount;
	}

	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}

	public Integer getFstate() {
		return fstate;
	}

	public void setFstate(Integer fstate) {
		this.fstate = fstate;
	}

	public String getFstate_s() {
		fstate_s = UserPushStateEnum.getUserPushStateByCode(this.fstate);
		return fstate_s;
	}

	public void setFstate_s(String fstate_s) {
		this.fstate_s = fstate_s;
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

	public String getFremark() {
		return fremark;
	}

	public void setFremark(String fremark) {
		this.fremark = fremark == null ? null : fremark.trim();
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}