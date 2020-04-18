package com.qkwl.service.entrust.tx;

import com.qkwl.common.dto.Enum.EntrustChangeEnum;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.mq.MQEntrustState;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.mq.MQSendHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.service.common.mapper.CommonUserCoinWalletMapper;
import com.qkwl.service.entrust.dao.FEntrustMapper;
import com.qkwl.service.entrust.model.EntrustDO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * 委单接口实现
 *
 * @author LY
 */
@Service("entrustServerTx")
public class EntrustOrderTx {

    @Autowired
    private FEntrustMapper entrustMapper;
    @Autowired
    private CommonUserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private MQSendHelper MQSendHelper;
 

    /**
     * 创建委单
     * @param entrust 委单数据
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BigInteger createEntrust(EntrustDO entrust) throws Exception {
        if (entrustMapper.insert(entrust) <= 0) {
            throw new Exception("entrust insert failure");
        }
        UserCoinWallet wallet = null;
        if(entrust.getFtype().equals(EntrustTypeEnum.BUY.getCode())){
            wallet = userCoinWalletMapper.selectByUidAndCoinId(entrust.getFuid(),entrust.getFbuycoinid());
            if(wallet == null){
                throw new Exception("wallet is null");
            }
            if(wallet.getTotal().compareTo(entrust.getFamount())<0){
                throw new Exception("wallet total balance");
            }
            wallet.setTotal(MathUtils.positive2Negative(entrust.getFamount()));
            wallet.setFrozen(entrust.getFamount());
            wallet.setGmtModified(new Date());
        } else if(entrust.getFtype().equals(EntrustTypeEnum.SELL.getCode())){
            wallet = userCoinWalletMapper.selectByUidAndCoinId(entrust.getFuid(),entrust.getFsellcoinid());
            if(wallet == null){
                throw new Exception("wallet is null");
            }
            if(wallet.getTotal().compareTo(entrust.getFcount())<0){
                throw new Exception("wallet total balance");
            }
            wallet.setTotal(MathUtils.positive2Negative(entrust.getFcount()));
            wallet.setFrozen(entrust.getFcount());
            wallet.setGmtModified(new Date());
        }

        if(wallet == null){
            throw new Exception("wallet is null");
        }
        if (userCoinWalletMapper.changeFinance(wallet) <= 0) {
            throw new Exception("wallet update failure");
        }

        MQEntrustState mqBody = new MQEntrustState();
        mqBody.setTradeId(entrust.getFtradeid());
        mqBody.setBuyID(entrust.getFuid());
        mqBody.setBuyOrderId(entrust.getFid());
        mqBody.setBuyPrize(entrust.getFprize());
        mqBody.setCount(entrust.getFcount());
        String key;
        if (entrust.getFtype().equals(EntrustTypeEnum.BUY.getCode())) {
            mqBody.setType(EntrustChangeEnum.BUY);
            key = "TRADE_BUY_" + entrust.getFid();
        } else if (entrust.getFtype().equals(EntrustTypeEnum.SELL.getCode())) {
            mqBody.setType(EntrustChangeEnum.SELL);
            key = "TRADE_SELL_" + entrust.getFid();
        } else {
            throw new Exception("entrust type error");
        }
        if (!MQSendHelper.send(MQTopic.ENTRUST_STATE, MQConstant.TAG_ENTRUST_STATE, key, mqBody)) {
            throw new Exception("mq send failure");
        }
        return entrust.getFid();
    }

    /**
     * 撤单
     */
    public Boolean cancleEntrust(EntrustDO entrust) throws Exception {
        return entrustMapper.updateByfId(entrust) > 0;
    }
}
