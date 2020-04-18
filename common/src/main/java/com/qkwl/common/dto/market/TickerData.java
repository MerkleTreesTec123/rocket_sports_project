package com.qkwl.common.dto.market;

import java.io.Serializable;
import java.math.BigDecimal;
/**
 * 行情
 * @author TT
 */
public class TickerData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// 买1
	private BigDecimal buy;
	// 卖1
	private BigDecimal sell;
	// 高
	private BigDecimal high;
	// 低
	private BigDecimal low;
	// 上次成交价
	private BigDecimal last;
	// 开盘价
	private BigDecimal kai;
	// 成交量
	private BigDecimal vol;
	// 涨跌幅
	private BigDecimal chg;

	public TickerData() {

	}

	public TickerData(BigDecimal buy, BigDecimal sell, BigDecimal high, BigDecimal low, BigDecimal last, BigDecimal vol, BigDecimal chg) {
		this.buy = buy;
		this.sell = sell;
		this.high = high;
		this.low = low;
		this.last = last;
		this.vol = vol;
		this.chg = chg;
	}

	public BigDecimal getBuy() {
		return buy;
	}

	public void setBuy(BigDecimal buy) {
		this.buy = buy;
	}

	public BigDecimal getSell() {
		return sell;
	}

	public void setSell(BigDecimal sell) {
		this.sell = sell;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}

	public BigDecimal getVol() {
		return vol;
	}

	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}

	public BigDecimal getChg() {
		return chg;
	}

	public void setChg(BigDecimal chg) {
		this.chg = chg;
	}

	public BigDecimal getKai() {
		return kai;
	}

	public void setKai(BigDecimal kai) {
		this.kai = kai;
	}
}
