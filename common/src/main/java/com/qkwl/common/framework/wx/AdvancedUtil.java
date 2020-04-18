package com.qkwl.common.framework.wx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qkwl.common.dto.wx.po.SNSUserInfo;
import com.qkwl.common.dto.wx.po.WeixinOauth2Token;
import com.qkwl.common.dto.wx.po.WeixinQRCode;
import com.qkwl.common.dto.wx.po.WeixinUserInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 * 高级接口工具类
 * @author ZKF
 */
public class AdvancedUtil {
	private static final Logger logger = LoggerFactory.getLogger(AdvancedUtil.class);
	

	/**
	 * 组装文本客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param content 文本消息内容
	 * @return
	 */
	public String makeTextCustomMessage(String openId, String content) {
		// 对消息内容中的双引号进行转义
		content = content.replace("\"", "\\\"");
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
		return String.format(jsonMsg, openId, content);
	}

	/**
	 * 组装图片客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param mediaId 媒体文件id
	 * @return
	 */
	public String makeImageCustomMessage(String openId, String mediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"image\",\"image\":{\"media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId);
	}

	/**
	 * 组装语音客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param mediaId 媒体文件id
	 * @return
	 */
	public String makeVoiceCustomMessage(String openId, String mediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"voice\",\"voice\":{\"media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId);
	}

	/**
	 * 组装视频客服消息
	 * 
	 * @param openId 消息发送对象
	 * @param mediaId 媒体文件id
	 * @param thumbMediaId 视频消息缩略图的媒体id
	 * @return
	 */
	public String makeVideoCustomMessage(String openId, String mediaId, String thumbMediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"video\",\"video\":{\"media_id\":\"%s\",\"thumb_media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId, thumbMediaId);
	}

	/**
	 * 发送客服消息
	 * 
	 * @param accessToken 接口访问凭证
	 * @param jsonMsg json格式的客服消息（包括touser、msgtype和消息内容）
	 * @return true | false
	 */
	public boolean sendCustomMessage(String accessToken, String jsonMsg) {
		logger.info("消息内容：{}", jsonMsg);
		System.out.println("消息内容："+jsonMsg);
		boolean result = false;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 发送客服消息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", jsonMsg);
        System.out.println("jsonObject"+jsonObject);
		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				logger.info("客服消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				logger.error("客服消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}

		return result;
	}
	
	
	/**
	 * 发送模板消息
	 * 
	 * @param accessToken 接口访问凭证
	 * @param jsonMsg json格式的模板消息
	 * @return true | false
	 */
	public boolean sendTemplateMessage(String accessToken, String jsonMsg) {
		logger.info("消息内容：{}", jsonMsg);
		System.out.println("消息内容："+jsonMsg);
		boolean result = false;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 发送客服消息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", jsonMsg);
        System.out.println("jsonObject"+jsonObject);
		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				logger.error("模板消息发送成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				logger.error("模板消息发送失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}

		return result;
	}
	
	/**
	 * 获取jsapi_ticket
	 * 
     * jsapi_ticket是公众号用于调用微信JS接口的临时票据。
     * 正常情况下，jsapi_ticket的有效期为7200秒，通过access_token来获取。
     * 由于获取jsapi_ticket的api调用次数非常有限，频繁刷新jsapi_ticket会导致api调用受限，影响自身业务，开发者必须在自己的服务全局缓存jsapi_ticket 。
	 */
	public static String getTickect(String access_token) {
		String ticket = "";
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", access_token);
		// 获取网页授权凭证
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			try {
				ticket = jsonObject.getString("ticket");
			} catch (Exception e) {
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				logger.error("获取jsapi_ticket失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return ticket;
	}
	
	/**
	 * 获取api_ticket
	 * 
     * api_ticket是公众号用于调用微信卡券接口的临时票据。
     * 正常情况下，api_ticket的有效期为7200秒，通过access_token来获取。
     * 由于获取api_ticket的api调用次数非常有限，频繁刷新api_ticket会导致api调用受限，影响自身业务，开发者必须在自己的服务全局缓存api_ticket 。
	 */
	public static String getCardTickect(String access_token) {
		String ticket = "";
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=wx_card";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", access_token);
		// 获取网页授权凭证
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			try {
				ticket = jsonObject.getString("ticket");
			} catch (Exception e) {
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				logger.error("获取jsapi_ticket失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return ticket;
	}

	/**
	 * 获取网页授权凭证
	 * 
	 * @param appId 公众账号的唯一标识
	 * @param appSecret 公众账号的密钥
	 * @param code
	 * @return WeixinAouth2Token
	 */
	public static WeixinOauth2Token getOauth2AccessToken(String appId, String appSecret, String code) {
		WeixinOauth2Token wat = null;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
		requestUrl = requestUrl.replace("APPID", appId);
		requestUrl = requestUrl.replace("SECRET", appSecret);
		requestUrl = requestUrl.replace("CODE", code);
		// 获取网页授权凭证
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			try {
				wat = new WeixinOauth2Token();
				wat.setAccessToken(jsonObject.getString("access_token"));
				wat.setExpiresIn(jsonObject.getInt("expires_in"));
				wat.setRefreshToken(jsonObject.getString("refresh_token"));
				wat.setOpenId(jsonObject.getString("openid"));
				wat.setScope(jsonObject.getString("scope"));
			} catch (Exception e) {
				wat = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				logger.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return wat;
	}

	/**
	 * 刷新网页授权凭证
	 * 
	 * @param appId 公众账号的唯一标识
	 * @param refreshToken
	 * @return WeixinAouth2Token
	 */
	public static WeixinOauth2Token refreshOauth2AccessToken(String appId, String refreshToken) {
		WeixinOauth2Token wat = null;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
		requestUrl = requestUrl.replace("APPID", appId);
		requestUrl = requestUrl.replace("REFRESH_TOKEN", refreshToken);
		// 刷新网页授权凭证
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			try {
				wat = new WeixinOauth2Token();
				wat.setAccessToken(jsonObject.getString("access_token"));
				wat.setExpiresIn(jsonObject.getInt("expires_in"));
				wat.setRefreshToken(jsonObject.getString("refresh_token"));
				wat.setOpenId(jsonObject.getString("openid"));
				wat.setScope(jsonObject.getString("scope"));
			} catch (Exception e) {
				wat = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				logger.error("刷新网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return wat;
	}

	/**
	 * 通过网页授权获取用户信息
	 * 
	 * @param accessToken 网页授权接口调用凭证
	 * @param openId 用户标识
	 * @return SNSUserInfo
	 */
	@SuppressWarnings( { "deprecation", "unchecked" })
	public static SNSUserInfo getSNSUserInfo(String accessToken, String openId) {
		SNSUserInfo snsUserInfo = null;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		// 通过网页授权获取用户信息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject) {
			try {
				snsUserInfo = new SNSUserInfo();
				// 用户的标识
				snsUserInfo.setOpenId(jsonObject.getString("openid"));
				// 昵称
				snsUserInfo.setNickname(jsonObject.getString("nickname"));
				// 性别（1是男性，2是女性，0是未知）
				snsUserInfo.setSex(jsonObject.getInt("sex"));
				// 用户所在国家
				snsUserInfo.setCountry(jsonObject.getString("country"));
				// 用户所在省份
				snsUserInfo.setProvince(jsonObject.getString("province"));
				// 用户所在城市
				snsUserInfo.setCity(jsonObject.getString("city"));
				// 用户头像
				snsUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
				// 用户特权信息
				snsUserInfo.setPrivilegeList(JSONArray.toList(jsonObject.getJSONArray("privilege"), List.class));
				
				snsUserInfo.setUnionid(jsonObject.getString("unionid"));
			} catch (Exception e) {
				snsUserInfo = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				logger.error("获取SNS用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return snsUserInfo;
	}

	/**
	 * 创建临时带参二维码
	 * 
	 * @param accessToken 接口访问凭证
	 * @param expireSeconds 二维码有效时间，单位为秒，最大不超过604800(7天)
	 * @param sceneId 场景ID
	 * @return WeixinQRCode
	 */
	public WeixinQRCode createTemporaryQRCode(String accessToken, int expireSeconds, int sceneId) {
		WeixinQRCode weixinQRCode = null;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonMsg = "{\"expire_seconds\": %d, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %d}}}";
		// 创建临时带参二维码
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonMsg, expireSeconds, sceneId));

		if (null != jsonObject) {
			try {
				weixinQRCode = new WeixinQRCode();
				weixinQRCode.setTicket(jsonObject.getString("ticket"));
				weixinQRCode.setExpireSeconds(jsonObject.getInt("expire_seconds"));
				logger.info("创建临时带参二维码成功 ticket:{} expire_seconds:{}", weixinQRCode.getTicket(), weixinQRCode.getExpireSeconds());
			} catch (Exception e) {
				weixinQRCode = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				logger.error("创建临时带参二维码失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return weixinQRCode;
	}

	/**
	 * 创建永久带参二维码
	 * 
	 * @param accessToken 接口访问凭证
	 * @param sceneId 场景ID
	 * @return ticket
	 */
	public static String createPermanentQRCode(String accessToken, String scene_str) {
		String ticket = null;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonMsg = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": %s}}}";
		// 创建永久带参二维码
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonMsg, scene_str));

		if (null != jsonObject) {
			try {
				ticket = jsonObject.getString("ticket");
				logger.info("创建永久带参二维码成功 ticket:{}", ticket);
			} catch (Exception e) {
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				logger.error("创建永久带参二维码失败 errcode:{} errmsg:{}", errorCode, errorMsg);
				if(errorCode==40001){
					
				}
			}
		}
		return ticket;
	}

	/**
	 * 根据ticket换取二维码
	 * 
	 * @param ticket 二维码ticket
	 * @param savePath 保存路径
	 */
	public static String getQRCode(String ticket, String savePath) {
		String filePath = null;
		// 拼接请求地址
		String requestUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
		requestUrl = requestUrl.replace("TICKET", CommonUtil.urlEncodeUTF8(ticket));
		try {
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");

			if (!savePath.endsWith("/")) {
				savePath += "/";
			}
			// 将ticket作为文件名
			filePath = savePath + ticket + ".jpg";

			// 将微信服务器返回的输入流写入文件
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1)
				fos.write(buf, 0, size);
			fos.close();
			bis.close();

			conn.disconnect();
			logger.info("根据ticket换取二维码成功，filePath=" + filePath);
		} catch (Exception e) {
			filePath = null;
			logger.error("根据ticket换取二维码失败：{}", e);
		}
		return filePath;
	}
	

	/**
	 * 获取用户信息
	 * 
	 * @param accessToken 接口访问凭证
	 * @param openId 用户标识
	 * @return WeixinUserInfo
	 */
	public static WeixinUserInfo getUserInfo(String accessToken, String openId) {
		WeixinUserInfo weixinUserInfo = null;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		// 获取用户信息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

		if (null != jsonObject) {
			try {
				weixinUserInfo = new WeixinUserInfo();
				// 用户的标识
				weixinUserInfo.setOpenId(jsonObject.getString("openid"));
				weixinUserInfo.setSubscribe(jsonObject.getInt("subscribe"));
				// 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
				if(weixinUserInfo.getSubscribe()==1){
					// 用户关注时间
					weixinUserInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
					// 昵称
					weixinUserInfo.setNickname(jsonObject.getString("nickname"));
					// 用户的性别（1是男性，2是女性，0是未知）
					weixinUserInfo.setSex(jsonObject.getInt("sex"));
					// 用户所在国家
					weixinUserInfo.setCountry(jsonObject.getString("country"));
					// 用户所在省份
					weixinUserInfo.setProvince(jsonObject.getString("province"));
					// 用户所在城市
					weixinUserInfo.setCity(jsonObject.getString("city"));
					// 用户的语言，简体中文为zh_CN
					weixinUserInfo.setLanguage(jsonObject.getString("language"));
					// 用户头像
					weixinUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
				}else{
					logger.info("用户{}未关注", weixinUserInfo.getOpenId());
				}
			} catch (Exception e) {
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				logger.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return weixinUserInfo;
	}

	
	
	public static String long2short(String accessToken, String long_url) {
		boolean result = false;
		// 拼接请求地址
		String requestUrl = "https://huobi.weixin.qq.com/cgi-bin/shorturl?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 需要提交的json数据
		String jsonData = "{\"action\":\"long2short\",\"long_url\":\"%s\"}";
		// 移动用户分组
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, long_url));

		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				logger.info("长链接转短链接成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				logger.error("长链接转短链接失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		return jsonObject.getString("short_url");
	}

}
