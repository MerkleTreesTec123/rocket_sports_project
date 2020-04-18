package com.qkwl.web.front.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.capital.*;
import com.qkwl.common.dto.coin.SystemCoinSetting;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemCoinTypeVO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FinanceRecordDTO;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.system.FSystemBankinfoWithdraw;
import com.qkwl.common.dto.user.*;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.dto.wallet.CurrencyRate;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserCapitalAccountService;
import com.qkwl.common.rpc.capital.IUserCapitalService;
import com.qkwl.common.rpc.capital.IUserRewardCodeService;
import com.qkwl.common.rpc.capital.IUserWalletService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

/**
 * 用户相关的接口，包括：登录、注册、绑定验证、用户资产
 */
@Controller
public class UserApiController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    IUserService userService;

    @Autowired
    private IUserWalletService userWalletService;

    @Autowired
    private IUserIdentityService userIdentityService;

    @Autowired
    private IUserCapitalAccountService userCapitalAccountService;

    @Autowired
    private IUserCapitalService userCapitalService;

    @Autowired
    private IUserSecurityService userSecurityService;

    @Autowired
    private IUserRewardCodeService rewardCodeService;

    @Autowired
    private RedisHelper redisHelper;


    @Autowired
    private ValidationCheckHelper validationCheckHelper;

    @ResponseBody
    @RequestMapping(value = "/v1/user/register", method = RequestMethod.POST)
    public ReturnResult regIndex(
            @RequestParam(required = false, defaultValue = "0") String password,
            @RequestParam(required = false, defaultValue = "0") String regName,
            @RequestParam(required = false, defaultValue = "0") Integer regType,
            @RequestParam(required = false, defaultValue = "0") String pcode,
            @RequestParam(required = false, defaultValue = "0") String ecode,
            @RequestParam(required = false, defaultValue = "86") String area,
            @RequestParam(required = false, defaultValue = "") String intro_user,
            @RequestParam(required = false,defaultValue = "") String tradePassword) throws Exception {
        // 获取IP
        String ip = getIpAddr();
        // 区号
        area = area.replace("+", "");
        // 检测密码强度
        if (!password.matches(Constant.passwordReg)) {
            return ReturnResult.FAILUER(-10, GetR18nMsg("com.validate.error.11009"));
        }
        
        if (!TextUtils.isEmpty(tradePassword)) {
           if (!tradePassword.matches(Constant.passwordReg)) {
                return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11009"));
            }
        }
        // 检测开放注册
        String isOpenReg = redisHelper.getSystemArgs("isOpenReg").trim();
        if (!isOpenReg.equals("1")) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11014"));
        }

        RequestUserInfo userInfo = new RequestUserInfo();
        // 推广推荐ID
        if (StringUtils.isEmpty(intro_user)) {
            if (redisHelper.getSystemArgs(ArgsConstant.ISMUSTINTROL).equals("1"))
                return ReturnResult.FAILUER(GetR18nMsg("user.reg.error.1006"));
        } else {
            try {
                if (StringUtils.isNumeric(intro_user)) {
                    try {
                        userInfo.setIntroUid(Integer.valueOf(intro_user));
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage());
                        return ReturnResult.FAILUER(GetR18nMsg("user.reg.error.1006"));
                    }
                } else {
                    return ReturnResult.FAILUER(GetR18nMsg("user.reg.error.1006"));
                }
            }catch (NumberFormatException e){
                return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
            }
        }
        userInfo.setFareacode(area);
        if (regType == 0) {
            userInfo.setCode(pcode);
        } else {
            userInfo.setCode(ecode);
        }
        userInfo.setFloginname(regName);
        userInfo.setType(regType);
        userInfo.setFagentid(WebConstant.BCAgentId);
        userInfo.setFloginpassword(Utils.MD5(password));
        userInfo.setPlatform(PlatformEnum.BC);
        userInfo.setIp(ip);
        if (!TextUtils.isEmpty(tradePassword)) {
            userInfo.setPassword(Utils.MD5(tradePassword));
        }
        try {
            Result result = this.userService.insertRegister(userInfo, UserLoginType.APPUser);
            if (result.getCode() == 200) {
                LoginResponse login = (LoginResponse) result.getData();
                // 设置登录成功的Token
                sessionContextUtils.addContextToken("token", login.getToken());
                return ReturnResult.SUCCESS(login.getUserinfo().getFid());
            } else if (result.getCode() > 200 && result.getCode() < 1000) {
                return ReturnResult.FAILUER(GetR18nMsg("common.error." + result.getCode()));
            } else if (result.getCode() >= 1000 && result.getCode() < 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("user.reg.error." + result.getCode()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode(), result.getData()));
            }
        } catch (Exception e) {
            logger.error("用户注册异常：" + e.getMessage());
            return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10000"));
        }
    }

    /**
     * @param type      类型 0 手机 1邮箱
     * @param loginName
     * @param password
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/login", method = RequestMethod.POST)
    public ReturnResult loginIndex(
            @RequestParam(required = false, defaultValue = "0") Integer type,
            @RequestParam(required = true) String loginName,
            @RequestParam(required = true) String password
    ) throws Exception {
        // 获取IP地址
        String ip = getIpAddr();

        if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password)) {
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
            Result result = userService.updateCheckLogin(requestUserInfo, UserLoginType.APPUser, ip, super.getLanEnum());
            if (result.getCode() == 200) {
                LoginResponse login = (LoginResponse) result.getData();
                return ReturnResult.SUCCESS(ReturnResult.SUCCESS, login);
            } else if (result.getCode() > 200 && result.getCode() < 1000) {
                return ReturnResult.FAILUER(GetR18nMsg("common.error." + result.getCode()));
            } else if (result.getCode() >= 1000 && result.getCode() < 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("user.login.error." + result.getCode(), result.getData()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode(), result.getData()));
            }
        } catch (Exception e) {
            logger.error("用户登录异常:", e);
            return ReturnResult.FAILUER(GetR18nMsg("com.public.error.10004"));
        }
    }

    /**
     * 获取用户余额
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/balance", method = RequestMethod.GET)
    public ReturnResult balance(String base) throws Exception {
        FUser user = getCurrentUserInfoByApiToken();
        if (user == null) {
            return ReturnResult.FAILUER("请重新登录");
        }

        Integer fid = user.getFid();
        JSONObject result = new JSONObject();
        List<UserCoinWallet> userCoinWallets;
        try {
            userCoinWallets = userWalletService.listUserCoinWallet(fid);
            Iterator iterator = userCoinWallets.iterator();
            while (iterator.hasNext()) {
                UserCoinWallet wallet = (UserCoinWallet) iterator.next();
                if (!redisHelper.hasCoinId(wallet.getCoinId())) {
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER("请登录！");
        }

        if (TextUtils.isEmpty(base)) {
            base = "CNY";
        }
        
        List<SystemCoinType> systemCoinTypes = redisHelper.getCoinTypeListAll();
        for (UserCoinWallet wallet : userCoinWallets) {
            for (SystemCoinType coinType : systemCoinTypes) {
                if (wallet.getCoinId().equals(coinType.getId())){
                    wallet.setCanDeposit(coinType.getIsRecharge());
                    wallet.setCanWithdraw(coinType.getIsWithdraw());
                }
            }
        }

        CurrencyRate currencyRateObj;
        String currencyJson = redisHelper.get("currency_" + base);
        if (TextUtils.isEmpty(currencyJson)) {
            currencyRateObj = userWalletService.getCurrencyRate(base);
            if (currencyRateObj != null) {
                redisHelper.setNoExpire("currency_" + base, JSON.toJSONString(currencyRateObj));
            }
        } else {
            currencyRateObj = JSON.parseObject(currencyJson,CurrencyRate.class);
        }

        if (currencyRateObj == null) {
            return ReturnResult.FAILUER("system not support this currency rate");
        }

        BigDecimal netAssets = getNetAssets(userCoinWallets);
        result.put("netassets", netAssets.multiply(currencyRateObj.getRate()));
        result.put("totalassets", getTotalAssets(userCoinWallets));
        result.put("wallet", userCoinWallets);
        return ReturnResult.SUCCESS(result);
    }



    /**
     * 测试签名
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/test")
    public ReturnResult testSign() {
        return ReturnResult.SUCCESS();
    }

    /**
     * 获取安全设置详情
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/security", method = RequestMethod.GET)
    public ReturnResult userSecurity() throws Exception {
        FUser userInfo = getCurrentUserInfoByApiToken();
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
        if (isIdentity) {
            bindcount++;
        }

        String loginName = Utils.formatloginName(fuser.getFloginname());
        int securityLevel = 1;
        if (bindcount >= 2) {
            securityLevel = 2;
        }
        JSONObject jsonObject = new JSONObject();
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
        if (fuser.getFtelephone() != null && !fuser.getFtelephone().equals("")) {
            String phoneString = fuser.getFtelephone();
            phoneString = phoneString.substring(0, 3) + "****" + phoneString.substring(7);
            jsonObject.put("phoneString", "+" + fuser.getFareacode() + " " + phoneString);
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
     * 获取虚拟币充值地址和近十次充值记录
     *
     * @param symbol
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/deposit/coin", method = RequestMethod.GET)
    public ReturnResult rechargeBtc(@RequestParam(required = false, defaultValue = "0") Integer symbol) throws Exception {
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsRecharge()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
            return ReturnResult.FAILUER("暂停充值");
        }
        FUser user = getCurrentUserInfoByApiToken();
        // 充值地址
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        Result result = userCapitalAccountService.createCoinAddressRecharge(user.getFid(), coinType.getId(), ip);

        JSONObject jsonObject = new JSONObject();
        if (result.getSuccess()) {
            jsonObject.put("rechargeAddress", result.getData());
        }else {
            jsonObject.put("rechargeAddress",new JSONObject());
        }
        // 最近十次充值记录
        Pagination<FVirtualCapitalOperationDTO> page = new Pagination<>(1, Constant.CapitalRecordPerPage);
        FVirtualCapitalOperationDTO operation = new FVirtualCapitalOperationDTO();
        operation.setFuid(user.getFid());
        operation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
        operation.setFcoinid(coinType.getId());
        page = userCapitalService.listVirtualCapitalOperation(page, operation);
        jsonObject.put("page", page);
        jsonObject.put("coinType", ModelMapperUtils.mapper(coinType, SystemCoinTypeVO.class));
        if (coinType.getCoinType().equals(SystemCoinSortEnum.EOS.getCode())) {
            jsonObject.put("memo",user.getFid());
        }
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 修改/绑定，登录和交易密码
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/password", method = RequestMethod.POST)
    public ReturnResult modifyPwd(
            @RequestParam(required = true) String newPwd,
            @RequestParam(required = false, defaultValue = "") String originPwd,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") Integer pwdType,
            @RequestParam(required = true) String reNewPwd,
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam(value = "identityCode", required = false) String identityCode) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FUser fuser = getCurrentUserInfoByApiToken();
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
            if (result.getSuccess()) {
                //更新redis中的用户信息
                if (action == LogUserActionEnum.MODIFY_LOGINPWD) {
                    deleteUserInfo();
                } else {
                    updateUserInfo(fuser);
                }
                return ReturnResult.SUCCESS();
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("com.validate.error." + result.getCode(), result.getData()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + result.getCode()));
            }
        } catch (Exception e) {
            logger.error("modifyPwd error {}", e);
        }
        return ReturnResult.FAILUER(GetR18nMsg("common.error.400"));
    }

    /**
     * 添加谷歌设备
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/google_device", method = RequestMethod.GET)
    public ReturnResult googleAuth() throws Exception {
        JSONObject jsonObject = new JSONObject();
        FUser fuser = getCurrentUserInfoByApiToken();
        fuser.setIp(getIpAddr());
        try {
            Result result = userSecurityService.updateUserSecurityInfo(fuser, null, LogUserActionEnum.MODIFY_GOOGLE, null);
            if (result.getSuccess()) {
                Map<String, String> map = (Map<String, String>) result.getData();
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
     * 添加Google认证`
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/google_auth", method = RequestMethod.POST)
    public ReturnResult validateAuthenticator(
            @RequestParam String code,
            @RequestParam String totpKey) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FUser fuser = getCurrentUserInfoByApiToken();
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
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("com.validate.error." + result.getCode(), result.getData()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + result.getCode()));
            }
        } catch (Exception e) {
            logger.error("preperGoogle err {}", e);
        }
        return ReturnResult.FAILUER(GetR18nMsg("common.error.400"));
    }

    /**
     * 实名认证
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/real_auth", method = RequestMethod.POST)
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

        FUser fuser = getCurrentUserInfoByApiToken();

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
        if (result.getSuccess()) {
            return ReturnResult.SUCCESS();
        }
        return ReturnResult.FAILUER(result.getMsg());
    }

    /**
     * 绑定手机的短信
     *
     * @param phone
     * @param area
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/bind_msg", method = RequestMethod.GET)
    public ReturnResult postPhone(@RequestParam(required = false, defaultValue = "0") String phone,
                                  @RequestParam(required = false, defaultValue = "0") String area) throws Exception {
        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FUser fuser = getCurrentUserInfoByApiToken();
        if (phone.equals("0")) {
            //判断手机是否为空
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1003"));
        }
        fuser.setFtelephone(phone);

        String ip = Utils.getIpAddr(request);

        ValidateParamInfo paramInfo = new ValidateParamInfo();
        paramInfo.setIp(ip);

        paramInfo.setPlatform(PlatformEnum.BC);
        paramInfo.setBusinessType(BusinessTypeEnum.SMS_BING_MOBILE);
        paramInfo.setLocale(super.getLanEnum());
        paramInfo.setAreaCode(area);
        paramInfo.setPhone(phone);

        Result validateResult = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, LogUserActionEnum.ADD_PHONE, null);
        if (!validateResult.getSuccess()) {
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + validateResult.getCode()));
        }
        //验证短信已经发送，请及时验证！
        return ReturnResult.SUCCESS(GetR18nMsg("user.security.error.1017"));
    }

    /**
     * 绑定手机
     *
     * @param phone 手机号
     * @param area  地区码
     * @param code  验证码
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/bind_phone", method = RequestMethod.POST)
    public ReturnResult bindPhone(@RequestParam(required = false, defaultValue = "0") String phone,
                                  @RequestParam(required = false, defaultValue = "0") String area,
                                  @RequestParam(required = false, defaultValue = "0") String code) throws Exception {

        HttpServletRequest request = sessionContextUtils.getContextRequest();
        FUser fuser = getCurrentUserInfoByApiToken();
        if (phone.equals("0")) {
            //判断手机是否为空
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error.1003"));
        }
        fuser.setFtelephone(phone);

        String ip = Utils.getIpAddr(request);
        fuser.setIp(ip);
        ValidateParamInfo paramInfo = new ValidateParamInfo();
        paramInfo.setIp(ip);
        paramInfo.setPlatform(PlatformEnum.BC);
        paramInfo.setBusinessType(BusinessTypeEnum.SMS_BING_MOBILE);
        paramInfo.setLocale(super.getLanEnum());
        paramInfo.setSecondAreaCode(area);
        paramInfo.setSecondPhone(phone);
        paramInfo.setSecondCode(code);
        paramInfo.setCode(code);

        Result validateResult = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, LogUserActionEnum.BIND_PHONE, null);
        if (!validateResult.getSuccess()) {
            logger.error(validateResult.getMsg());
            return ReturnResult.FAILUER(GetR18nMsg("user.security.error." + validateResult.getCode()));
        }
        //验证短信已经发送，请及时验证！
        return ReturnResult.SUCCESS(GetR18nMsg("user.security.error.1017"));
    }

    /**
     *
     * 添加虚拟币提现地址
     *
     * @param phoneCode
     * @param totpCode
     * @param symbol
     * @param withdrawAddr
     * @param password
     * @param remark
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/coin/address",method = RequestMethod.POST)
    public ReturnResult coinWithdraw(
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam(required = true) int symbol,
            @RequestParam(required = true) String withdrawAddr,
            @RequestParam(required = true) String password,
            @RequestParam(required = true) String remark) throws Exception {
        try{
            FUser fuser = getCurrentUserInfoByApiToken();
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
	@RequestMapping(value = "/v1/coin/address/del")
	public ReturnResult detelCoinAddress(
			@RequestParam(required = true) int addressId) throws Exception {
		try {
            FUser fuser = super.getCurrentUserInfoByApiToken();
            
			Result result = userCapitalAccountService.deleteCoinAddressWithdraw(fuser.getFid(), addressId);
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
     * 虚拟币提现地址列表
     */
    @ResponseBody
    @RequestMapping(value = "/v1/coin/list_address",method = RequestMethod.GET)
    public ReturnResult withdrawBtc(@RequestParam(required = false) Integer symbol) throws Exception {
        List<SystemCoinType> systemCoinTypes =  redisHelper.getCoinTypeListAll();
        List<FUserVirtualAddressWithdrawDTO> withdrawAddress = userCapitalAccountService.listCoinAddressWithdraw(getCurrentUserInfoByApiToken().getFid(), symbol);
        for (FUserVirtualAddressWithdrawDTO withdrawDTO : withdrawAddress) {
            for (SystemCoinType coin : systemCoinTypes) {
                if (coin.getId().equals(withdrawDTO.getFcoinid())) {
                    withdrawDTO.setCoinName(coin.getShortName());
                }
            }
        }
        SystemCoinType coinType = null;
        // 币种查找
        if (symbol != null) {
            coinType = redisHelper.getCoinType(symbol);
        }   
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("withdrawAddress", withdrawAddress);
        jsonObject.put("appLogo",coinType == null ? "" : coinType.getAppLogo());
        return ReturnResult.SUCCESS(jsonObject);
    }   

    /**
     * 虚拟币提现
     */
    @ResponseBody
    @RequestMapping(value = "/v1/coin/withdraw",method = RequestMethod.POST)
    public ReturnResult withdrawBtcSubmit(
            @RequestParam Integer withdrawAddr,
            @RequestParam BigDecimal withdrawAmount,
            @RequestParam String tradePwd,
            @RequestParam Integer symbol,
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") Integer btcfeesIndex,
            @RequestParam(required = false) String memo) {
        if (withdrawAddr == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1000"));
        }
        if (withdrawAmount == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1001"));
        }
        withdrawAmount = MathUtils.toScaleNum(withdrawAmount, MathUtils.ENTER_COIN_SCALE);
        if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1002"));
        }
        if (org.springframework.util.StringUtils.isEmpty(tradePwd)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10116"));
        }
        // 用户
        FUser user = getCurrentUserInfoByApiToken();
        if ("0".equals(phoneCode) && user.getFistelephonebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10114"));
        }
        if ("0".equals(totpCode) && user.getFgooglebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10115"));
        }
        // 币信息
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw() || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
        }
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        // BTC网络手续费
        BigDecimal BTCFees = coinType.getNetworkFee();
        if (coinType.getShortName().equals("BTC")) {
            if (btcfeesIndex <= 0 || btcfeesIndex >= Constant.BTC_FEES_MAX) {
                BTCFees = Constant.BTC_FEES[0];
            } else {
                BTCFees = Constant.BTC_FEES[btcfeesIndex];
            }
        }
        // 提现
        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (Exception e) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
        }
        if (coinType.getShortName().equals("BTK") && MathUtils.toScaleNum(withdrawAmount, MathUtils.INTEGER_SCALE).compareTo(withdrawAmount) != 0) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10119"));
        }
        Boolean risk = coinType.getRiskNum() != null && coinType.getRiskNum().compareTo(BigDecimal.ZERO) > 0 && coinType.getRiskNum().compareTo(withdrawAmount) < 0;
        CoinOperationOrderDTO order = new CoinOperationOrderDTO();
        order.setUserId(user.getFid());
        order.setCoinId(coinType.getId());
        order.setCoinName(coinType.getName());
        order.setOperationType(VirtualCapitalOperationTypeEnum.COIN_OUT);
        order.setDataSource(DataSourceEnum.WEB);
        order.setPlatform(PlatformEnum.BC);
        order.setAddressBindId(withdrawAddr);
        order.setAmount(withdrawAmount);
        order.setTradePass(tradePwd);
        order.setPhoneCode(phoneCode);
        order.setGoogleCode(totpCode);
        order.setIp(ip);
        order.setRisk(risk);
        order.setNetworkFees(BTCFees);
        order.setMemo(org.springframework.util.StringUtils.isEmpty(memo) ? null : memo);
         
        try {
            Result result = userCapitalService.createCoinOperationOrder(order);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(GetR18nMsg("capital.coin.withdraw.create." + result.getCode().toString()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("withdrawBtcSubmit {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.create." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("withdrawBtcSubmit error");
        }
        return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
    }


      /**
     * 虚拟币提现
     */
    @ResponseBody
    @RequestMapping(value = "/v1/coin/withdraw/address",method = RequestMethod.POST)
    public ReturnResult withdrawBtcSubmit2(
            @RequestParam String withdrawAddr,
            @RequestParam BigDecimal withdrawAmount,
            @RequestParam String tradePwd,
            @RequestParam Integer symbol,
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") Integer btcfeesIndex,
            @RequestParam(required = false) String memo) {
        if (org.springframework.util.StringUtils.isEmpty(withdrawAddr)) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1000"));
        }
        if (withdrawAmount == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1001"));
        }
        withdrawAmount = MathUtils.toScaleNum(withdrawAmount, MathUtils.ENTER_COIN_SCALE);
        if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.com.1002"));
        }
        if (org.springframework.util.StringUtils.isEmpty(tradePwd)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10116"));
        }
        // 用户
        FUser user = getCurrentUserInfoByApiToken();
        if ("0".equals(phoneCode) && user.getFistelephonebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10114"));
        }
        if ("0".equals(totpCode) && user.getFgooglebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10115"));
        }
        // 币信息
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw() || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
        }
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        // BTC网络手续费
        BigDecimal BTCFees = coinType.getNetworkFee();
        if (coinType.getShortName().equals("BTC")) {
            if (btcfeesIndex <= 0 || btcfeesIndex >= Constant.BTC_FEES_MAX) {
                BTCFees = Constant.BTC_FEES[0];
            } else {
                BTCFees = Constant.BTC_FEES[btcfeesIndex];
            }
        }
        // 提现
        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (Exception e) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
        }
        if (coinType.getShortName().equals("BTK") && MathUtils.toScaleNum(withdrawAmount, MathUtils.INTEGER_SCALE).compareTo(withdrawAmount) != 0) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10119"));
        }
        Boolean risk = coinType.getRiskNum() != null && coinType.getRiskNum().compareTo(BigDecimal.ZERO) > 0 && coinType.getRiskNum().compareTo(withdrawAmount) < 0;
        CoinOperationOrderDTO order = new CoinOperationOrderDTO();
        order.setUserId(user.getFid());
        order.setCoinId(coinType.getId());
        order.setCoinName(coinType.getName());
        order.setOperationType(VirtualCapitalOperationTypeEnum.COIN_OUT);
        order.setDataSource(DataSourceEnum.WEB);
        order.setPlatform(PlatformEnum.BC);
        //order.setAddressBindId(withdrawAddr);
        order.setAddress(withdrawAddr);
        order.setAmount(withdrawAmount);
        order.setTradePass(tradePwd);
        order.setPhoneCode(phoneCode);
        order.setGoogleCode(totpCode);
        order.setIp(ip);
        order.setRisk(risk);
        order.setNetworkFees(BTCFees);
        order.setMemo(org.springframework.util.StringUtils.isEmpty(memo) ? null : memo);
         
        try {
            Result result = userCapitalService.createCoinOperationOrder(order);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS(GetR18nMsg("capital.coin.withdraw.create." + result.getCode().toString()));
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("withdrawBtcSubmit {}", result.getMsg());
            } else if (result.getCode() < 10000) {
                return ReturnResult.FAILUER(GetR18nMsg("capital.coin.withdraw.create." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(GetR18nMsg("com.error." + result.getCode().toString(), result.getData().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("withdrawBtcSubmit error");
        }
        return ReturnResult.FAILUER(GetR18nMsg("com.error.10117"));
    }

    /**
     * 登录日志
     */
    @ResponseBody
    @RequestMapping(value = "/v1/log/login", method = RequestMethod.GET)
    public ReturnResult userLoginlog(@RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
        int pagesize = 10;
        FUser userInfo = getCurrentUserInfoByApiToken();

        Pagination<FLogUserAction> flogs = new Pagination<FLogUserAction>(currentPage, pagesize, "/user/user_loginlog.html?");
        flogs = userSecurityService.listSettingLogByUser(userInfo.getFid(), flogs, 1);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flogs", flogs);
        jsonObject.put("pagin", flogs.getPagin());
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 安全设置日志
     */
    @ResponseBody
    @RequestMapping(value = "/v1/log/setting", method = RequestMethod.GET)
    public ReturnResult userSettinglog(@RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
        int pagesize = 10;
        FUser userInfo = getCurrentUserInfoByApiToken();

        Pagination<FLogUserAction> flogs = new Pagination<FLogUserAction>(currentPage, pagesize, "/user/user_settinglog.html?");
        flogs = userSecurityService.listSettingLogByUser(userInfo.getFid(), flogs, 2);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flogs", flogs);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 充值码兑换
     *
     * @param code
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/activity/exchange")
    public ReturnResult codeExchange(@RequestParam(required = true, defaultValue = "") String code) throws Exception {
        // 传送消息
        if (code == "" || code.length() != 16) {
            return ReturnResult.FAILUER("充值码不正确");
        }
        FUser fUser = getCurrentUserInfoByApiToken();
        fUser = userService.selectUserById(fUser.getFid());
        String ip = Utils.getIpAddr(sessionContextUtils.getContextRequest());
        Result flag = null;
        try {
            flag = rewardCodeService.UseRewardCode(fUser.getFid(), code, ip);
            if (flag.getSuccess()) {
                return ReturnResult.SUCCESS(flag.getMsg());
            } else {
                return ReturnResult.FAILUER(flag.getMsg());
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ReturnResult.FAILUER(GetR18nMsg("com.activity.error.10002"));
        }
    }

    /**
     * 使用的兑换码列表
     *
     * @param currentPage
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/activity/index")
    public ReturnResult activityGo(@RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
        int pagesize = 5;
        FUser fuser = super.getCurrentUserInfoByApiToken();
        fuser = userService.selectUserById(fuser.getFid());

        Pagination<FRewardCodeDTO> page = new Pagination<FRewardCodeDTO>(currentPage, pagesize, "/activity/index_json.html?");
        FRewardCodeDTO code = new FRewardCodeDTO();
        code.setFuid(fuser.getFid());
        code.setFstate(true);
        page = rewardCodeService.listRewardeCode(page, code);
        System.out.println(page);
        Collection<FRewardCodeDTO> list = page.getData();
        if (list != null) {
            for (FRewardCodeDTO fRewardCode : list) {
                SystemCoinType coinType = redisHelper.getCoinType(fRewardCode.getFtype());
                fRewardCode.setFtype_s(coinType.getShortName());
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pagin", page.getPagin());
        jsonObject.put("frewardcodes", list);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 用户信息
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/info")
    public ReturnResult userInfo() throws Exception {
        FUser currentUserInfoByApiToken = getCurrentUserInfoByApiToken();
        currentUserInfoByApiToken.setFtradepassword("");
        currentUserInfoByApiToken.setFloginpassword("");
        currentUserInfoByApiToken.setFgoogleauthenticator("");
        currentUserInfoByApiToken.setFgoogleurl("");
        return ReturnResult.SUCCESS(currentUserInfoByApiToken);
    }


    /**
     * 添加银行卡
     *
     * @param account        账号
     * @param phoneCode      手机验证码
     * @param totpCode       Google验证码
     * @param openBankType   银行卡类型ID
     * @param address        详细地址
     * @param prov           省
     * @param city           市
     * @param dist           区
     * @param payeeAddr      开户名
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/save_bankinfo")
    public ReturnResult updateOutAddress(
            @RequestParam(required = true) String account,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam(required = true) int openBankType,
            @RequestParam(required = true) String address,
            @RequestParam(required = true) String prov,
            @RequestParam(required = true) String city,
            @RequestParam(required = false) String dist,
            @RequestParam(required = true) String payeeAddr) throws Exception {
        try{
            String ip = getIpAddr();
            // 用户
            FUser fuser = super.getCurrentUserInfoByApiToken();

            int bankId  = 0;

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
     * 人民提现
     *
     * @param tradePwd          交易密码
     * @param withdrawBalance   提现金额
     * @param totpCode          Google验证码
     * @param phoneCode         手机验证码
     * @param withdrawBlank     提现银行卡ID
     * @param symbol            币种ID（这个主要是后台用来判断是不是人民币类型的）
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/v1/user/cny_manual")
    public ReturnResult withdrawBankSubmit(String tradePwd,
                                           BigDecimal withdrawBalance,
                                           @RequestParam(required = false, defaultValue = "0") String totpCode,
                                           @RequestParam(required = false, defaultValue = "0") String phoneCode,
                                           Integer withdrawBlank,
                                           Integer symbol) {
        if (withdrawBlank == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.bank.withdraw.com.1000"));
        }

        if (withdrawBalance == null) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.bank.withdraw.com.1001"));
        }

        withdrawBalance = MathUtils.toScaleNum(withdrawBalance, MathUtils.ENTER_CNY_SCALE);
        if (withdrawBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResult.FAILUER(GetR18nMsg("capital.bank.withdraw.com.1002"));
        }
        if (org.springframework.util.StringUtils.isEmpty(tradePwd)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10116"));
        }

        // 用户
        FUser fuser = getCurrentUserInfoByApiToken();
        if ("0".equals(phoneCode) && fuser.getFistelephonebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10114"));
        }
        if ("0".equals(totpCode) && fuser.getFgooglebind()) {
            return ReturnResult.FAILUER(GetR18nMsg("com.error.10115"));
        }

        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
            return ReturnResult.FAILUER(GetR18nMsg("financial.err.1002"));
        }

        try {
            tradePwd = Utils.MD5(tradePwd);
        } catch (BCException e) {
            e.printStackTrace();
        }

        BankOperationOrderDTO bankOperationOrder = new BankOperationOrderDTO();
        bankOperationOrder.setCoinId(coinType.getId());
        bankOperationOrder.setAmount(withdrawBalance);
        bankOperationOrder.setUserBankId(withdrawBlank);
        bankOperationOrder.setUserLevel(fuser.getLevel());
        bankOperationOrder.setUserId(fuser.getFid());
        bankOperationOrder.setOperationInOutType(CapitalOperationInOutTypeEnum.OUT);
        bankOperationOrder.setOperationType(CapitalOperationTypeEnum.RMB_OUT);
        bankOperationOrder.setPlatform(PlatformEnum.BC);
        bankOperationOrder.setIp(getIpAddr());
        bankOperationOrder.setDataSource(DataSourceEnum.WEB);
        bankOperationOrder.setCoinName(coinType.getName());
        bankOperationOrder.setRisk(true);
        bankOperationOrder.setPhone(fuser.getFtelephone());
        bankOperationOrder.setPhoneCode(phoneCode);
        bankOperationOrder.setPassword(tradePwd);
        bankOperationOrder.setGoogleCode(totpCode);
        try {
            Result result = userCapitalService.createBankOperationOrder(bankOperationOrder);
            if (result.getSuccess()) {
                return ReturnResult.SUCCESS("提交成功");
            } else {
                return ReturnResult.FAILUER(result.getCode() + result.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ReturnResult.FAILUER("");
    }

    /**
     * 绑定的银行卡列表
     *
     * @param symbol        币种id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/v1/user/bankinfo")
    public ReturnResult withdrawBankInfo(
            @RequestParam(required = false, defaultValue = "0") Integer symbol) throws Exception {
        // 币种
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
            coinType = redisHelper.getCoinTypeIsWithdrawFirst(SystemCoinTypeEnum.CNY.getCode());
            if (coinType == null) {
                return ReturnResult.FAILUER("");
            }
        }
        // 用户
        FUser fuser = getCurrentUserInfoByApiToken();
        fuser = userService.selectUserById(fuser.getFid());
        // 提现银行
        List<FUserBankinfoDTO> fbankinfoWithdraw = userCapitalAccountService.listBankInfo(fuser.getFid(), coinType.getId(), 0);
        for (FUserBankinfoDTO bankinfo : fbankinfoWithdraw) {
            FSystemBankinfoWithdraw item = redisHelper.getWithdrawBankById(bankinfo.getFbanktype());
            bankinfo.setFbanktype_s(item.getFcnname());
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bankinfo",fbankinfoWithdraw);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 人民币提现记录
     *
     * @param symbol        币种id
     * @param currentPage   当前页面
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/v1/user/cny_list")
    public ReturnResult withdrawBankInfo(
            @RequestParam(required = false, defaultValue = "0") Integer symbol,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
        // 币种
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())
                || !coinType.getType().equals(SystemCoinTypeEnum.CNY.getCode())) {
            coinType = redisHelper.getCoinTypeIsWithdrawFirst(SystemCoinTypeEnum.CNY.getCode());
            if (coinType == null) {
                return ReturnResult.FAILUER("");
            }
        }
        // 用户
        FUser fuser = getCurrentUserInfoByApiToken();
        fuser = userService.selectUserById(fuser.getFid());
        // 提现记录
        Pagination<FWalletCapitalOperationDTO> page = new Pagination<FWalletCapitalOperationDTO>(currentPage,
                Constant.CapitalWithdrawPerPage, "/withdraw/cny_withdraw.html?symbol=" + symbol + "&");
        FWalletCapitalOperationDTO operation = new FWalletCapitalOperationDTO();
        operation.setFuid(fuser.getFid());
        operation.setFinouttype(CapitalOperationInOutTypeEnum.OUT.getCode());
        page = userCapitalService.listWalletCapitalOperation(page, operation);
        // 钱包
        return ReturnResult.    SUCCESS(page.getData());
    }

    /**
     * 系统银行卡
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/v1/system/bankinfo")
    public ReturnResult systemBankInfo() throws Exception {
        List<FSystemBankinfoWithdraw> bankTypes = redisHelper.getWithdrawBankList();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bankinfo",bankTypes);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 提现手续费
     *
     * @param symbol
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/system/fee")
    public ReturnResult systemWithdrawFee(@RequestParam(required = false, defaultValue = "0") Integer symbol){
        // 币种
        SystemCoinType coinType = redisHelper.getCoinType(symbol);
        if (coinType == null || !coinType.getIsWithdraw()
                || coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode())){
            return ReturnResult.FAILUER("币种不支持提现，或者已经禁用");
        }
        // 用户
        FUser fuser = getCurrentUserInfoByApiToken();
        fuser = userService.selectUserById(fuser.getFid());
        SystemCoinSetting withdrawSetting = redisHelper.getCoinSetting(coinType.getId(), fuser.getLevel());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("withdrawSetting", withdrawSetting);
        return ReturnResult.SUCCESS(jsonObject);
    }

    /**
     * 充值客服 QQ
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/v1/user/qq")
    public ReturnResult getQQ(){
        String qq_customer = redisHelper.getSystemArgs("QQ_CUSTOMER");
        if (TextUtils.isEmpty(qq_customer)){
            return ReturnResult.SUCCESS(new ArrayList<>());
        }
        String[] split = qq_customer.split(",");
        if (split == null || split.length == 0){
            return ReturnResult.SUCCESS(new ArrayList<>());
        }
        return ReturnResult.SUCCESS( new JSONArray( Arrays.asList(split)));
    }

    /**
     * 操作记录
     */
    @ResponseBody
    @RequestMapping("/v1/user/operation")
    public ReturnResult operationRecord(
            @RequestParam(required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(required = false) Integer coinId,
            @RequestParam(required = false) Integer operation) {
        FUser fuser = getCurrentUserInfoByApiToken();
        if (fuser == null){
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,"");
        }

        Pagination<FinanceRecordDTO> pagination = new Pagination<>(currentPage,Constant.webPageSize,"/index.php?s=exc&c=financeController&m=record");
        pagination.setOrderField("fid");
        pagination.setOrderDirection("desc");
        pagination = userCapitalService.listFinanceRecord(pagination,fuser.getFid(),coinId,operation);
        return ReturnResult.SUCCESS(pagination.getData());
    }

    /**
     * 通过手机找回密码-重置密码
     */
    @ResponseBody
    @RequestMapping("/v1/user/resetpwd")
    public ReturnResult resetPasswordByPhone(
            @RequestParam(required = false, defaultValue = "0") String phone,
            @RequestParam(required = false, defaultValue = "0") String area,
            @RequestParam(required = false, defaultValue = "0") String code,
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam String newPassword,
            @RequestParam String newPassword2) throws Exception {

        HttpServletRequest request = sessionContextUtils.getContextRequest();
        // 检测密码强度
        if (!newPassword.matches(Constant.passwordReg) || newPassword.length() < 6) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11009"));
        }

        if (!newPassword.equals(newPassword2)) {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11010"));
        }

        FUser param = new FUser();
        param.setFtelephone(phone);
        FUser fuser = this.userService.selectUserByParam(param);

        //验证码校验
        if (fuser.getFistelephonebind()) {
            //验证手机验证码
            Result phoneCodeCheckResult = validationCheckHelper.getPhoneCodeCheck(area, phone, code, BusinessTypeEnum.SMS_FIND_PHONE_PASSWORD.getCode(),
                    Utils.getIpAddr(sessionContextUtils.getContextRequest()), PlatformEnum.BC.getCode());
            if (!phoneCodeCheckResult.getSuccess()) {
                return ReturnResult.FAILUER(phoneCodeCheckResult.getMsg());
            }
        } else {
            return ReturnResult.FAILUER(GetR18nMsg("com.validate.error.11012"));
        }

        String ip = Utils.getIpAddr(request);

        ValidateParamInfo validateParamInfo = new ValidateParamInfo();
        validateParamInfo.setBusinessType(BusinessTypeEnum.SMS_FIND_PHONE_PASSWORD);
        validateParamInfo.setPlatform(PlatformEnum.BC);
        validateParamInfo.setIp(ip);
        validateParamInfo.setTotpCode(totpCode);

        fuser.setFloginpassword(Utils.MD5(newPassword));
        fuser.setIp(ip);
        fuser.setFtelephone(phone);
        try {
            Result result = userSecurityService.restLoginPasword(fuser, validateParamInfo, 0);
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
     * 通过手机找回密码-重置密码
     */
    @ResponseBody
    @RequestMapping("/v1/user/findtradepwd")
    public ReturnResult resetTradePwdByPhone(
            @RequestParam(required = false, defaultValue = "0") String phone,
            @RequestParam(required = false, defaultValue = "0") String area,
            @RequestParam(required = false, defaultValue = "0") String code,
            @RequestParam(required = false, defaultValue = "0") String totpCode,
            @RequestParam String newPassword,
            @RequestParam String newPassword2) throws Exception {

        FUser currentUserInfoByApiToken = getCurrentUserInfoByApiToken();
        if (currentUserInfoByApiToken == null){
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
            return ReturnResult.FAILUER("与绑定的手机号码不一致");
        }

        FUser fuser = this.userService.selectUserById(currentUserInfoByApiToken.getFid());
        if (TextUtils.isEmpty(fuser.getFtradepassword())){
            return ReturnResult.FAILUER("还没有设置交易密码不能找回");
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







}
