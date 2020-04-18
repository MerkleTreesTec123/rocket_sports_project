package com.qkwl.service.user.tx;

import com.qkwl.common.match.MathUtils;
import com.qkwl.service.user.base.UserWalletBase;
import com.qkwl.service.user.dao.FUserPushMapper;
import com.qkwl.service.user.model.FUserPushDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PushServiceTx extends UserWalletBase {
    @Autowired
    private FUserPushMapper userPushMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean createUserPushOrder(FUserPushDO userPush) throws Exception {
        boolean resultStatus = super.updateUserCoinWallet(userPush.getFuid(), userPush.getFcoinid(),
                MathUtils.positive2Negative(userPush.getFcount()), userPush.getFcount());
        if (!resultStatus) {
            return false;
        }
        if (userPushMapper.insert(userPush) <= 0) {
            throw new Exception();
        }
        return true;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean cancleUserPushOrder(FUserPushDO userPush) throws Exception {
        Boolean result = updateUserCoinWallet(userPush.getFuid(), userPush.getFcoinid(),
                userPush.getFcount(), MathUtils.positive2Negative(userPush.getFcount()));
        if (!result) {
            return false;
        }
        if (userPushMapper.updateByPrimaryKey(userPush) <= 0) {
            throw new Exception();
        }
        return true;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean payUserPushOrder(FUserPushDO userPush, Integer coinId) throws Exception {
        //更新自己出钱包
        Boolean result = super.updateUserCoinWalletTotal(userPush.getFpushuid(), coinId,
                MathUtils.positive2Negative(userPush.getFamount()));
        if (!result) {
            return false;
        }
        //更新自己入钱包
        result = super.updateUserCoinWalletTotal(userPush.getFpushuid(), userPush.getFcoinid(), userPush.getFcount());
        if (!result) {
            throw new Exception();
        }
        //更新对方出钱包
        result = super.updateUserCoinWalletFrozen(userPush.getFuid(), userPush.getFcoinid(),
                MathUtils.positive2Negative(userPush.getFcount()));
        if (!result) {
            throw new Exception();
        }
        //更新对方入钱包
        result = super.updateUserCoinWalletTotal(userPush.getFuid(), coinId, userPush.getFamount());
        if (!result) {
            throw new Exception();
        }
        if (userPushMapper.updateByPrimaryKey(userPush) <= 0) {
            throw new Exception();
        }
        return true;
    }

}
