package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
/**
 * 价格闹钟
 * 
 * @author ZKF
 */
public class FUserPriceclock implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer fid;

	private Integer fuid;

	private Integer ftradeid;

	private BigDecimal fmaxprice;

	private BigDecimal fminprice;

	private Boolean fisopen;

	private Date fupdatetime;

	private Integer version;

	/***** 扩展字段 *****/
	// 当前价格
	private BigDecimal flastprice;

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

	public Integer getFtradeid() {
		return ftradeid;
	}

	public void setFtradeid(Integer ftradeid) {
		this.ftradeid = ftradeid;
	}

	public BigDecimal getFmaxprice() {
		return fmaxprice;
	}

	public void setFmaxprice(BigDecimal fmaxprice) {
		this.fmaxprice = fmaxprice;
	}

	public BigDecimal getFminprice() {
		return fminprice;
	}

	public void setFminprice(BigDecimal fminprice) {
		this.fminprice = fminprice;
	}

	public Boolean getFisopen() {
		return fisopen;
	}

	public void setFisopen(Boolean fisopen) {
		this.fisopen = fisopen;
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

	public BigDecimal getFlastprice() {
		return flastprice;
	}

	public void setFlastprice(BigDecimal flastprice) {
		this.flastprice = flastprice;
	}

}