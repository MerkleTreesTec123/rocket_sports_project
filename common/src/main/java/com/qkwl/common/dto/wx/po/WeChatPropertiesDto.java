package com.qkwl.common.dto.wx.po;

/**
 * 微信参数缓存DTO
 * @author ZKF
 */
public class WeChatPropertiesDto implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	/**微信token*/
	private String accesstoken;
	/**微信jssdk票据*/
	private String jsapiTicket;
    /**微信卡券接口的临时票据*/
	private String apiTicket;
    /**刷新时间，大于2小时即刷新*/
	private String refreshtime;

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	public String getJsapiTicket() {
		return jsapiTicket;
	}

	public void setJsapiTicket(String jsapiTicket) {
		this.jsapiTicket = jsapiTicket;
	}

	public String getApiTicket() {
		return apiTicket;
	}

	public void setApiTicket(String apiTicket) {
		this.apiTicket = apiTicket;
	}

	public String getRefreshtime() {
		return refreshtime;
	}

	public void setRefreshtime(String refreshtime) {
		this.refreshtime = refreshtime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
