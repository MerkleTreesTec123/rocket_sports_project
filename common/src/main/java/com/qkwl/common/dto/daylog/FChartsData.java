package com.qkwl.common.dto.daylog;

import java.io.Serializable;

/**
 * 注册人数图表数据模型
 * @author ZKF
 */
public class FChartsData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer regnum;
	private FCoinCharts btc;
	private FCoinCharts ltc;
	private FCoinCharts etc;
	
	public Integer getRegnum() {
		return regnum;
	}
	public void setRegnum(Integer regnum) {
		this.regnum = regnum;
	}
	public FCoinCharts getBtc() {
		return btc;
	}
	public void setBtc(FCoinCharts btc) {
		this.btc = btc;
	}
	public FCoinCharts getLtc() {
		return ltc;
	}
	public void setLtc(FCoinCharts ltc) {
		this.ltc = ltc;
	}
	public FCoinCharts getEtc() {
		return etc;
	}
	public void setEtc(FCoinCharts etc) {
		this.etc = etc;
	}
	
	
	

}
