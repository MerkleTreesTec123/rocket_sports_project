package com.qkwl.service.user.tx;

import com.qkwl.common.match.MathUtils;
import com.qkwl.service.user.base.UserWalletBase;
import com.qkwl.service.user.dao.FUserFinancesMapper;
import com.qkwl.service.user.model.FUserFinancesDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class FinancesServiceTx extends UserWalletBase {

    @Autowired
    private FUserFinancesMapper userFinancesMapper;

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean createUserFinancesOrder(FUserFinancesDO userFinances) throws Exception {
        //更新虚拟币钱包
        boolean resultStatus = super.updateUserCoinWallet(userFinances.getFuid(), userFinances.getFcoinid(),
                MathUtils.positive2Negative(userFinances.getFamount()), BigDecimal.ZERO, userFinances.getFamount());
        if (!resultStatus) {
            return false;
        }
        // 更新记录
        if (userFinancesMapper.insert(userFinances) <= 0) {
            throw new Exception();
        }
        return true;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean cancleUserFinances(FUserFinancesDO userFinances) throws Exception {
        //更新虚拟币钱包
        boolean resultStatus = super.updateUserCoinWallet(userFinances.getFuid(), userFinances.getFcoinid(),
                userFinances.getFamount(), BigDecimal.ZERO, MathUtils.positive2Negative(userFinances.getFamount()));
        if (!resultStatus) {
            return false;
        }
        // 更新记录
        if (userFinancesMapper.update(userFinances) <= 0) {
            throw new Exception();
        }
        return true;
    }
}
