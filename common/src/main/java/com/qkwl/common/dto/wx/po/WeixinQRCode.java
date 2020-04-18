package com.qkwl.common.dto.wx.po;

/**
 * 临时二维码信息
 * @author ZKF
 */
public class WeixinQRCode {
	// 获取的二维码ticket
	private String ticket;
	// 二维码的有效时间，单位为秒，最大不超过604800
	private int expireSeconds;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public int getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(int expireSeconds) {
		this.expireSeconds = expireSeconds;
	}
}
