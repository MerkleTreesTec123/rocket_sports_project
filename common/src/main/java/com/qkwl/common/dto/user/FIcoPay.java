package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.math.BigDecimal;
public class FIcoPay implements Serializable{

	private static final long serialVersionUID = 1L;

	private BigDecimal price;
	private BigDecimal coin;
	private BigDecimal cny;
	private Integer gain;
	private boolean isHaveCny;
	private String icosign;
	private String nonceStr;
	
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getCoin() {
		return coin;
	}
	public void setCoin(BigDecimal coin) {
		this.coin = coin;
	}
	public BigDecimal getCny() {
		return cny;
	}
	public void setCny(BigDecimal cny) {
		this.cny = cny;
	}
	public Integer getGain() {
		return gain;
	}
	public void setGain(Integer gain) {
		this.gain = gain;
	}
	public boolean isHaveCny() {
		return isHaveCny;
	}
	public void setHaveCny(boolean isHaveCny) {
		this.isHaveCny = isHaveCny;
	}
	public String getIcosign() {
		return icosign;
	}
	public void setIcosign(String icosign) {
		this.icosign = icosign;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	
	
}
