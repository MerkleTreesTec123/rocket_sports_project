package com.qkwl.web.front.controller;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.capital.UserVirtualAddressWithdrawDTO;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.IdentityStatusEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.UserLoginType;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.user.*;
import com.qkwl.common.dto.capital.UserBankinfoDTO;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.framework.limit.LimitHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.i18n.LuangeHelper;
import com.qkwl.common.rpc.capital.IUserCapitalAccountService;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserVipService;
import com.qkwl.common.rpc.user.IUserApiService;
import com.qkwl.common.rpc.user.IUserIdentityService;
import com.qkwl.common.rpc.user.IUserSecurityService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.*;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;


@Controller
public class FrontUserJsonController extends JsonBaseController {

	private static final Logger logger = LoggerFactory.getLogger(FrontUserJsonController.class);

	@Autowired
	private IUserService userService;
	@Autowired
	private IUserSecurityService userSecurityService;
	@Autowired
	private IUserIdentityService userIdentityService;
	@Autowired
	private IUserVipService userVipService;
	@Autowired
	private ValidateHelper validateHelper;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private LimitHelper limitHelper;
	@Autowired
	private ValidationCheckHelper validationCheckHelper;
	@Autowired
	private IUserCapitalAccountService userCapitalAccountService;
	@Autowired
	private IUserApiService userApiService;

	/**
	 * 登录
	 */
	@ResponseBody
	@RequestMapping(value = "/login" )
	public ReturnResult loginIndex(
			@RequestParam(required = false, defaultValue = "0") Integer type,
			@RequestParam(required = true) String loginName,
			@RequestParam(required = true) String password
	)throws Exception {
		// 获取IP地址
		String ip = getIpAddr();

		if(StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password)){
			return ReturnResult.FAILUER(GetR18nMsg("user.login.error.1000"));
		}
		// 登录参数
		RequestUserInfo requestUserInfo = new RequestUserInfo();
		requestUserInfo.setFloginname(loginName);
		requestUserInfo.setType(type);
		requestUserInfo.setFagentid(WebConstant.BCAgentId);
		requestUserInfo.setPlatform(PlatformEnum.BC);
		// 登录
		try {
			requestUserInfo.setFloginpassword(Utils.MD5(password));
			Result result = userService.updateCheckLogin(requestUserInfo, UserLoginType.WebUser, ip, super.getLanEnum());
			if(result.getCode() == 200){
				LoginResponse login = (LoginResponse) result.getData();
				// 设置登录成功的Token
				sessionContextUtils.addContextToken("token",login.getToken());
				return ReturnResult.SUCCESS(ReturnResult.SUCCESS, login.getToken());
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(GetR18nMsg("common.error." + result.getCode()));
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				return ReturnResult.FAILUER(GetR18nMsg("user.login.error." + result.getCode(), result.getData()));
			} else{
				return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode(), result.getData()));
			}
		} catch (Exception e) {
			logger.error("用户登录异常:",e);
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10004"));
		}
	}

	/**
	 * 通过同浏览器的toink登陆
	 */
	@RequestMapping("/user/oauth.html")
	@ResponseBody
	public String userOauth(@RequestParam String token){
		sessionContextUtils.addContextToken("token", token);
		return "";
	}

	/**
	 * 注册
	 */
	@ResponseBody
	@RequestMapping(value = "/register")
	public ReturnResult regIndex(
			@RequestParam(required = false, defaultValue = "0") String password,
			@RequestParam(required = false, defaultValue = "0") String regName,
			@RequestParam(required = false, defaultValue = "0") Integer regType,
			@RequestParam(required = false, defaultValue = "0") String pcode,
			@RequestParam(required = false, defaultValue = "0") String ecode,
			@RequestParam(required = false, defaultValue = "86") String areaCode,
			@RequestParam(required = false, defaultValue = "0") String vcode,
			@RequestParam(required = false, defaultValue = "") String intro_user,
			@RequestParam(required = false, defaultValue = "1") Integer is_picture_code,
			@RequestParam(required = false,defaultValue = "") String tradePassword)throws Exception {
		// 获取IP
		String ip = getIpAddr();
		// 区号
		areaCode = areaCode.replace("+", "");
		// 检测密码强度
		if (!password.matches(Constant.passwordReg)) {
			return ReturnResult.FAILUER(-10, GetR18nMsg("com.validate.error.11009"));
		}				

		if (!TextUtils.isEmpty(tradePassword) && !tradePassword.matches(Constant.passwordReg)) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11009"));
		}							

		// 检测开放注册
		String isOpenReg = redisHelper.getSystemArgs("isOpenReg").trim();
		if (!isOpenReg.equals("1")) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11014"));
		}
		if (is_picture_code == 1) {
			String session_code = super.getRedisData("CHECKCODE");
			if (session_code == null || !vcode.equalsIgnoreCase(session_code)) {
				return ReturnResult.FAILUER(-20, GetR18nMsg("com.validate.error.11004"));
			} else {
				super.deletRedisData("CHECKCODE");
			}
		}


		RequestUserInfo userInfo = new RequestUserInfo();
		// 推广推荐ID
		if(StringUtils.isEmpty(intro_user)) {
			if(redisHelper.getSystemArgs(ArgsConstant.ISMUSTINTROL).equals("1"))
				return ReturnResult.FAILUER(GetR18nMsg("user.reg.error.1006"));
		}else{
			if(StringUtils.isNumeric(intro_user)){
				try {
					userInfo.setIntroUid(Integer.valueOf(intro_user));
				} catch (NumberFormatException e) {
					logger.error(e.getMessage());
					return ReturnResult.FAILUER(GetR18nMsg("user.reg.error.1006"));
				}
			}else{
				return ReturnResult.FAILUER(GetR18nMsg("user.reg.error.1006"));
			}
		}
		userInfo.setFareacode(areaCode);
		if(regType == 0){
			userInfo.setCode(pcode);
		}else{
			userInfo.setCode(ecode);
		}
		userInfo.setFloginname(regName);
		userInfo.setType(regType);
		userInfo.setFagentid(WebConstant.BCAgentId);
		userInfo.setFloginpassword(Utils.MD5(password));
		userInfo.setPlatform(PlatformEnum.BC);
		userInfo.setIp(ip);

		if (!TextUtils.isEmpty( tradePassword )) {
			userInfo.setPassword(Utils.MD5(password));
		}

		try {
			Result result = this.userService.insertRegister(userInfo, UserLoginType.WebUser);
			if(result.getCode() == 200){
				LoginResponse login = (LoginResponse) result.getData();
				// 设置登录成功的Token
                sessionContextUtils.addContextToken("token", login.getToken());
				return ReturnResult.SUCCESS(login.getUserinfo().getFid());
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(GetR18nMsg("common.error." + result.getCode()));
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				return ReturnResult.FAILUER(GetR18nMsg("user.reg.error." + result.getCode()));
			} else{
				return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode(), result.getData()));
			}
		} catch (Exception e) {
			logger.error("用户注册异常："+e.getMessage());
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
		}
	}

	/**
	 * 验证注册帐号是否存在
	 */
	@ResponseBody
	@RequestMapping(value = "/user/check_user_exist")
	public ReturnResult chcekregname(
			@RequestParam(required = false, defaultValue = "0") String name,
			@RequestParam(required = false, defaultValue = "1") Integer type
			) throws Exception {
		FUser fUser = new FUser();
		if (type == 0) {
			// 手机账号
			fUser.setFtelephone(name);
		} else {
			// 邮箱账号
			fUser.setFemail(name);
		}
		boolean flag = this.userService.selectIsExistByParam(fUser);
		if (type == 0 && flag) {
			return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1006"));
		} else if (flag) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11005"));
		}
		return ReturnResult.SUCCESS();
	}

	/**
	 * 发送注册码邮件
	 */
	@ResponseBody
	@RequestMapping(value = "/user/send_reg_email")
	public ReturnResult sendemail(
			@RequestParam(required = true, defaultValue = "0") String address,
			@RequestParam(required = false, defaultValue = "1") Integer msgtype,
			@RequestParam(required = false) String vcode) throws Exception {
		FUser fuser = new FUser();
		if (!address.equals("0")) {
			if (!address.matches(Constant.EmailReg)) {
				return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11007"));
			}
			fuser.setFemail(address);
			boolean flag = this.userService.selectIsExistByParam(fuser);
			if (flag) {
				return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11005"));
			}
			fuser.setFagentid(WebConstant.BCAgentId);

			String sessionImageCode = super.getRedisData("CHECKCODE");
			if (sessionImageCode == null || vcode == null || !sessionImageCode.equalsIgnoreCase(vcode)) {
				return ReturnResult.FAILUER(-20, GetR18nMsg("com.validate.error.11004"));
			}
		} else {
			//缓存用户
			FUser userInfo = getCurrentUserInfoByToken();
			//查找用户
			fuser = userService.selectUserById(userInfo.getFid());
			address = fuser.getFemail();
		}
		boolean emailvalidate = validateHelper.mailOverdueValidate(address, PlatformEnum.BC,  BusinessTypeEnum.EMAIL_REGISTER_CODE);
		if (emailvalidate) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11006"));
		}
		boolean result = false;
		try {
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			result = validateHelper.mailSendCode(address, PlatformEnum.BC, super.getLanEnum(), Utils.getIpAddr(request));
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		if (result) {
			return ReturnResult.SUCCESS();
		} else {
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
		}
	}

	/**
	 * 发送注册码短信
	 */
	@ResponseBody
	@RequestMapping(value = "/user/send_sms")
	public ReturnResult sendphone(
			@RequestParam(required = true, defaultValue = "0") String phone,
			@RequestParam(required = true, defaultValue = "0") String area,
			@RequestParam(required = false, defaultValue = "1") Integer is_picture_code,
			@RequestParam(required = false) String vcode) throws Exception {
		FUser fuser = new FUser();
		if (!phone.equals("0")) {
			fuser.setFtelephone(phone);
			boolean flag = this.userService.selectIsExistByParam(fuser);
			if (flag) {
				return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11003"));
			}
			fuser.setFagentid(WebConstant.BCAgentId);
			if (is_picture_code == 1) {
				String sessionImageCode = super.getRedisData("CHECKCODE");
				if (sessionImageCode == null || vcode == null || !sessionImageCode.equalsIgnoreCase(vcode)) {
					return ReturnResult.FAILUER(-20, GetR18nMsg("com.validate.error.11004"));
				}
			}
		} else {
			//缓存用户
			FUser userInfo = getCurrentUserInfoByToken();
			//查找用户
			fuser = this.userService.selectUserById(userInfo.getFid());
			phone = fuser.getFemail();
		}

		boolean result;
		try {
			result = validateHelper.smsValidateCode(0,area,phone,SendTypeEnum.SMS_TEXT.getCode(),
					PlatformEnum.BC.getCode(),BusinessTypeEnum.SMS_WEB_REGISTER.getCode(), super.getLanEnum().getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		if (result) {
			return ReturnResult.SUCCESS();
		} else {
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
		}
	}

	/**
	 * 发送绑定验证码的代码
	 */
	@ResponseBody
	@RequestMapping(value = "/user/send_bank_sms")
	public ReturnResult sendBindBankCode() throws Exception {
		FUser userInfo = getCurrentUserInfoByToken();
		if (userInfo == null){
			return ReturnResult.FAILUER("请先登录");
		}
		if (!userInfo.getFistelephonebind()){
			return ReturnResult.FAILUER("你还没有绑定手机，请先到安全中心绑定手机");
		}
		boolean result = false;
		try {
			result = validateHelper.smsValidateCode(userInfo.getFid(),
					userInfo.getFareacode(),userInfo.getFtelephone(),SendTypeEnum.SMS_TEXT.getCode(),
					PlatformEnum.BC.getCode(),BusinessTypeEnum.SMS_CNY_WITHDRAW.getCode(),super.getLanEnum().getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		if (result) {
			return ReturnResult.SUCCESS();
		} else {
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
		}
	}

	/**
	 * 添加谷歌设备
	 */
	@ResponseBody
	@RequestMapping(value = "/user/bind_google_device")
	public ReturnResult googleAuth() throws Exception {
		JSONObject jsonObject=new JSONObject();
		FUser fuser = getCurrentUserInfoByToken();

		fuser.setIp(getIpAddr());
		try {
			Result result = userSecurityService.updateUserSecurityInfo(fuser,null,LogUserActionEnum.MODIFY_GOOGLE, null);
			if(result.getSuccess()){
				Map<String, String> map =  (Map<String, String>)result.getData();
				fuser.setFgoogleauthenticator(map.get("secret"));
				fuser.setFgoogleurl(map.get("url"));
				//更新redis中的用户信息
				updateUserInfo(fuser);
			}
		} catch (Exception e) {
			logger.error("googleAuth err {}", e);
			e.printStackTrace();
		}

		jsonObject.put("code", 0);
		jsonObject.put("qecode", fuser.getFgoogleurl());
		jsonObject.put("totpKey", fuser.getFgoogleauthenticator());
		return ReturnResult.SUCCESS(jsonObject);
	}

	/**
	 * 添加设备认证`
	 */
	@ResponseBody
	@RequestMapping(value = "/user/google_auth")
	public ReturnResult validateAuthenticator(
			@RequestParam String code,
			@RequestParam String totpKey) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FUser fuser = getCurrentUserInfoByToken();
		//验证谷歌
		String ip = Utils.getIpAddr(request);
		ValidateParamInfo paramInfo = new ValidateParamInfo();
		paramInfo.setTotpKey(totpKey);
		paramInfo.setTotpCode(code);
		paramInfo.setIp(ip);

		fuser.setFgooglebind(true);
		fuser.setFgoogleauthenticator(totpKey);
		fuser.setIp(ip);
		try {
			Result result = userSecurityService.updateUserSecurityInfo(fuser, paramInfo, LogUserActionEnum.BIND_GOOGLE, ScoreTypeEnum.GOOGLE);
			if (result.getSuccess()) {
				//更新redis中的用户信息
				updateUserInfo(fuser);
				return ReturnResult.SUCCESS(GetR18nMsg("common.succeed.200"));
			} else if(result.getCode() >= 10000) {
				return ReturnResult.FAILUER(GetR18nMsg("com.validate.error." + result.getCode(), result.getData()));
			} else {
				return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + result.getCode()));
			}
		} catch (Exception e) {
			logger.error("preperGoogle err {}",e);
		}
		return ReturnResult.FAILUER(GetR18nMsg("common.error.400" ));
	}

	/**
	 * 查看谷歌密匙
	 */
	@ResponseBody
	@RequestMapping(value = "/user/get_google_key")
	public ReturnResult getGoogleTotpKey(
			@RequestParam String totpCode){
		JSONObject jsonObject = new JSONObject();
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FUser fuser = getCurrentUserInfoByToken();
		//验证谷歌
		String ip = Utils.getIpAddr(request);
		Result checkResult = validationCheckHelper.getGoogleCodeCheck(fuser.getFgoogleauthenticator(), totpCode, ip);
		if (!checkResult.getSuccess()) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error." + checkResult.getCode(), checkResult.getData()));
		}
		jsonObject.put("qecode", fuser.getFgoogleurl());
		jsonObject.put("code", 0);
		jsonObject.put("totpKey", fuser.getFgoogleauthenticator());
		return ReturnResult.SUCCESS(jsonObject);
	}

	/**
	 * 修改登录和交易密码
	 */
	@ResponseBody
	@RequestMapping(value = "/user/modify_passwd")
	public ReturnResult modifyPwd(
			@RequestParam(required = true) String newPwd,
			@RequestParam(required = false, defaultValue = "") String originPwd,
			@RequestParam(required = false, defaultValue = "0") String phoneCode,
			@RequestParam(required = false, defaultValue = "0") Integer pwdType,
			@RequestParam(required = true) String reNewPwd,
			@RequestParam(required = false, defaultValue = "0") String totpCode,
			@RequestParam(value = "identityCode", required = false) String identityCode)throws Exception{
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FUser fuser = getCurrentUserInfoByToken();
		if (!newPwd.equals(reNewPwd)) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11010"));
		}
		// 检测密码强度
		if (!newPwd.matches(Constant.passwordReg)) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11009"));
		}
		LogUserActionEnum action = LogUserActionEnum.MODIFY_LOGINPWD;
		BusinessTypeEnum msgType = BusinessTypeEnum.SMS_MODIFY_LOGIN_PASSWORD;
		//是否需要发送短信提醒
		if (pwdType == 0) {
			// 修改登录密码
			fuser.setFloginpassword(Utils.MD5(newPwd));
		} else {
			// 修改交易密码
			action = LogUserActionEnum.BIND_TRADEPWD;
			if (fuser.getFtradepassword() != null && fuser.getFtradepassword().trim().length() > 0) {
				action = LogUserActionEnum.MODIFY_TRADEPWD;
				fuser.setFtradepwdtime(Utils.getTimestamp());
			}
			fuser.setFtradepassword(Utils.MD5(newPwd));
			fuser.setFidentityno(identityCode);
			msgType = BusinessTypeEnum.SMS_MODIFY_TRADE_PASSWORD;
		}

		String ip = Utils.getIpAddr(request);

		ValidateParamInfo paramInfo = new ValidateParamInfo();
		paramInfo.setCode(phoneCode);
		paramInfo.setTotpCode(totpCode);
		paramInfo.setIp(ip);
		paramInfo.setPlatform(PlatformEnum.BC);
		paramInfo.setBusinessType(msgType);
		paramInfo.setLocale(getLanEnum());
		paramInfo.setOriginLoginPwd(Utils.MD5(originPwd));

		fuser.setFupdatetime(Utils.getTimestamp());
		fuser.setIp(ip);
		try {
			Result result = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, action, null);
			if(result.getSuccess()) {
				//更新redis中的用户信息
				if(action == LogUserActionEnum.MODIFY_LOGINPWD){
					deleteUserInfo();
				} else {
					updateUserInfo(fuser);
				}
				return ReturnResult.SUCCESS();
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(GetR18nMsg("com.validate.error." + result.getCode(), result.getData()));
			} else {
				return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + result.getCode()));
			}
		} catch (Exception e) {
			logger.error("modifyPwd error {}",e);
		}
		return ReturnResult.FAILUER(GetR18nMsg("common.error.400"));
	}

	/**
	 * 实名认证
	 */
	@ResponseBody
	@RequestMapping(value = "/real_name_auth")
	public ReturnResult userRealNameAuth(
			@RequestParam(required = false, defaultValue = "") String realname,
			@RequestParam(required = false, defaultValue = "-1") Integer identitytype,
			@RequestParam(required = false, defaultValue = "") String identityno,
			@RequestParam(required = false, defaultValue = "") String address,
			@RequestParam(required = true,defaultValue = "") String idCardZmImgURL,
			@RequestParam(required = true,defaultValue = "") String idCardFmImgURL,
			@RequestParam(required = true,defaultValue = "") String idCardScImgURL
	) throws Exception {
		if (!isOpenAuth()) {
			return  ReturnResult.FAILUER(GetR18nMsg("com.user.identity.error.1016"));
		}
		// 非空验证
		if (StringUtils.isEmpty(realname) || identitytype == -1 || StringUtils.isEmpty(identityno)
				|| StringUtils.isEmpty(idCardZmImgURL) || StringUtils.isEmpty(idCardFmImgURL) || StringUtils.isEmpty(idCardScImgURL) ) {
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10005"));
		}
		// 判断非空
		if (!identityno.isEmpty()) {
			// 转换小写
			identityno = identityno.toLowerCase();
		}

		realname = URLDecoder.decode(realname, "UTF-8");
		//过滤特殊字符
		realname = Utils.filtChart(realname);
		address = URLDecoder.decode(address, "UTF-8");

		FUser fuser = getCurrentUserInfoByToken();

		FUserIdentity identity = new FUserIdentity();
		identity.setFuid(fuser.getFid());
		identity.setFname(realname);
		identity.setFtype(identitytype);
		identity.setFcountry(address);
		identity.setFcode(identityno);
		identity.setFstatus(IdentityStatusEnum.WAIT.getCode());
		identity.setFcreatetime(new Date());
		identity.setIp(getIpAddr());
		identity.setIdCardScImgURL(idCardScImgURL);
		identity.setIdCardFmImgURL(idCardFmImgURL);
		identity.setIdCardZmImgURL(idCardZmImgURL);

		Result result = userIdentityService.updateNormalIdentity(identity);
		if(result.getSuccess()){
			return ReturnResult.SUCCESS();
		}
		return ReturnResult.FAILUER(result.getMsg());
	}

	/**
	 * 添加提现地址
	 */
	@ResponseBody
	@RequestMapping(value = "/user/save_withdraw_address")
	public ReturnResult modifyWithdrawBtcAddr(
			@RequestParam(required = false, defaultValue = "0") String phoneCode,
			@RequestParam(required = false, defaultValue = "0") String totpCode,
			@RequestParam(required = true) int symbol,
			@RequestParam(required = true) String withdrawAddr,
			@RequestParam(required = true) String password,
			@RequestParam(required = true) String remark) throws Exception {
		try{
			FUser fuser = super.getCurrentUserInfoByToken();
			fuser = userService.selectUserById(fuser.getFid());
			String ip = getIpAddr();
			//查找币种
			SystemCoinType coinType = redisHelper.getCoinType(symbol);
			if (coinType == null || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
				return ReturnResult.FAILUER(GetR18nMsg("com.trade.error.10000"));
			}
			if (coinType.getCoinType().equals(SystemCoinSortEnum.EOS.getCode())) {
				if (TextUtils.isEmpty(withdrawAddr) || 12 != withdrawAddr.length()) {
					return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11017"));
				}
			} else if(!coinType.getCoinType().equals(SystemCoinSortEnum.GXS.getCode())){
				if(!(withdrawAddr.length() >= 20 && withdrawAddr.length() <= 34) && withdrawAddr.length() != 42){
					return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11017"));
				}
				if(withdrawAddr.length() == 42){
					String withdarwheader=withdrawAddr.substring(0, 2);
					if(!withdarwheader.equals("0x")){
						return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11017"));
					}
				}
			}
			UserVirtualAddressWithdrawDTO address = new UserVirtualAddressWithdrawDTO();
			address.setFuid(fuser.getFid());
			address.setInit(true);
			address.setFcreatetime(new Date());
			address.setFadderess(withdrawAddr);
			address.setFcoinid(coinType.getId());
			address.setFremark(remark);
			address.setVersion(0);
			address.setPhoneCode(phoneCode);
			address.setGoogleCode(totpCode);
			address.setIp(ip);
			address.setPassword(Utils.MD5(password));
			address.setPlatform(PlatformEnum.BC);
			Result result = userCapitalAccountService.createCoinAddressWithdraw(address);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(GetR18nMsg("common.error" + result.getCode()));
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				return ReturnResult.FAILUER(GetR18nMsg("user.address.error." + result.getCode()));
			} else{
				return ReturnResult.FAILUER(GetR18nMsg("com.error."+result.getCode(), result.getData().toString()));
			}
		}catch (Exception e){
			logger.error("添加提现地址异常", e);
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
		}
	}

	/**
	 * 删除提现地址
	 */
	@ResponseBody
	@RequestMapping(value = "/user/del_withdraw_address")
	public ReturnResult detelCoinAddress(
			@RequestParam(required = true) int fid) throws Exception {
		try {
			FUser fuser = super.getCurrentUserInfoByToken();

			Result result = userCapitalAccountService.deleteCoinAddressWithdraw(fuser.getFid(), fid);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(GetR18nMsg("common.error." + result.getCode()));
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				return ReturnResult.FAILUER(GetR18nMsg("user.address.error." + result.getCode()));
			} else{
				return ReturnResult.FAILUER(GetR18nMsg("com.error."+result.getCode()));
			}
		} catch (Exception e) {
			logger.error("删除提现地址:", e);
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
		}
	}

	/**
	 * 新增银行卡
	 */
	@ResponseBody
	@RequestMapping(value = "/user/save_bankinfo")
	public ReturnResult updateOutAddress(
			@RequestParam(required = true) String account,
			@RequestParam(required = false, defaultValue = "0") String phoneCode,
			@RequestParam(required = false, defaultValue = "0") String totpCode,
			@RequestParam(required = true) int openBankType,
			@RequestParam(required = true) String address,
			@RequestParam(required = true) String prov,
			@RequestParam(required = true) String city,
			@RequestParam(required = false) String dist,
			@RequestParam(required = true) String payeeAddr,
			@RequestParam(required = true) Integer bankId) throws Exception {
		try{
			String ip = getIpAddr();
			// 用户
			FUser fuser = super.getCurrentUserInfoByToken();

			UserBankinfoDTO userBankinfo = new UserBankinfoDTO();
			userBankinfo.setId(bankId);
			userBankinfo.setUserId(fuser.getFid());
			userBankinfo.setSystemBankId(openBankType);
			userBankinfo.setRealName(payeeAddr);
			userBankinfo.setBankNumber(HtmlUtils.htmlEscape(account));
			userBankinfo.setProv(prov);
			userBankinfo.setCity(city);
			userBankinfo.setDist(dist);
			userBankinfo.setAddress(address);
			userBankinfo.setPlatform(PlatformEnum.BC);
			userBankinfo.setPhoneCode(phoneCode);
			userBankinfo.setTotpCode(totpCode);
			userBankinfo.setType(WithdrawBankTypeEnum.Bank.getCode());
			userBankinfo.setIp(ip);

			Result result = userCapitalAccountService.createOrUpdateBankInfo(userBankinfo);
			if(result.getCode() == 200){
				return ReturnResult.SUCCESS();
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(GetR18nMsg("common.error." + result.getCode()));
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				return ReturnResult.FAILUER(GetR18nMsg("capital.bank.withdraw." + result.getCode()));
			} else{
				return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode(), result.getData()));
			}
		}catch (Exception e){
			logger.error("新增提现银行卡异常：", e);
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
		}
	}

	/**
	 * 登录
	 */
	@ResponseBody
	@RequestMapping(value = "/user/login_json")
	public ReturnResult login(@RequestParam(required = false, defaultValue = "") String forwardUrl) throws Exception {
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("forwardUrl", forwardUrl);
		return ReturnResult.SUCCESS(jsonObject);
	}

	/**
	 * 添加用户申请api
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/user/auth")
	public ReturnResult authApi(@RequestParam(required = true,defaultValue = "0") String phone,
								@RequestParam(required = true,defaultValue = "0") String areaCode,
								@RequestParam(required = true, defaultValue = "0") String phoneCode) throws Exception{
		if ("0".equals(phone) || "0".equals(phoneCode) || "0".equals(areaCode)) {
			return ReturnResult.FAILUER("参数错误");
		}
		FUser fuser = getCurrentUserInfoByToken();
		if (fuser == null){
			return ReturnResult.FAILUER("登录失效，请重新登录");
		}
		//检查用户手机号
		if (!fuser.getFtelephone().equals(phone)){
			return ReturnResult.FAILUER("参数错误，手机号与当前用户绑定的手机号不一致");
		}
		try {
			String ipAddr = getIpAddr();
			RequestUserInfo requestUserInfo = new RequestUserInfo();
			requestUserInfo.setCode(phoneCode);
			requestUserInfo.setFareacode(areaCode);
			requestUserInfo.setIp(ipAddr);
			requestUserInfo.setPlatform(PlatformEnum.BC);
			requestUserInfo.setFloginname(phone);
			FApiAuth fApiAuth = userApiService.insertApi(fuser.getFid(), ipAddr,requestUserInfo);
			if (fApiAuth == null){
				return ReturnResult.FAILUER("添加失败");
			}else{
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("apiKey",fApiAuth.getFapikey());
				jsonObject.put("apiSecret",fApiAuth.getFsecretkey());
				return ReturnResult.SUCCESS(jsonObject);
			}
		}catch (Exception e){
			logger.error("申请api异常：", e);
			return ReturnResult.FAILUER(e.getMessage());
		}
	}

	/**
	 * 发送申请api的验证码
	 */
	@ResponseBody
	@RequestMapping(value = "/user/send_api_sms")
	public ReturnResult sendApiPhoneCode(
			@RequestParam(required = true, defaultValue = "0") String phone,
			@RequestParam(required = true, defaultValue = "0") String area) throws Exception {

		if ("0".equals(phone) || "0".equals(area)){
			return ReturnResult.FAILUER("参数不正确");
		}

		FUser fuser = new FUser();
		fuser.setFtelephone(phone);
		boolean flag = this.userService.selectIsExistByParam(fuser);
		if (!flag) {
			return ReturnResult.FAILUER("用户不存在");
		}

		boolean result = false;
		try {
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			//发送申请api的短信验证码
			result = validateHelper.smsValidateCode(0,area,phone,SendTypeEnum.SMS_INTERNATIONAL.getCode(),
					PlatformEnum.BC.getCode(),BusinessTypeEnum.SMS_APPLY_API.getCode(), LocaleEnum.EN_US.getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		if (result) {
			return ReturnResult.SUCCESS();
		} else {
			return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
		}
	}

	/**
	 * 手机注册
	 */
	@ResponseBody
	@RequestMapping(value = "/user/phonereg_json")
	public ReturnResult regPhone() throws Exception{
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("areaCodes", LuangeHelper.getAreaCode(LocaleEnum.ZH_CN.getCode()));
		jsonObject.put("defaultAreaName", "中国");
		jsonObject.put("defaultAreaCode", "86");
		return ReturnResult.SUCCESS(jsonObject);
	}

	/**
	 * 注册
	 */
	@ResponseBody
	@RequestMapping(value = "/user/register_json")
	public ReturnResult register(@RequestParam(required = false, defaultValue = "") String uid) throws Exception {
		// 推荐注册
		FUser intro = null;
		try {
			if (uid.isEmpty()) {
				Cookie cs[] = sessionContextUtils.getContextRequest().getCookies();
				if(cs != null && cs.length > 0){
					for (Cookie cookie : cs) {
						if (cookie.getName().endsWith("r")) {
							intro = this.userService.selectUserByShowId(Integer.parseInt(cookie.getValue()));
							break;
						}
					}
				}
			} else {
				intro = this.userService.selectUserByShowId(Integer.parseInt(uid));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		JSONObject jsonObject=new JSONObject();
		if (intro != null) {
			jsonObject.put("intro", intro.getFshowid());
		}
		return ReturnResult.SUCCESS(jsonObject);
	}


	/**
	 * 用户申请api的列表
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/user/authlist")
	public ReturnResult authApiList() throws Exception{
		FUser fuser = getCurrentUserInfoByToken();
		if (fuser == null){
			return ReturnResult.FAILUER("登录失效，请重新登录");
		}
		List<FApiAuth> fApiAuths = userApiService.selectApiByUser(fuser.getFid());
		if (fApiAuths == null || fApiAuths.size() == 0){
			return ReturnResult.FAILUER("");
		}
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < fApiAuths.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("apiKey",fApiAuths.get(i).getFapikey());
			jsonObject.put("apiSecret",fApiAuths.get(i).getFsecretkey());
			jsonObject.put("createTime",fApiAuths.get(i).getFcreatetime());
			jsonArray.add(i,jsonObject);
		}
		return ReturnResult.SUCCESS(jsonArray);
	}

	/**
	 * 安全设置
	 */
	@ResponseBody
	@RequestMapping(value = "/user/security_json")
	public ReturnResult userSecurity() throws Exception {
		FUser userInfo = getCurrentUserInfoByToken();
		FUser fuser = userService.selectUserById(userInfo.getFid());
		updateUserInfo(fuser);

		FUserIdentity identity = userIdentityService.selectByUser(fuser.getFid());
		String device_name = Constant.GoogleAuthName + "--" + fuser.getFloginname();

		boolean isBindGoogle = fuser.getFgooglebind() == null ? false : fuser.getFgooglebind();
		boolean isBindTelephone = fuser.getFistelephonebind() == null ? false : fuser.getFistelephonebind();
		boolean isEmail = fuser.getFismailbind() == null ? false : fuser.getFismailbind();
		boolean isTradePass = fuser.getFtradepassword() != null && !fuser.getFtradepassword().equals("");
		boolean isLoginPass = fuser.getFloginpassword() != null && !fuser.getFloginpassword().equals("");
		boolean isIdentity = identity != null && identity.getFstatus() == 1;

		int bindcount = 0;
		if (isBindGoogle) {
			bindcount++;
		}
		if (isBindTelephone) {
			bindcount++;
		}
		if (isEmail) {
			bindcount++;
		}
		if (isTradePass) {
			bindcount++;
		}
		if (isLoginPass) {
			bindcount++;
		}
		if (isIdentity){
			bindcount++;
		}

		String loginName = Utils.formatloginName(fuser.getFloginname());
		int securityLevel = 1;
		if (bindcount >= 2) {
			securityLevel = 2;
		}
		JSONObject jsonObject=new JSONObject();
		if (fuser.getFemail() != null && !fuser.getFemail().equals("")) {
			String[] emails = fuser.getFemail().split("@");
			String emaString = "";
			if (emails[0].length() > 3) {
				emaString = emails[0].substring(0, 3) + "****@" + emails[1];
			} else {
				emaString = emails[0].substring(0, 1) + "****@" + emails[1];
			}
			jsonObject.put("emaString", emaString);
		}
		if (fuser.getFtelephone() != null && !fuser.getFtelephone().equals("")){
			String phoneString = fuser.getFtelephone();
			phoneString = phoneString.substring(0,3)+"****"+phoneString.substring(7);
			jsonObject.put("phoneString", "+"+fuser.getFareacode()+" "+phoneString);
		}
		jsonObject.put("bindcount", bindcount);
		jsonObject.put("loginName", loginName);
		jsonObject.put("device_name", device_name);
		jsonObject.put("securityLevel", securityLevel);
		jsonObject.put("bindLogin", !StringUtils.isEmpty(fuser.getFloginpassword()));
		jsonObject.put("bindTrade", !StringUtils.isEmpty(fuser.getFtradepassword()));
		jsonObject.put("fuser", ModelMapperUtils.mapper(fuser, FUserDTO.class));
		jsonObject.put("identity", identity);
		return ReturnResult.SUCCESS(jsonObject);
	}

	/**
	 * 登录日志
	 */
	@ResponseBody
	@RequestMapping(value = "/user/user_loginlog_json")
	public ReturnResult userLoginlog(@RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
		int pagesize = 10;
		FUser userInfo = getCurrentUserInfoByToken();

		Pagination<FLogUserAction> flogs = new Pagination<FLogUserAction>(currentPage, pagesize, "/index.php?s=exc&c=securityController&");
		flogs = userSecurityService.listSettingLogByUser(userInfo.getFid(), flogs,1);

		JSONObject jsonObject=new JSONObject();
		jsonObject.put("flogs", flogs);
		jsonObject.put("pagin", flogs.getPagin());
		return ReturnResult.SUCCESS(jsonObject);
	}

	/**
	 * 安全设置日志
	 */
	@ResponseBody
	@RequestMapping(value = "/user/user_settinglog_json")
	public ReturnResult userSettinglog(@RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
		int pagesize = 10;
		FUser userInfo = getCurrentUserInfoByToken();

		Pagination<FLogUserAction> flogs = new Pagination<FLogUserAction>(currentPage, pagesize, "/index.php?s=exc&c=securityController&type=safelog&");
		flogs = userSecurityService.listSettingLogByUser(userInfo.getFid(), flogs,2);

		JSONObject jsonObject=new JSONObject();
		jsonObject.put("flogs", flogs);
		return ReturnResult.SUCCESS(jsonObject);
	}

	/**
	 * 通过手机找回密码-重置密码
	 */
	@ResponseBody
	@RequestMapping("/user/findtradepwd")
	public ReturnResult resetTradePwdByPhone(
			@RequestParam(required = false, defaultValue = "0") String phone,
			@RequestParam(required = false, defaultValue = "0") String area,
			@RequestParam(required = false, defaultValue = "0") String code,
			@RequestParam(required = false, defaultValue = "0") String totpCode,
			@RequestParam String newPassword,
			@RequestParam String newPassword2) throws Exception {
		FUser currentUserInfoByApiToken = getCurrentUserInfoByToken();
		if (currentUserInfoByApiToken == null) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,"请重新登录");
		}

		HttpServletRequest request = sessionContextUtils.getContextRequest();
		// 检测密码强度
		if (!newPassword.matches(Constant.passwordReg) || newPassword.length() < 6) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11009"));
		}

		if (!newPassword.equals(newPassword2)) {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11010"));
		}

		if (TextUtils.isEmpty(phone)){
			return ReturnResult.FAILUER("");
		}

		if (!phone.equals(currentUserInfoByApiToken.getFtelephone())){
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11012"));
		}

		FUser fuser = this.userService.selectUserById(currentUserInfoByApiToken.getFid());
		if (TextUtils.isEmpty(fuser.getFtradepassword())){
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.10003"));
		}

		//验证码校验
		if (fuser.getFistelephonebind()) {
			//验证手机验证码
			Result phoneCodeCheckResult = validationCheckHelper.getPhoneCodeCheck(area, phone, code, BusinessTypeEnum.SMS_MODIFY_TRADE_PASSWORD.getCode(),
					Utils.getIpAddr(sessionContextUtils.getContextRequest()), PlatformEnum.BC.getCode());
			if (!phoneCodeCheckResult.getSuccess()) {
				return ReturnResult.FAILUER(phoneCodeCheckResult.getMsg());
			}
		} else {
			return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11012"));
		}

		String ip = Utils.getIpAddr(request);

		ValidateParamInfo validateParamInfo = new ValidateParamInfo();
		validateParamInfo.setBusinessType(BusinessTypeEnum.SMS_MODIFY_TRADE_PASSWORD);
		validateParamInfo.setPlatform(PlatformEnum.BC);
		validateParamInfo.setIp(ip);
		validateParamInfo.setTotpCode(totpCode);

		fuser.setFtradepassword(Utils.MD5(newPassword));
		fuser.setIp(ip);
		fuser.setFtelephone(phone);
		try {
			Result result = userSecurityService.restTradePassword(fuser, validateParamInfo);
			if (result.getSuccess()) {
				return ReturnResult.SUCCESS();
			}
			return ReturnResult.FAILUER(GetR18nMsg("user.findPassword.error." + result.getCode()));
		} catch (Exception e) {
			logger.error("resetPassword err {}", e);
		}
		return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
	}


	/**
	 * 退出
	 */

	@RequestMapping(value = "/user/logout")
	public ReturnResult logout() throws Exception {
		// 删除登陆中的用户
		super.deleteUserInfo();
		return ReturnResult.SUCCESS();
	}




}
