package com.qkwl.common.dto.user;

import java.io.Serializable;

/**
 * 用户杠杆相关
 * @author ZKF
 */
public class UserLever implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 总资产
	 */
	private String totalasset;
	/**
	 * 净资产
	 */
	private String asset;
	/**
	 * 已杠杆
	 */
	private String totalborrow;
	/**
	 * 可杠杆
	 */
	private String canborrow;
	
	public String getTotalasset() {
		return totalasset;
	}
	public void setTotalasset(String totalasset) {
		this.totalasset = totalasset;
	}
	public String getAsset() {
		return asset;
	}
	public void setAsset(String asset) {
		this.asset = asset;
	}
	public String getTotalborrow() {
		return totalborrow;
	}
	public void setTotalborrow(String totalborrow) {
		this.totalborrow = totalborrow;
	}
	public String getCanborrow() {
		return canborrow;
	}
	public void setCanborrow(String canborrow) {
		this.canborrow = canborrow;
	}
	
	
}
