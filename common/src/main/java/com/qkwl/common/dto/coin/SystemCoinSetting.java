package com.qkwl.common.dto.coin;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币种相关设置
 * @author LY
 *
 */
public class SystemCoinSetting {
	// 主键ID
	private Integer id;
	// 币种ID
	private Integer coinId;
	// VIP等级
	private Integer levelVip;
	// 最大提现
	private BigDecimal withdrawMax;
	// 最小提现
	private BigDecimal withdrawMin;
	// 提现费率
	private BigDecimal withdrawFee;
	// 提现次数
	private Integer withdrawTimes;
	// 每天提现额度
	private BigDecimal withdrawDayLimit;
	// 创建时间
	private Date gmtCreate;
	// 修改时间
	private Date gmtModified;
	// 版本号
	private Integer version;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCoinId() {
		return coinId;
	}

	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}

	public Integer getLevelVip() {
		return levelVip;
	}

	public void setLevelVip(Integer levelVip) {
		this.levelVip = levelVip;
	}

	public BigDecimal getWithdrawMax() {
		return withdrawMax;
	}

	public void setWithdrawMax(BigDecimal withdrawMax) {
		this.withdrawMax = withdrawMax;
	}

	public BigDecimal getWithdrawMin() {
		return withdrawMin;
	}

	public void setWithdrawMin(BigDecimal withdrawMin) {
		this.withdrawMin = withdrawMin;
	}

	public BigDecimal getWithdrawFee() {
		return withdrawFee;
	}

	public void setWithdrawFee(BigDecimal withdrawFee) {
		this.withdrawFee = withdrawFee;
	}

	public Integer getWithdrawTimes() {
		return withdrawTimes;
	}

	public void setWithdrawTimes(Integer withdrawTimes) {
		this.withdrawTimes = withdrawTimes;
	}

	public BigDecimal getWithdrawDayLimit() {
		return withdrawDayLimit;
	}

	public void setWithdrawDayLimit(BigDecimal withdrawDayLimit) {
		this.withdrawDayLimit = withdrawDayLimit;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}