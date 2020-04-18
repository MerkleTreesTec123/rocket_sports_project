package com.qkwl.service.user.impl;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.Enum.IdentityStatusEnum;
import com.qkwl.common.dto.Enum.LimitTypeEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.Enum.UserLoginType;
import com.qkwl.common.dto.Enum.UserStatusEnum;
import com.qkwl.common.dto.activity.FActivityRecord;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.log.FLogUserScore;
import com.qkwl.common.dto.user.FBeautiful;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserIdentity;
import com.qkwl.common.dto.user.FUserInfo;
import com.qkwl.common.dto.user.FUserPriceclock;
import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.dto.user.LoginResponse;
import com.qkwl.common.dto.user.RequestUserInfo;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.limit.LimitHelper;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.GUIDUtils;
import com.qkwl.common.util.Ip2Long;
import com.qkwl.common.util.Utils;
import com.qkwl.service.user.dao.FActivityRecordMapper;
import com.qkwl.service.user.dao.FBeautifulMapper;
import com.qkwl.service.user.dao.FLogUserActionMapper;
import com.qkwl.service.user.dao.FLogUserScoreMapper;
import com.qkwl.service.user.dao.FUserIdentityMapper;
import com.qkwl.service.user.dao.FUserInfoMapper;
import com.qkwl.service.user.dao.FUserMapper;
import com.qkwl.service.user.dao.FUserPriceclockMapper;
import com.qkwl.service.user.dao.FUserScoreMapper;
import com.qkwl.service.user.dao.UserCoinWalletMapper;
import com.qkwl.service.user.utils.MQSend;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用户业务实现
 *
 */
@Service("userService")
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private FUserMapper userMapper;
    @Autowired
    private FUserScoreMapper userScoreMapper;
    @Autowired
    private FLogUserActionMapper logUserActionMapper;
    @Autowired
    private FLogUserScoreMapper logUserScoreMapper;
    @Autowired
    private FBeautifulMapper beautifulMapper;
    @Autowired
    private FUserPriceclockMapper userPriceclockMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private MemCache memCache;
    @Autowired
    private MQSend mqSend;
    @Autowired
    private ScoreHelper scoreHelper;
    @Autowired
    private FUserInfoMapper userInfoMapper;
    @Autowired
    private FActivityRecordMapper activityRecordMapper;
    @Autowired
    private UserCoinWalletMapper coinWalletMapper;
    @Autowired
    private FUserIdentityMapper userIdentityMapper;
    @Autowired
    private ValidateHelper validateHelper;
    @Autowired
    private LimitHelper limitHelper;
    @Autowired
    private ValidationCheckHelper validationCheckHelper;
    /**
     * 用户登录
     *
     * @param userinfo 请求用户信息的参数实体
     * @param type     登录类型
     * @param ip       登录IP地址
     * @return 登录后响应的用户信息实体对象
     * 200  登录成功
     * 300  登录失败
     * 400  参数错误
     * 1000 用户名或密码不能为空
     * 1001 用户不存在
     * 1002 用户名或密码错误
     * 1003 用户积分等级不存在
     * 1004 系统升级中，暂停登录
     * 1005 账户出现安全隐患被冻结，请尽快联系客服
     * 1006 ip限制中，请稍候登录
     * @throws Exception
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result updateCheckLogin(RequestUserInfo userinfo, UserLoginType type, String ip, LocaleEnum lan) throws Exception {
        try {
            if (userinfo == null || type == null || StringUtils.isEmpty(ip)) {
                return Result.param("请求参数错误！");
            }
            Boolean isLimit = limitHelper.checkLimit(ip, LimitTypeEnum.LoginPassword.getCode());
            if (!isLimit) {
                return Result.failure(1006, "ip限制中，请稍候登录");
            }
            // 登录用户数据组装
            FUser user = new FUser();
            if (StringUtils.isEmpty(userinfo.getFloginname()) || StringUtils.isEmpty(userinfo.getFloginpassword())) {
                return Result.failure(1000, "登录名或密码不能为空");
            } else {
                if (userinfo.getType() == 0) {
                    user.setFtelephone(userinfo.getFloginname());
                } else {
                    user.setFemail(userinfo.getFloginname());
                }
                List<FUser> list = userMapper.getUserListByParam(user);
                if (list == null || list.size() <= 0) {
                    return Result.failure(1001, "用户不存在");
                }
                user.setFloginpassword(userinfo.getFloginpassword());
            }

            user.setFqqopenid(userinfo.getFqqopenid());
            user.setFunionid(userinfo.getFunionid());
            user.setFagentid(userinfo.getFagentid());

            // 查询用户
            List<FUser> userList = userMapper.getUserListByParam(user);
            if (userList != null && userList.size() > 0) {
                user = userList.get(0);
            } else {
                Integer limit = limitHelper.updateLimit(ip, LimitTypeEnum.LoginPassword.getCode());
                return Result.failure(1002, "用户名或密码错误", limit);
            }

            LoginResponse result = new LoginResponse();
            // 限制登录
            String isCanlogin = redisHelper.getSystemArgs(ArgsConstant.ISCANLOGIN);
            if (isCanlogin != null && !isCanlogin.trim().equals("1")) {
                return Result.failure(1004, "系统升级中，暂停登录");
            }
            // 验证帐号状态
            if (user.getFstatus() == UserStatusEnum.FORBBIN_VALUE) {
                return Result.failure(1005, "账户出现安全隐患被冻结，请尽快联系客服");
            }

            // 登录类型判断
            if (type == UserLoginType.APPUser || type == UserLoginType.APPQQ || type == UserLoginType.APPWX) {
                result = login(user, false);
            } else if ((type == UserLoginType.WebUser || type == UserLoginType.WebQQ || type == UserLoginType.WebWX)) {
                result = login(user, true);
            } else {
                return Result.param("登录类型参数错误！");
            }

            FUser fuser = result.getUserinfo();

            // 删除过往交易密码缓存
            String tpStr = RedisConstant.TRADE_NEED_PASSWORD + MD5Util.md5(String.valueOf(fuser.getFid()));
            memCache.delete(tpStr);

            // 是否更新积分
            int isTodayFirstLogin = 0;
            String last = DateUtils.format(fuser.getFlastlogintime(), "yyyy-MM-dd");
            String now = DateUtils.format(new Date(), "yyyy-MM-dd");
            if (last.equals(now)) {
                isTodayFirstLogin = 1;
            }

            Date date = new Date();
            String lastIp = fuser.getFlastip();
            fuser.setFlastlogintime(date);
            fuser.setFlastip(Ip2Long.ip2Long(ip));
            // 更新登录时间
            userMapper.updateLoginTime(fuser);
            // 首次登录
            if (isTodayFirstLogin == 0) {
                scoreHelper.SendUserScore(fuser.getFid(), BigDecimal.ZERO, ScoreTypeEnum.LOGIN.getCode(), "登录");
            }
            // 返回数据
            result.getUserinfo().setIp(ip);
            // MQ_USER_ACTION
            if (fuser != null) {
                mqSend.SendUserAction(fuser.getFagentid(), fuser.getFid(), LogUserActionEnum.LOGIN, ip, type.getCode(), String.valueOf(type.getValue()));
            }
            // 登录提醒邮件
            if (lastIp != null && !Ip2Long.long2ip(lastIp).equals(ip)) {
                if (user.getFismailbind()) {
                    validateHelper.mailSendContent(user.getFemail(), userinfo.getPlatform(),
                            lan, BusinessTypeEnum.EMAIL_NEW_IP_LOGIN, ip, user);
                }
            }
            // 清空ip登陆错误次数
            limitHelper.deleteLimit(ip, LimitTypeEnum.LoginPassword.getCode());
            return Result.success("登录成功！", result);
        } catch (Exception e) {
            logger.error("用户登录服务异常:", e);
            throw e;
        }
    }

    /**
     * 处理多种登录
     *
     * @param user
     * @param isWebLogin
     * @return
     */
    private LoginResponse login(FUser user, Boolean isWebLogin) {
        LoginResponse result = new LoginResponse();

        // 组装redis
        String md5AccountId = MD5Util.md5(String.valueOf(user.getFid()));
        String accountTokenInfo = "";
        if (isWebLogin) {
            accountTokenInfo = RedisConstant.ACCOUNT_LOGIN_TOTAL_KEY + md5AccountId + "_";
        } else {
            accountTokenInfo = RedisConstant.APP_LOGIN_TOTAL_KEY + md5AccountId + "_";
        }
        String token = accountTokenInfo + GUIDUtils.getGUIDString();
        RedisObject obj = new RedisObject();
        obj.setExtObject(user);

        // 缓存用户数据
        memCache.removeByPattern(accountTokenInfo);
        memCache.set(token, JSON.toJSONString(obj), Constant.EXPIRETIME);

        // 签名
        if (!isWebLogin) {
            // 组装签名redis
            String md5Account = MD5Util.md5(MD5Util.md5(token) + md5AccountId);
            String accountKeyInfo = RedisConstant.ACCOUNT_SIGN__KEY + md5AccountId + "_";
            String keyname = accountKeyInfo + md5Account;
            String secretKey = GUIDUtils.getGUIDString();
            RedisObject objkey = new RedisObject();
            objkey.setExtObject(secretKey);

            // 缓存签名数据
            memCache.removeByPattern(accountKeyInfo);
            memCache.set(keyname, JSON.toJSONString(objkey), Constant.EXPIRETIME);

            // 下发签名
            result.setSecretKey(secretKey);
        }

        // 删除过往交易密码缓存
        String tpStr = RedisConstant.TRADE_NEED_PASSWORD + MD5Util.md5(String.valueOf(user.getFid()));
        memCache.delete(tpStr);

        // 返回登录数据
        result.setToken(token);
        result.setUserinfo(user);
        return result;
    }


    /**
     * 用户注册
     *
     * @param fuser 用户实体对象
     * @param ip    注册IP地址
     * @return 登录后响应的用户信息实体对象
     * 200 注册成功
     * 300 注册失败
     * 400 参数异常
     * 1000 用户名或密码不能为空
     * 1001 手机号格式错误
     * 1002 手机号已存在
     * 1003 邮件格式错误
     * 1004 邮箱已存在
     * 1005 第三方账号已存在
     * 1006 推荐人不存在
     * 10004 手机验证码错误
     * 10005 手机验证码错误达到上限，请两小时后重试!
     * 10014 邮箱验证码错误
     * 10015 邮箱验证码错误达到上限，请两小时后重试!
     * @throws Exception
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result insertRegister(RequestUserInfo userinfo, UserLoginType type) throws Exception {
        try {
            if (userinfo == null || type == null || StringUtils.isEmpty(userinfo.getIp())) {
                return Result.param("请求参数错误！");
            }

            if (StringUtils.isEmpty(userinfo.getFloginname()) || StringUtils.isEmpty(userinfo.getFloginpassword())) {
                return Result.failure(1000, "用户名或密码不能为空");
            }
            String ipLimitCount = redisHelper.getSystemArgs(ArgsConstant.IPLIMTCOUNT);
            if (!StringUtils.isNumeric(ipLimitCount)) {
                return Result.param("请求参数错误！");
            }
            if (userMapper.selectIpUserCount(Ip2Long.ip2Long(userinfo.getIp())) >= Integer.valueOf(ipLimitCount)) {
                System.out.println("--->IP 频繁注册：" + userinfo.getIp() + "||" + userinfo.getFloginname());
                return Result.failure(1007, "当前IP注册频繁！");
            }

            // 业务类型判断
            Integer businessType = 0;
            FUser user = new FUser();
            List<FUser> list = new ArrayList<>();
            if (userinfo.getType() == 0) {

                if (!StringUtils.isNumeric(userinfo.getFareacode())) {
                    return Result.param("areacode is wrong");
                }

                if (type == UserLoginType.APPUser || type == UserLoginType.APPQQ || type == UserLoginType.APPWX) {
                    businessType = BusinessTypeEnum.SMS_APP_REGISTER.getCode();
                } else if ((type == UserLoginType.WebUser || type == UserLoginType.WebQQ || type == UserLoginType.WebWX)) {
                    businessType = BusinessTypeEnum.SMS_WEB_REGISTER.getCode();
                } else {
                    return Result.param("登录类型参数错误！");
                }

                // 正则判断手机格式
                if ("86".equals(userinfo.getFareacode()) && !userinfo.getFloginname().matches(Constant.PhoneReg)) {
                    return Result.failure(1001, "手机号格式错误！");
                }
                user.setFtelephone(userinfo.getFloginname());
                list = userMapper.getUserListByParam(user);
                if (list != null && list.size() > 0) {
                    return Result.failure(1002, "手机号已存在！");
                }

                //if (!"1020201024".equals(userinfo.getCode())) {
                    // 验证码
                    Result result = validationCheckHelper.getPhoneCodeCheck(userinfo.getFareacode(), userinfo.getFloginname(),
                            userinfo.getCode(), businessType, userinfo.getIp(), userinfo.getPlatform().getCode());
                    if (result.getCode() > 200) {
                        return result;
                    }
                //}

            } else {
                businessType = BusinessTypeEnum.EMAIL_REGISTER_CODE.getCode();

                // 正则判断邮箱地址
                if (!userinfo.getFloginname().matches(Constant.EmailReg)) {
                    return Result.failure(1003, "邮件格式错误！");
                }
                user.setFemail(userinfo.getFloginname());
                list = userMapper.getUserListByParam(user);
                if (list != null && list.size() > 0) {
                    return Result.failure(1004, "邮箱已存在！");
                }

                // 验证码
                Result result = validationCheckHelper.getEmailCodeCheck(userinfo.getFloginname(),
                        userinfo.getCode(), businessType, userinfo.getIp(), userinfo.getPlatform().getCode());
                if (result.getCode() > 200) {
                    return result;
                }
            }

            user = new FUser();
            list.clear();
            if (!StringUtils.isEmpty(userinfo.getFqqopenid())) {
                user.setFqqopenid(userinfo.getFqqopenid());
                list = userMapper.getUserListByParam(user);
                if (list != null && list.size() > 0) {
                    return Result.failure(1005, "第三方账号已存在！");
                }
            } else if (!StringUtils.isEmpty(userinfo.getFunionid())) {
                user.setFunionid(userinfo.getFunionid());
                list = userMapper.getUserListByParam(user);
                if (list != null && list.size() > 0) {
                    return Result.failure(1005, "第三方账号已存在！");
                }
            }


            if (userinfo.getIntroUid() != null) {
                FUser intro = userMapper.selectByShowId(userinfo.getIntroUid());
                if (intro == null) {
                    return Result.failure(1006, "推荐人不存在！");
                }
            } else {
                if (redisHelper.getSystemArgs(ArgsConstant.ISMUSTINTROL).equals("1")) {
                    return Result.failure(1006, "推荐人不存在！");
                }
            }

            if (userinfo.getType() == 0) {
                user.setFtelephone(userinfo.getFloginname());
                user.setFistelephonebind(true);
                user.setFismailbind(false);
            } else if (userinfo.getType() == 1) {
                user.setFemail(userinfo.getFloginname());
                user.setFismailbind(true);
                user.setFistelephonebind(false);
            } else {
                return Result.param("regType is wrong");
            }
            user.setFintrouid(userinfo.getIntroUid());
            user.setFtradepassword(null);
            user.setFregistertime(Utils.getTimestamp());
            user.setFhasrealvalidate(false);
            user.setFidentitytype(0);
            user.setFstatus(UserStatusEnum.NORMAL_VALUE);
            user.setFiscny(UserStatusEnum.NORMAL_VALUE);
            user.setFiscoin(UserStatusEnum.NORMAL_VALUE);
            user.setFlastlogintime(Utils.getTimestamp());
            user.setFupdatetime(Utils.getTimestamp());
            user.setFgooglebind(false);
            user.setVersion(0);
            user.setFinvalidateintrocount(0);
            user.setFleverlock(0);
            user.setFlastip(Ip2Long.ip2Long(userinfo.getIp()));
            user.setFagentid(userinfo.getFagentid());
            user.setFplatform(userinfo.getPlatform().getCode());
            user.setFloginpassword(userinfo.getFloginpassword());
            user.setFloginname(userinfo.getFloginname());
            user.setFnickname(userinfo.getFloginname());
            user.setFareacode(userinfo.getFareacode());
            user.setFtradepassword(userinfo.getPassword());
                    
            if (userMapper.insert(user) <= 0) {
                throw new BCException("注册用户超时！");
            }
            // 靓号查询
            FBeautiful beautiful = beautifulMapper.selectByBid(user.getFid());
            if (beautiful != null) {
                if (userMapper.updateFshowidByFid(beautiful.getFuid(), user.getFid()) <= 0) {
                    throw new BCException("显示ID更新失败！");
                }
                user.setFshowid(beautiful.getFuid());
            } else {
                if (userMapper.updateFshowidByFid(user.getFid(), user.getFid()) <= 0) {
                    throw new BCException("显示ID更新失败！");
                }
                user.setFshowid(user.getFid());
            }
            // 初始化钱包
            List<SystemCoinType> fvirtualcointypes = redisHelper.getCoinTypeListSystem();
            for (SystemCoinType fvirtualcointype : fvirtualcointypes) {
                UserCoinWallet coinWallet = new UserCoinWallet();
                coinWallet.setTotal(BigDecimal.ZERO);
                coinWallet.setFrozen(BigDecimal.ZERO);
                coinWallet.setBorrow(BigDecimal.ZERO);
                coinWallet.setIco(BigDecimal.ZERO);
                coinWallet.setCoinId(fvirtualcointype.getId());
                coinWallet.setUid(user.getFid());
                coinWallet.setGmtCreate(Utils.getTimestamp());
                coinWallet.setGmtModified(Utils.getTimestamp());
                if (coinWalletMapper.insert(coinWallet) <= 0) {
                    throw new BCException("注册用户虚拟钱包超时！");
                }
            }
            // 等级
            FUserScore fscore = new FUserScore();
            fscore.setFlevel(0);
            fscore.setFscore(0L);
            fscore.setFtradingqty(0);
            fscore.setVersion(0);
            fscore.setFuid(user.getFid());
            if (userScoreMapper.insert(fscore) <= 0) {
                throw new BCException("注册用户等级超时！");
            }

            // 注册成功，自动登录
            LoginResponse resultlr = new LoginResponse();
            // 登录类型判断
            if (type == UserLoginType.APPUser || type == UserLoginType.APPQQ || type == UserLoginType.APPWX) {
                resultlr = login(user, false);
            } else if ((type == UserLoginType.WebUser || type == UserLoginType.WebQQ || type == UserLoginType.WebWX)) {
                resultlr = login(user, true);
            } else {
                return Result.param("登录类型参数错误！");
            }

            // 增加积分-手机注册
            if (user.getFistelephonebind() != null && user.getFistelephonebind()) {
                scoreHelper.SendUserScore(user.getFid(), BigDecimal.ZERO, ScoreTypeEnum.PHONE.getCode(), "手机认证");
                mqSend.SendUserAction(user.getFagentid(), user.getFid(), LogUserActionEnum.BIND_PHONE, userinfo.getIp());
            }
            // 增加积分-邮件注册
            if (user.getFismailbind() != null && user.getFismailbind()) {
                scoreHelper.SendUserScore(user.getFid(), BigDecimal.ZERO, ScoreTypeEnum.EMAIL.getCode(), "邮箱认证");
                mqSend.SendUserAction(user.getFagentid(), user.getFid(), LogUserActionEnum.BIND_MAIL, userinfo.getIp());
            }
            // 增加登录积分
            scoreHelper.SendUserScore(user.getFid(), BigDecimal.ZERO, ScoreTypeEnum.LOGIN.getCode(), "登录");
            // MQ_USER_ACTION
            mqSend.SendUserAction(user.getFagentid(), user.getFid(), LogUserActionEnum.REGISTER, userinfo.getIp());
            mqSend.SendUserAction(user.getFagentid(), user.getFid(), LogUserActionEnum.LOGIN, userinfo.getIp());

            return Result.success("注册成功！", resultlr);
        } catch (BCException e) {
            logger.error("用户注册服务异常:", e);
            throw e;
        }

    }

    /**
     * 根据条件校验用户是否存在(登录名、登录密码、邮箱、手机号、身份证、微信UnionId、QQOpenid)
     *
     * @param user
     * @return
     * @see com.qkwl.common.rpc.user.IUserService#selectIsExistByParam(com.qkwl.common.dto.user.FUser)
     */
    @Override
    public boolean selectIsExistByParam(FUser user) {
        List<FUser> list = userMapper.getUserListByParam(user);
        if (list != null) {
            return list.size() > 0 ? true : false;
        }
        return false;
    }

    /**
     * 修改密码
     *
     * @param fuser  用户实体对象
     * @param type   修改密码类型 0登录密码 1交易密码
     * @param newPwd 新密码
     * @return 0(成功) 1(异常) -1(登录密码与交易密码不能一样) -2( 交易密码与登录密码不能一样 )
     * @see com.qkwl.common.rpc.user.IUserService#updatePwd(com.qkwl.common.dto.user.FUser, int,
     * java.lang.String)
     */
    @Override
    public int updatePwd(FUser fuser, int type, String newPwd) throws BCException {
        LogUserActionEnum actionEnum = null;
        if (type == 0) {
            // 修改登录密码
            if (!StringUtils.isEmpty(fuser.getFtradepassword()) && fuser.getFtradepassword().equals(Utils.MD5(newPwd))) {
                return -1;
            }
            if (StringUtils.isEmpty(fuser.getFloginpassword())) {
                actionEnum = LogUserActionEnum.BIND_LOGINPWD;
            } else {
                actionEnum = LogUserActionEnum.MODIFY_LOGINPWD;
            }
            fuser.setFloginpassword(Utils.MD5(newPwd));
        } else {
            // 修改交易密码
            if (!StringUtils.isEmpty(fuser.getFloginpassword()) && fuser.getFloginpassword().equals(Utils.MD5(newPwd))) {
                return -2;
            }
            if (StringUtils.isEmpty(fuser.getFtradepassword())) {
                actionEnum = LogUserActionEnum.BIND_TRADEPWD;
            } else {
                actionEnum = LogUserActionEnum.MODIFY_TRADEPWD;
                fuser.setFtradepwdtime(new Date());
            }
            fuser.setFtradepassword(Utils.MD5(newPwd));
        }
        int result = userMapper.updateByPrimaryKey(fuser);
        if (result <= 0) {
            return 1;
        }
        // MQ_USER_ACTION
        mqSend.SendUserAction(fuser.getFagentid(), fuser.getFid(), actionEnum, fuser.getIp());
        return 0;
    }

    /**
     * 根据id更新用户
     *
     * @param fuser     用户实体对象
     * @param action    用户行为类型
     * @param scoreType 积分类型
     * @return true：成功，false：失败
     * @throws BCException
     * @see com.qkwl.common.rpc.user.IUserService#updateUserById(com.qkwl.common.dto.user.FUser,
     * com.qkwl.common.dto.Enum.LogUserActionEnum, com.qkwl.common.dto.Enum.ScoreTypeEnum)
     */
    @Override
    public boolean updateUserById(FUser fuser, LogUserActionEnum action, ScoreTypeEnum scoreType) throws BCException {
        if (!StringUtils.isNumeric(fuser.getFareacode())) {
            return false;
        }
        int result = userMapper.updateByPrimaryKey(fuser);
        if (result <= 0) {
            return false;
        }
        if (action != null) {
            // MQ_USER_ACTION
            mqSend.SendUserAction(fuser.getFagentid(), fuser.getFid(), action, fuser.getIp());
        }
        if (scoreType != null) {
            //增加积分
            scoreHelper.SendUserScore(fuser.getFid(), BigDecimal.ZERO, scoreType.getCode(), scoreType.getValue().toString());
        }
        return true;
    }

    /**
     * 根据条件查找用户(登录名、登录密码、邮箱、手机号、身份证、微信UnionId、QQOpenid)
     *
     * @param user 用户实体对象
     * @return 用户实体对象
     * @see com.qkwl.common.rpc.user.IUserService#selectUserByParam(com.qkwl.common.dto.user.FUser)
     */
    @Override
    public FUser selectUserByParam(FUser user) {
        List<FUser> list = userMapper.getUserListByParam(user);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据条件查找用户列表
     *
     * @param user 用户实体对象
     * @return 用户实体对象列表
     * @see com.qkwl.common.rpc.user.IUserService#selectUserListByParam(com.qkwl.common.dto.user.FUser)
     */
    @Override
    public List<FUser> selectUserListByParam(FUser user) {
        return userMapper.getUserListByParam(user);
    }

    /**
     * 查询推广用户的个数
     *
     * @param fuid 用户ID
     * @return 推广人数
     * @see com.qkwl.common.rpc.user.IUserService#selectIntroUserCount(java.lang.Integer)
     */
    @Override
    public Integer selectIntroUserCount(Integer fuid) {
        if (fuid == null || fuid <= 0) {
            return 0;
        }
        return userMapper.selectIntroUserCount(fuid);
    }

    /**
     * 更新登陆时间
     *
     * @param user 用户ID
     * @return true：成功，false：失败
     * @see com.qkwl.common.rpc.user.IUserService#updateLoginTime(com.qkwl.common.dto.user.FUser)
     */
    @Override
    public boolean updateLoginTime(FUser user) {
        int i = userMapper.updateLoginTime(user);
        return i > 0 ? true : false;
    }

    /**
     * 更新用户爆仓状态
     *
     * @param user 用户实体对象
     * @return true：成功，false：失败
     * @see com.qkwl.common.rpc.user.IUserService#updateLeverLock(com.qkwl.common.dto.user.FUser)
     */
    @Override
    public boolean updateLeverLock(FUser user) {
        int i = userMapper.updateLeverLock(user);
        return i > 0 ? true : false;
    }

    /**
     * 查询用户积分记录
     *
     * @param fuid 用户ID
     * @param page 分页实体对象
     * @return 分页实体对象
     * @see com.qkwl.common.rpc.user.IUserService#selectScoreListByUser(int,
     * java.lang.String, java.lang.String, com.qkwl.common.dto.common.Pagination)
     */
    @Override
    public Pagination<FLogUserAction> selectScoreListByUser(int fuid, String beginDate, String endDate, Pagination<FLogUserAction> page) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("beginDate", beginDate);
        map.put("endDate", endDate);
        map.put("fuid", fuid);
        map.put("ftype", LogUserActionEnum.SCORE.getCode());

        int count = logUserActionMapper.countListByUser(map);
        if (count > 0) {
            List<FLogUserAction> scoreList = logUserActionMapper.selectByType(map);
            page.setData(scoreList);
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }

    /**
     * 根据用户Id获取用户
     *
     * @param id 用户ID
     * @return 用户实体对象
     * @see com.qkwl.common.rpc.user.IUserService#selectUserById(int)
     */
    @Override
    public FUser selectUserById(int id) {
        FUser user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            return null;
        }
        // 查询会员等级
        FUserScore userScore = userScoreMapper.selectByUid(user.getFid());
        // 设置等级
        user.setLevel(userScore.getFlevel());
        user.setScore(userScore.getFscore());
        return user;
    }

    /**
     * 根据用户显示Id获取用户
     *
     * @param showId 用户显示ID
     * @return 用户实体对象
     * @see com.qkwl.common.rpc.user.IUserService#selectUserByShowId(int)
     */
    @Override
    public FUser selectUserByShowId(int showId) {
        FUser user = userMapper.selectByShowId(showId);
        if (user == null) {
            return null;
        }
        // 查询会员等级
        FUserScore userScore = userScoreMapper.selectByUid(user.getFid());
        if (userScore != null) {
            user.setLevel(userScore.getFlevel());
            user.setScore(userScore.getFscore());
        }
        return user;
    }

    /**
     * 根据用户参数查询用户集合
     *
     * @param fuser 用户实体对象
     * @return 用户实体对象列表
     * @see com.qkwl.common.rpc.user.IUserService#selectUserByUser(com.qkwl.common.dto.user.FUser)
     */
    @Override
    public List<FUser> selectUserByUser(FUser fuser) {
        return userMapper.getUserListByParam(fuser);
    }


    /**
     * 根据用户查询积分记录
     *
     * @param page  分页实体对象
     * @param score
     * @return 分页实体对象
     * @see com.qkwl.common.rpc.user.IUserService#selectUserScoreByPage(com.qkwl.common.dto.common.Pagination,
     * com.qkwl.common.dto.log.FLogUserScore)
     */
    @Override
    public Pagination<FLogUserScore> selectUserScoreByPage(Pagination<FLogUserScore> page, FLogUserScore score) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("fuid", score.getFuid());
        map.put("beginDate", page.getBegindate());
        map.put("endDate", page.getEnddate());

        int count = logUserScoreMapper.countByPage(map);
        if (count > 0) {
            List<FLogUserScore> logUserScoreList = logUserScoreMapper.selectByPage(map);
            for (FLogUserScore logscore : logUserScoreList) {
                logscore.setFtype_s(ScoreTypeEnum.getValueByCode(logscore.getFtype()));
            }
            page.setData(logUserScoreList);
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }


    /**
     * 根据用户ID查找等级积分
     *
     * @param fuid 用户ID
     * @return 用户积分实体对象
     * @see com.qkwl.common.rpc.user.IUserService#selectUserScoreById(int)
     */
    @Override
    public FUserScore selectUserScoreById(int fuid) {
        return userScoreMapper.selectByUid(fuid);
    }

    /**
     * 用户积分增加
     *
     * @param fuid       用户ID
     * @param fscore     增加积分
     * @param fscoretype 增加积分类型
     * @param famount    增加积分金额（没有为null）
     * @param fratio     积分增加比例（没有为null）
     * @param ip         操作IP地址
     * @return true：成功，false：失败
     * @throws BCException
     * @see com.qkwl.common.rpc.user.IUserService#updateUserScore(int, int, int,
     * java.math.BigDecimal, java.math.BigDecimal, java.lang.String)
     */
    @Override
    @Deprecated
    public boolean updateUserScore(int fuid, int fscore, int fscoretype, BigDecimal famount, BigDecimal fratio, String ip) throws BCException {
        if (fscore <= 0) {
            return false;
        }
        FUserScore fuserScore = userScoreMapper.selectByUid(fuid);
        if (fscoretype == ScoreTypeEnum.TRADING.getCode()) {
            if (fuserScore.getFtradingqty() + fscore > 10000) {
                fscore = 10000 - fuserScore.getFtradingqty();
            }
            fuserScore.setFtradingqty(fscore);
        }
        fuserScore.setFscore(fuserScore.getFscore() + fscore);
        Long fcurscore = fuserScore.getFscore();
        int fleve = 0;
        if (fcurscore >= 1000000) {
            fleve = 5;
        } else if (fcurscore >= 500000) {
            fleve = 4;
        } else if (fcurscore >= 300000) {
            fleve = 3;
        } else if (fcurscore >= 100000) {
            fleve = 2;
        } else if (fcurscore >= 10000) {
            fleve = 1;
        }
        if (fuserScore.getFlevel() < fleve) {
            fuserScore.setFlevel(fleve);
        }
        if (fscoretype == ScoreTypeEnum.ASSETLIMIT.getCode()) {
            fuserScore.setFtradingqty(0);
            if (fuserScore.getFleveltime() != null) {
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(new Date());
                rightNow.add(Calendar.YEAR, -1);// 日期减1年
                if (rightNow.getTime().after(fuserScore.getFleveltime())) {
                    fuserScore.setFleveltime(null);
                    fuserScore.setFlevel(5);
                }
            }
        }

        int result = userScoreMapper.updateByPrimaryKey(fuserScore);
        if (result <= 0) {
            return false;
        }
        FUser user = userMapper.selectByPrimaryKey(fuid);
        // MQ
        if (famount == null || fratio == null) {
            mqSend.SendUserAction(user.getFagentid(), fuid, LogUserActionEnum.SCORE, fscoretype, new BigDecimal(fscore), ip);
        } else {
            mqSend.SendUserAction(user.getFagentid(), fuid, LogUserActionEnum.SCORE, fscoretype, new BigDecimal(fscore), famount + "_" + fratio, ip);
        }
        return true;
    }

    /**
     * 更新价格闹钟
     *
     * @param price 闹钟实体
     * @return 是否保存成功
     * @see com.qkwl.common.rpc.user.IUserService#updateUserPriceClock(com.qkwl.common.dto.user.FUserPriceclock)
     */
    @Override
    public boolean updateUserPriceClock(FUserPriceclock price) {

        FUserPriceclock clock = userPriceclockMapper.selectByParam(price);

        if (clock != null) {
            //update
            clock.setFisopen(price.getFisopen());
            clock.setFupdatetime(new Date());
            if (price.getFisopen()) {
                clock.setFmaxprice(price.getFmaxprice());
                clock.setFminprice(price.getFminprice());
            } else {
                clock.setFmaxprice(clock.getFmaxprice());
                clock.setFminprice(clock.getFminprice());
            }

            int result = userPriceclockMapper.updateByPrimaryKey(clock);
            return result > 0 ? true : false;
        } else {
            //insert
            price.setFupdatetime(new Date());
            price.setVersion(0);
            int result = userPriceclockMapper.insert(price);
            return result > 0 ? true : false;
        }
    }

    /**
     * 新增用户联系地址
     *
     * @param userInfo 用户信息实体
     * @return true 成功，false 失败
     * @see com.qkwl.common.rpc.user.IUserService#insertUserInfo(com.qkwl.common.dto.user.FUserInfo)
     */
    @Override
    public Boolean insertUserInfo(FUserInfo userInfo) {
        int result = userInfoMapper.insert(userInfo);
        return result > 0;
    }

    /**
     * 修改用户联系地址
     *
     * @param userInfo 用户信息实体
     * @return true 成功，false 失败
     * @see com.qkwl.common.rpc.user.IUserService#updateUserInfo(com.qkwl.common.dto.user.FUserInfo)
     */
    @Override
    public Boolean updateUserInfo(FUserInfo userInfo) {
        int result = userInfoMapper.update(userInfo);
        return result > 0;
    }

    /**
     * 根据用户ID查询用户联系地址
     *
     * @param fuid 用户ID
     * @return 用户信息实体
     * @see com.qkwl.common.rpc.user.IUserService#selectUserInfo(java.lang.Integer)
     */
    @Override
    public FUserInfo selectUserInfo(Integer fuid) {
        List<FUserInfo> infos = userInfoMapper.selectByFuid(fuid);
        if (infos.size() > 0) {
            return infos.get(0);
        } else {
            return null;
        }
    }

    /**
     * 根据用户查询价格闹钟
     *
     * @param fuid 用户id
     * @return 闹钟列表
     * @see com.qkwl.common.rpc.user.IUserService#selectPriceClockByUser(java.lang.Integer)
     */
    @Override
    public List<FUserPriceclock> selectPriceClockByUser(Integer fuid) {
        return userPriceclockMapper.selectByUser(fuid);
    }

    /**
     * 根据用户id和币种查询闹钟
     *
     * @param price 闹钟实体
     * @return 查询实体
     * @see com.qkwl.common.rpc.user.IUserService#selectPriceClockByClock(com.qkwl.common.dto.user.FUserPriceclock)
     */
    @Override
    public FUserPriceclock selectPriceClockByClock(FUserPriceclock price) {
        return userPriceclockMapper.selectByParam(price);
    }

    @Override
    public Integer selectActivityIntroCount(Integer fuid) {
        return activityRecordMapper.selectByUserIntro(fuid);
    }

    @Override
    public Pagination<FActivityRecord> selectActivityRecord(Pagination<FActivityRecord> page, FActivityRecord record) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("fuid", record.getFuid());

        int count = activityRecordMapper.selectActivityRecordCount(map);
        if (count > 0) {
            List<FActivityRecord> list = activityRecordMapper.selectActivityRecordList(map);
            page.setData(list);
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer insertMigrationUser(FUser user) throws BCException {
        // 判断用户信息是否存在
        FUser migrationUser = userMapper.selectUserByOldUid(user.getFolduid());
        if (migrationUser != null) {
            return migrationUser.getFid();
        }
        // 设置登录名
        if (!StringUtils.isEmpty(user.getFtelephone())) {
            user.setFloginname(user.getFtelephone());
            user.setFnickname(user.getFtelephone());
        } else if (!StringUtils.isEmpty(user.getFemail())) {
            user.setFloginname(user.getFemail());
            user.setFnickname(user.getFemail().split("@")[0]);
        } else {
            user.setFloginname(user.getFolduid().toString());
            user.setFnickname(user.getFolduid().toString());
        }
        // 判断用户信息是否被使用
        FUser existUser = new FUser();
        existUser.setFtelephone(user.getFtelephone());
        existUser.setFemail(user.getFemail());
        int userCount = userMapper.selectMigrationUser(existUser);
        if (userCount > 0) {
            return 0;
        }
        // 判断身份证是否存在
        FUserIdentity userIdentity = userIdentityMapper.selectByCode(user.getFareacode());
        if (userIdentity != null) {
            return 0;
        }
        user.setFgooglebind(false);
        user.setFregistertime(new Date());
        user.setFstatus(UserStatusEnum.NORMAL_VALUE);
        user.setFiscny(UserStatusEnum.NORMAL_VALUE);
        user.setFiscoin(UserStatusEnum.NORMAL_VALUE);
        user.setFlastlogintime(Utils.getTimestamp());
        user.setFupdatetime(Utils.getTimestamp());
        user.setVersion(0);
        user.setFinvalidateintrocount(0);
        user.setFleverlock(0);
        if (userMapper.insert(user) <= 0) {
            throw new BCException("注册用户超时！");
        }
        // 靓号查询
        FBeautiful beautiful = beautifulMapper.selectByBid(user.getFid());
        if (beautiful != null) {
            if (userMapper.updateFshowidByFid(beautiful.getFuid(), user.getFid()) <= 0) {
                throw new BCException("显示ID更新失败！");
            }
            user.setFshowid(beautiful.getFuid());
        } else {
            if (userMapper.updateFshowidByFid(user.getFid(), user.getFid()) <= 0) {
                throw new BCException("显示ID更新失败！");
            }
            user.setFshowid(user.getFid());
        }
        // 虚拟钱包
        List<SystemCoinType> systemCoinTypeList = redisHelper.getCoinTypeListSystem();
        for (SystemCoinType coin : systemCoinTypeList) {
            UserCoinWallet wallet = new UserCoinWallet();
            wallet.setTotal(BigDecimal.ZERO);
            wallet.setFrozen(BigDecimal.ZERO);
            wallet.setBorrow(BigDecimal.ZERO);
            wallet.setCoinId(coin.getId());
            wallet.setUid(user.getFid());
            wallet.setGmtCreate(Utils.getTimestamp());
            wallet.setGmtModified(Utils.getTimestamp());
            wallet.setIco(BigDecimal.ZERO);
            if (coinWalletMapper.insert(wallet) <= 0) {
                throw new BCException("注册用户钱包超时！");
            }
        }
        // 等级
        FUserScore fscore = new FUserScore();
        fscore.setFlevel(0);
        fscore.setFscore(0L);
        fscore.setFtradingqty(0);
        fscore.setVersion(0);
        fscore.setFuid(user.getFid());
        if (userScoreMapper.insert(fscore) <= 0) {
            throw new BCException("注册用户等级超时！");
        }
        // 实名
        if (user.getFhasrealvalidate()) {
            FUserIdentity identity = new FUserIdentity();
            identity.setFuid(user.getFid());
            identity.setFname(user.getFrealname());
            identity.setFtype(user.getFidentitytype());
            identity.setFcountry("中国大陆(China)");
            identity.setFcode(user.getFidentityno());
            identity.setFstatus(IdentityStatusEnum.PASS.getCode());
            identity.setFcreatetime(new Date());
            if (userIdentityMapper.insert(identity) <= 0) {
                throw new BCException("注册用户实名超时！");
            }
        }
        // MQ_USER_ACTION
        mqSend.SendUserAction(user.getFagentid(), user.getFid(), LogUserActionEnum.MIGRATION, "");
        return user.getFid();
    }

    @Override
    public int selectIpCount(String ipLong) {
        return userMapper.selectIpUserCount(ipLong);
    }
}
