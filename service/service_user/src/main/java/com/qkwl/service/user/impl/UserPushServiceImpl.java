package com.qkwl.service.user.impl;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.dto.Enum.UserPushStateEnum;
import com.qkwl.common.dto.capital.FUserPushDTO;
import com.qkwl.common.dto.capital.UserPushOrderDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.pre.PreValidationHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserPushService;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.service.common.mapper.UserCommonMapper;
import com.qkwl.service.user.dao.FUserPushMapper;
import com.qkwl.service.user.dao.UserCoinWalletMapper;
import com.qkwl.service.user.model.FUserPushDO;
import com.qkwl.service.user.tx.PushServiceTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userPushService")
public class UserPushServiceImpl implements IUserPushService {

    private static final Logger logger = LoggerFactory.getLogger(UserPushServiceImpl.class);

    @Autowired
    private FUserPushMapper userPushMapper;
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private PushServiceTx pushServiceTx;
    @Autowired
    private UserCommonMapper userCommonMapper;
    @Autowired
    private PreValidationHelper preValidationHelper;
    @Autowired
    private ValidationCheckHelper validationCheckHelper;

    @Override
    public Result createUserPushOrder(UserPushOrderDTO userPushOrder) {
        if (userPushOrder.getUserId() == null) {
            return Result.param("userId is null");
        }
        if (userPushOrder.getPushuId() == null) {
            return Result.param("pushuId is null");
        }
        if (userPushOrder.getCoinId() == null) {
            return Result.param("coinId is null");
        }
        if (userPushOrder.getPrice() == null || userPushOrder.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.param("price is null");
        }
        if (userPushOrder.getCount() == null || userPushOrder.getCount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.param("count is null");
        }
        if(StringUtils.isEmpty(userPushOrder.getPhoneCode()) && StringUtils.isEmpty(userPushOrder.getGoogleCode())){
            return Result.param("phoneCode and googleCode is null");
        }
        if(StringUtils.isEmpty(userPushOrder.getTradePass())){
            return Result.param("tradePass is null");
        }
        if(StringUtils.isEmpty(userPushOrder.getIp())){
            return Result.param("ip is null");
        }
        if(userPushOrder.getPlatformId() == null){
            return Result.param("platformId is null");
        }
        userPushOrder.setPrice(MathUtils.toScaleNum(userPushOrder.getPrice(), MathUtils.ENTER_COIN_SCALE));
        userPushOrder.setCount(MathUtils.toScaleNum(userPushOrder.getCount(), MathUtils.ENTER_COIN_SCALE));
        if(userPushOrder.getUserId().equals(userPushOrder.getPushuId())){
            return Result.failure(1000, "对方UID不能为自己的UID");
        }
        FUser user  = userCommonMapper.selectOneById(userPushOrder.getUserId());
        if(user == null){
            return Result.param("user is not found");
        }
        Result preResult = preValidationHelper.validateCapital(user, "coin");
        if(!preResult.getSuccess()){
            return preResult;
        }
        FUser pushUser  = userCommonMapper.selectOneByShowId(userPushOrder.getPushuId());
        if(pushUser == null){
            return Result.failure(1001, "对方UID不存在");
        }
        BigDecimal amount = MathUtils.toScaleNum(MathUtils.mul(userPushOrder.getPrice(), userPushOrder.getCount()), MathUtils.DEF_CNY_SCALE);
        UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoin(userPushOrder.getUserId(), userPushOrder.getCoinId());
        if (wallet == null) {
            logger.error("wallt is null  userId:{},coinId:{} ", userPushOrder.getUserId(), userPushOrder.getCoinId());
            return Result.failure(10018, "余额不足");
        }
        if (wallet.getTotal().compareTo(userPushOrder.getCount()) < 0) {
            return Result.failure(10018, "余额不足");
        }
        Result checkResult = validationCheckHelper.getCapitalCheck(user, userPushOrder.getPhoneCode(),
                BusinessTypeEnum.SMS_PUSHASSET.getCode(),userPushOrder.getGoogleCode(),userPushOrder.getTradePass(),
                userPushOrder.getIp(), userPushOrder.getPlatformId());
        if(!checkResult.getSuccess()){
            return checkResult;
        }
        FUserPushDO userPush = new FUserPushDO();
        userPush.setFamount(amount);
        userPush.setFcount(userPushOrder.getCount());
        userPush.setFcoinid(userPushOrder.getCoinId());
        userPush.setFprice(userPushOrder.getPrice());
        userPush.setFpushuid(userPushOrder.getPushuId());
        userPush.setFuid(userPushOrder.getUserId());
        userPush.setFstate(UserPushStateEnum.PAYWAIT.getCode());
        userPush.setFcreatetime(new Date());
        userPush.setVersion(0);
        try {
            if (pushServiceTx.createUserPushOrder(userPush)) {
                return Result.success("订单创建成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("createUserPushOrder {}", userPushOrder.toString());
        }
        return Result.failure("订单创建失败");
    }

    @Override
    public Result cancleUserPushOrder(Integer id, Integer userId) {
        if (id == null) {
            return Result.param("id is null");
        }
        if (userId == null) {
            return Result.param("userId is null");
        }
        FUserPushDO userPush = userPushMapper.selectByPrimaryKey(id);
        if (userPush == null) {
            return Result.failure(1000, "PUSH记录未找到");
        }
        if(userPush.getFcoinid().equals(7)){
            // DHG特殊处理
            return Result.failure(1000, "PUSH记录未找到");
        }
        if (!userPush.getFuid().equals(userId)) {
            return Result.failure(1000, "PUSH记录未找到");
        }
        if (!userPush.getFstate().equals(UserPushStateEnum.PAYWAIT.getCode())) {
            return Result.failure(1001, "PUSH记录状态错误");
        }
        userPush.setFstate(UserPushStateEnum.PAYCANCEL.getCode());
        userPush.setFupdatetime(new Date());
        try {
            if (pushServiceTx.cancleUserPushOrder(userPush)) {
                return Result.success("PUSH记录取消成功");
            }
        } catch (Exception e) {
            logger.error("createUserPushOrder id:{},userId:{} ", id, userId, e);
        }
        return Result.failure("PUSH记录取消失败");
    }

    @Override
    public Result payUserPushOrder(Integer id, Integer userId, Integer coinId, String tradePass, String ip) {
        if (id == null) {
            return Result.param("id is null");
        }
        if (userId == null) {
            return Result.param("userId is null");
        }
        if (coinId == null) {
            return Result.param("coinId is null");
        }
        if (StringUtils.isEmpty(tradePass)) {
            return Result.param("tradePass is null");
        }
        if (StringUtils.isEmpty(ip)) {
            return Result.param("ip is null");
        }
        FUser user = userCommonMapper.selectOneById(userId);
        if(user == null){
            return Result.param("user is not found");
        }
        if(StringUtils.isEmpty(user.getFtradepassword())){
            return Result.failure(10120, "请设置交易密码");
        }
        Result preResult = preValidationHelper.validateCapital(user,"cny");
        if(!preResult.getSuccess()){
            return preResult;
        }
        Result validateResult = validationCheckHelper.getTradePasswordCheck(user.getFtradepassword(),tradePass,ip);
        if(!validateResult.getSuccess()){
            return validateResult;
        }
        FUserPushDO userPush = userPushMapper.selectByPrimaryKey(id);
        if (userPush == null) {
            return Result.failure(1000, "PUSH记录未找到");
        }
        if (userPush.getFstate().equals(UserPushStateEnum.PAYCANCEL.getCode())) {
            return Result.failure(1001, "PUSH记录已取消");
        }
        if (userPush.getFstate().equals(UserPushStateEnum.PAYSUCCEED.getCode())) {
            return Result.failure(1002, "PUSH记录已付款");
        }
        UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoin(userId, coinId);
        if (wallet == null) {
            logger.error("wallt is null  userId:{},coinId:{} ", userId, coinId);
            return Result.failure(10118, "余额不足");
        }
        if (wallet.getTotal().compareTo(userPush.getFamount()) < 0) {
            return Result.failure(10118, "余额不足");
        }
        userPush.setFstate(UserPushStateEnum.PAYSUCCEED.getCode());
        userPush.setFupdatetime(new Date());
        try {
            if (pushServiceTx.payUserPushOrder(userPush, coinId)) {
                return Result.success("付款成功");
            }
        } catch (Exception e) {
            logger.error("payUserPushOrder id:{},userId:{},coinId:{} ", id, userId, coinId, e);
        }
        return Result.failure("付款失败");
    }

    @Override
    public Pagination<FUserPushDTO> listUserPushOrder(Pagination<FUserPushDTO> page, FUserPushDTO userPush) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("fuid", userPush.getFuid());
        map.put("fpushuid", userPush.getFpushuid());
        int count = userPushMapper.selectUserPushCount(map);
        if (count > 0) {
            List<FUserPushDO> list = userPushMapper.selectUserPushList(map);
            Map<Integer, String> coinNameMap = redisHelper.getCoinTypeNameMap();
            for (FUserPushDO fUserPush : list) {
                fUserPush.setFcoin_s(coinNameMap.get(fUserPush.getFcoinid()));
            }
            page.setData(PojoConvertUtil.convert(list, FUserPushDTO.class));
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }
}
