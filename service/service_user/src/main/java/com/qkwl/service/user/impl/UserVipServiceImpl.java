package com.qkwl.service.user.impl;

import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.pre.PreValidationHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserVipService;
import com.qkwl.service.common.mapper.UserCommonMapper;
import com.qkwl.service.user.dao.FUserScoreMapper;
import com.qkwl.service.user.dao.UserCoinWalletMapper;
import com.qkwl.service.user.tx.VipServiceTx;
import com.qkwl.service.user.utils.MQSendUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户VIP实现
 */
@Service("userVipService")
public class UserVipServiceImpl implements IUserVipService {

    private static final Logger logger = LoggerFactory.getLogger(UserVipServiceImpl.class);

    @Autowired
    private FUserScoreMapper userScoreMapper;
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private UserCommonMapper userCommonMapper;
    @Autowired
    private MQSendUtils mqSendUtils;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private VipServiceTx vipServiceTx;
    @Autowired
    private ValidationCheckHelper validationCheckHelper;
    @Autowired
    private PreValidationHelper preValidationHelper;

    @Override
    public Result createUserVipOrder(Integer userId, Integer coinId, String tradePass, String ip) {
        if (userId == null) {
            return Result.param("userId is null");
        }
        FUser user = userCommonMapper.selectOneById(userId);
        if(user == null){
            return Result.param("user is not fount");
        }
        Result preResult = preValidationHelper.validateCapital(user,"cny");
        if(!preResult.getSuccess()){
            return preResult;
        }
        Result validateResult = validationCheckHelper.getTradePasswordCheck(user.getFtradepassword(), tradePass, ip);
        if (!validateResult.getSuccess()) {
            return validateResult;
        }
        FUserScore fuserScore = this.userScoreMapper.selectByUid(userId);
        if (fuserScore.getFlevel() == 6) {
            return Result.failure(1000, "您已经是VIP6");
        }
        BigDecimal vipPrice = new BigDecimal(redisHelper.getSystemArgs("buyVipPrice"));
        UserCoinWallet userCoinWallet = userCoinWalletMapper.selectByUidAndCoin(userId, coinId);
        if (userCoinWallet.getTotal().compareTo(vipPrice) < 0) {
            return Result.failure(10118, "余额不足");
        }
        fuserScore.setFlevel(6);
        fuserScore.setFleveltime(new Date());
        try {
            if (!vipServiceTx.createUserVipOrder(fuserScore, coinId, vipPrice)) {
                return Result.failure("购买失败");
            }
        } catch (Exception e) {
            logger.error("createUserVipOrder is err userId:{},coinId:{},ip:{}", userId, coinId, ip, e);
        }
        mqSendUtils.SendUserAction(userId, LogUserActionEnum.BUY_VIP6, ip);
        return Result.success("购买成功");
    }
}
