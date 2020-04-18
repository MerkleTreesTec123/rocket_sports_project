package com.qkwl.service.user.tx;

import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.match.MathUtils;
import com.qkwl.service.user.base.UserWalletBase;
import com.qkwl.service.user.dao.FUserScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * VIP
 */
@Service
public class VipServiceTx extends UserWalletBase {
    @Autowired
    private FUserScoreMapper userScoreMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean createUserVipOrder(FUserScore userScore, Integer coinId, BigDecimal vipPrice) throws Exception {
        Boolean result = super.updateUserCoinWalletTotal(userScore.getFuid(), coinId, MathUtils.positive2Negative(vipPrice));
        if (!result) {
            return false;
        }
        if (userScoreMapper.updateByPrimaryKey(userScore) <= 0) {
            throw new Exception();
        }
        return true;
    }
}
