package com.qkwl.service.user.tx;

import com.qkwl.service.user.base.UserWalletBase;
import com.qkwl.service.user.dao.FRewardCodeMapper;
import com.qkwl.service.user.model.FRewardCodeDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 充值码实现
 */
@Service
public class RewardCodeServiceTx extends UserWalletBase {

    @Autowired
    private FRewardCodeMapper rewardCodeMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean useRewardCode(FRewardCodeDO rewardCode) throws Exception {
        // 更改用户钱包
        boolean result = super.updateUserCoinWalletTotal(rewardCode.getFuid(), rewardCode.getFtype(), rewardCode.getFamount());
        if (!result) {
            throw new Exception();
        }
        if (rewardCodeMapper.updateByPrimaryKey(rewardCode) <= 0) {
            throw new Exception();
        }
        return true;
    }
}
