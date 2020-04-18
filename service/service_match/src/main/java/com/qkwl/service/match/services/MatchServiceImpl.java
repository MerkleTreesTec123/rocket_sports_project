package com.qkwl.service.match.services;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.user.CommissionRecord;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.service.common.mapper.CommonUserCoinWalletMapper;
import com.qkwl.service.match.dao.*;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.entrust.FEntrustLog;
import com.qkwl.common.dto.mq.MQEntrustState;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.mq.MQSendHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.common.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Bidi;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service("matchService")
public class MatchServiceImpl {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MatchServiceImpl.class);

    /**
     * 撮合每次读取委单条数
     */
    private static final int MATCH_MAX = 10;

    /**
     * 撤单每次读取委单条数
     */
    private static final int CANCEL_MAX = 10;

    /**
     * 历史每次读取委单条数
     */
    private static final int HISTORY_MAX = 10;

    /**
     * 免收手续费列表
     */
    private int[] freeFee;

    @Autowired
    private FEntrustMapper fEntrustMapper;
    @Autowired
    private FEntrustLogMapper fEntrustLogMapper;
    @Autowired
    private FEntrustHistoryMapper fEntrustHistoryMapper;
    @Autowired
    private CommonUserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private MQSendHelper entrustMQSend;

    @Autowired
    private CommissionRecordMapper commissionRecordMapper;
    //@Autowired
    //private UserPackageMapper userPackageMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 初始化
     */
    public void init() { }

    /**
     * 平台撮合
     *
     * @param systemTradeType  交易ID
     * @param buyEntrustWait  买委单
     * @param sellEntrustWait 卖委单
     * @return true
     * @throws Exception 执行异常
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public synchronized boolean updateMatch(SystemTradeType systemTradeType, FEntrust buyEntrustWait, FEntrust sellEntrustWait) throws Exception {
        // 锁单
        FEntrust buyEntrust = fEntrustMapper.selectEntrust(buyEntrustWait.getFid());
        FEntrust sellEntrust = fEntrustMapper.selectEntrust(sellEntrustWait.getFid());
        if (buyEntrust == null || sellEntrust == null) {
            return false;
        }
        // 订单状态判断
        if (buyEntrust.getFstatus().equals(EntrustStateEnum.AllDeal.getCode()) ||
                buyEntrust.getFstatus().equals(EntrustStateEnum.Cancel.getCode()) ||
                buyEntrust.getFstatus().equals(EntrustStateEnum.WAITCancel.getCode()) ||
                sellEntrust.getFstatus().equals(EntrustStateEnum.AllDeal.getCode()) ||
                sellEntrust.getFstatus().equals(EntrustStateEnum.Cancel.getCode()) ||
                sellEntrust.getFstatus().equals(EntrustStateEnum.WAITCancel.getCode())) {
            return false;
        }

        if (MathUtils.compareTo(buyEntrust.getFprize(), sellEntrust.getFprize()) < 0) {
            return false;
        }

        // 买卖数量比较结果
        int countResult = MathUtils.compareTo(buyEntrust.getFleftcount(), sellEntrust.getFleftcount());
        // 成交价
        BigDecimal successPrize = null;

        BigDecimal lastPrize = redisHelper.getLastPrice(systemTradeType.getId());
        if (MathUtils.compareTo(lastPrize, buyEntrust.getFprize()) == -1 &&
                MathUtils.compareTo(lastPrize, sellEntrust.getFprize()) == 1) {
            // 卖  < 上次成交价 < 买
            successPrize = lastPrize;
        } else if (MathUtils.compareTo(sellEntrust.getFprize(), lastPrize) >= 0) {
            // 卖 > 上次成交价
            successPrize = sellEntrust.getFprize();
        } else if (MathUtils.compareTo(buyEntrust.getFprize(), lastPrize) <= 0) {
            // 买 < 上次成交价
            successPrize = buyEntrust.getFprize();
        } else {
            throw new Exception("successPrize err!");
        }

        // 成交价报错
        if (successPrize.compareTo(BigDecimal.ZERO) == -1) {
            logger.error("successPrize err : {}", successPrize);
            return false;
        }
        // 买单手续费(买币收币)
        BigDecimal buyCountFee;
        // 卖单手续费(卖币收钱)
        BigDecimal sellAmountFee;
        // 默认最小手续费
        BigDecimal minFee = new BigDecimal("0.0000000001");
        // 成交金额
        BigDecimal successAmount;
        // 成交数量
        BigDecimal successCount;
        // 剩余未成交
        BigDecimal buyLeftCount;
        BigDecimal sellLeftCount;
        // 手续费标志
        boolean isBuyFee = true;
        boolean isSellFee = true;
        // 检测免手续费列表
        if (freeFee != null && freeFee.length > 0) {
            for (int uid: freeFee) {
                if (uid == buyEntrust.getFuid()) {
                    isBuyFee = false;
                }
                if (uid == sellEntrust.getFuid()) {
                    isSellFee = false;
                }
            }
        }
        //检测
        FApiAuth buyApiAuth = null;
        FApiAuth sellApiAuth = null;
        if (buyEntrust.getFsource() == EntrustSourceEnum.API.getCode()) {
            //检查是否是api的请求
            buyApiAuth = isApiAuth(buyEntrust.getFuid());
            if (buyApiAuth == null){
                return false;
            }
        }

        if (sellEntrust.getFsource() == EntrustSourceEnum.API.getCode()) {
            sellApiAuth = isApiAuth(sellEntrust.getFuid());
            if (sellApiAuth == null){
                return false;
            }
        }

        // 比较
        if (countResult > 0) {
            // 买数量 > 卖数量 ：买单部分成交，卖单完全成交
            successAmount = MathUtils.mul(sellEntrust.getFleftcount(), successPrize);
            buyLeftCount = MathUtils.sub(buyEntrust.getFleftcount(), sellEntrust.getFleftcount());
            sellLeftCount = BigDecimal.ZERO;
            successCount = sellEntrust.getFleftcount();

            //卖的手续费
            sellAmountFee = calculateSellFee(sellApiAuth,successAmount,systemTradeType,isSellFee);
            buyCountFee = calculateBuyFee(buyApiAuth,successCount,systemTradeType,isBuyFee);

            buyEntrust.setFstatus(EntrustStateEnum.PartDeal.getCode());
            sellEntrust.setFstatus(EntrustStateEnum.AllDeal.getCode());
        } else if (countResult < 0) {
            // 买数量 < 卖数量：买单完全成交，卖单部分成交
            successAmount = MathUtils.mul(buyEntrust.getFleftcount(), successPrize);
            buyLeftCount = BigDecimal.ZERO;
            sellLeftCount = MathUtils.sub(sellEntrust.getFleftcount(), buyEntrust.getFleftcount());
            successCount = buyEntrust.getFleftcount();
            sellAmountFee = calculateSellFee(sellApiAuth,successAmount,systemTradeType,isSellFee);
            buyCountFee = calculateBuyFee(buyApiAuth,successCount,systemTradeType,isBuyFee);

            buyEntrust.setFstatus(EntrustStateEnum.AllDeal.getCode());
            sellEntrust.setFstatus(EntrustStateEnum.PartDeal.getCode());
        } else {
            // 买数量 = 卖数量：买卖单完全成交
            successAmount = MathUtils.mul(buyEntrust.getFleftcount(), successPrize);
            buyLeftCount = BigDecimal.ZERO;
            sellLeftCount = BigDecimal.ZERO;
            successCount = buyEntrust.getFleftcount();
            sellAmountFee = calculateSellFee(sellApiAuth,successAmount,systemTradeType,isSellFee);
            buyCountFee = calculateBuyFee(buyApiAuth,successCount,systemTradeType,isBuyFee);

            buyEntrust.setFstatus(EntrustStateEnum.AllDeal.getCode());
            sellEntrust.setFstatus(EntrustStateEnum.AllDeal.getCode());
        }

        if (buyApiAuth == null){
            // 买入收币手续费不足0.0000000001时收取0.0000000001；
            if (buyCountFee.compareTo(minFee) < 0 && systemTradeType.getBuyFee().compareTo(BigDecimal.ZERO) > 0) {
                buyCountFee = minFee;
            }
        }

        if (sellApiAuth == null){
            // 卖出收钱手续费不足0.0000000001时收取0.0000000001；
            if (sellAmountFee.compareTo(minFee) < 0 && systemTradeType.getSellFee().compareTo(BigDecimal.ZERO) > 0) {
                sellAmountFee = minFee;
            }
        }

        // 钱包日志
        String buyWalletLog = "";
        String sellWalletLog = "";
        // 更新用户钱包
        UserCoinWallet buyWallet = userCoinWalletMapper.selectByUidAndCoinId(buyEntrust.getFuid(), buyEntrust.getFbuycoinid());
        UserCoinWallet sellWallet;
        // 卖日志 手续费是否抵扣
        int sellDeduction = 0;
        // 买日志 手续费是否抵扣
        int buyDeduction = 0;
        if (buyEntrust.getFuid().equals(sellEntrust.getFuid())) {
            BigDecimal totalTmp = MathUtils.sub(successAmount, sellAmountFee);
            BigDecimal total = MathUtils.add(buyWallet.getTotal(), totalTmp);
            buyWalletLog += "{RMB:[Bfrozen:" + buyWallet.getFrozen() + ",";
            sellWalletLog += "{RMB:[Btotal:" + buyWallet.getTotal() + ",";
            BigDecimal frozen = MathUtils.sub(buyWallet.getFrozen(), successAmount);
            buyWallet.setTotal(totalTmp);
            buyWallet.setFrozen(MathUtils.positive2Negative(successAmount));
            buyWallet.setGmtModified(Utils.getTimestamp());
            buyWalletLog += "Afrozen:" + frozen + ",vol:" + successAmount + "],";
            sellWalletLog += "Atotal:" + total + ",vol:" + totalTmp + ",fee:" + sellAmountFee + "],";
            if (this.userCoinWalletMapper.changeFinance(buyWallet) <= 0) {
                throw new Exception("buyWallet update err");
            }
        } else {
           sellWallet = userCoinWalletMapper.selectByUidAndCoinId(sellEntrust.getFuid(), sellEntrust.getFbuycoinid());
            buyWalletLog += "{RMB:[Bfrozen:" + buyWallet.getFrozen() + ",";
            sellWalletLog += "{RMB:[Btotal:" + sellWallet.getTotal() + ",";
            BigDecimal frozen = MathUtils.sub(buyWallet.getFrozen(), successAmount);
            buyWallet.setFrozen(MathUtils.positive2Negative(successAmount));
            buyWallet.setTotal(BigDecimal.ZERO);
            buyWallet.setGmtModified(Utils.getTimestamp());

            BigDecimal totalTmp = MathUtils.sub(successAmount, sellAmountFee);
            BigDecimal total = MathUtils.add(sellWallet.getTotal(), totalTmp);
            sellWallet.setTotal(totalTmp);
            sellWallet.setFrozen(BigDecimal.ZERO);
            sellWallet.setGmtModified(Utils.getTimestamp());
            buyWalletLog += "Afrozen:" + frozen + ",vol:" + successAmount + "],";
            sellWalletLog += "Atotal:" + total + ",vol:" + totalTmp + ",fee:" + sellAmountFee + "],";
            if (this.userCoinWalletMapper.changeFinance(buyWallet) <= 0) {
                throw new Exception("buyWallet update err");
            }
            if (this.userCoinWalletMapper.changeFinance(sellWallet) <= 0) {
                throw new Exception("sellWallet update err");
            }
        }
        // 更新用户虚拟币钱包
        UserCoinWallet buyVirtualWallet = this.userCoinWalletMapper.selectByUidAndCoinId(buyEntrust.getFuid(), buyEntrust.getFsellcoinid());
        UserCoinWallet sellVirtualWallet;
        if (buyEntrust.getFuid().equals(sellEntrust.getFuid())) {
            BigDecimal totalTmp = MathUtils.sub(successCount, buyCountFee);

            BigDecimal total = MathUtils.add(buyVirtualWallet.getTotal(), totalTmp);
            buyWalletLog += "Coin:[Btotal:" + buyVirtualWallet.getTotal() + ",";
            sellWalletLog += "Coin:[Bfrozen:" + buyVirtualWallet.getFrozen() + ",";
            BigDecimal frozen = MathUtils.sub(buyVirtualWallet.getFrozen(), successCount);
            buyVirtualWallet.setTotal(totalTmp);
            buyVirtualWallet.setFrozen(MathUtils.positive2Negative(successCount));
            buyVirtualWallet.setGmtModified(Utils.getTimestamp());
            buyWalletLog += "Atotal:" + total + ",vol:" + totalTmp + ",fee:" + buyCountFee + "],my:1}";
            sellWalletLog += "Afrozen:" + frozen + ",vol:" + successCount + "],my:1}";
            if (this.userCoinWalletMapper.changeFinance(buyVirtualWallet) <= 0) {
                throw new Exception();
            }
        } else {
            sellVirtualWallet = this.userCoinWalletMapper.selectByUidAndCoinId(sellEntrust.getFuid(), sellEntrust.getFsellcoinid());
            BigDecimal totalTmp = MathUtils.sub(successCount, buyCountFee);

            BigDecimal total = MathUtils.add(buyVirtualWallet.getTotal(), totalTmp);
            buyWalletLog += "Coin:[Btotal:" + buyVirtualWallet.getTotal() + ",";
            sellWalletLog += "Coin:[Bfrozen:" + sellVirtualWallet.getFrozen() + ",";
            buyVirtualWallet.setTotal(totalTmp);
            buyVirtualWallet.setFrozen(BigDecimal.ZERO);
            buyVirtualWallet.setGmtModified(Utils.getTimestamp());
            BigDecimal frozen = MathUtils.sub(sellVirtualWallet.getFrozen(), successCount);
            sellVirtualWallet.setFrozen(MathUtils.positive2Negative(successCount));
            sellVirtualWallet.setTotal(BigDecimal.ZERO);
            sellVirtualWallet.setGmtModified(Utils.getTimestamp());
            buyWalletLog += "Atotal:" + total + ",vol:" + totalTmp + ",fee:" + buyCountFee + "]}";
            sellWalletLog += "Afrozen:" + frozen + ",vol:" + successCount + "]}";
            if (this.userCoinWalletMapper.changeFinance(buyVirtualWallet) <= 0) {
                throw new Exception("buyVirtualWallet update err");
            }
            if (this.userCoinWalletMapper.changeFinance(sellVirtualWallet) <= 0) {
                throw new Exception("sellVirtualWallet update err");
            }
        }

        //买的佣金
        //commissionFee(buyCountFee,buyEntrust);

        // 买单成交价
        buyEntrust.setFlastamount(MathUtils.add(buyEntrust.getFlastamount(), successPrize));
        buyEntrust.setFlastcount(buyEntrust.getFlastcount() + 1);
        buyEntrust.setFlast(MathUtils.div(buyEntrust.getFlastamount(), new BigDecimal(buyEntrust.getFlastcount())));
        // 修改买单
        buyEntrust.setFsuccessamount(MathUtils.add(buyEntrust.getFsuccessamount(), successAmount));
        buyEntrust.setFleftcount(buyLeftCount);
        buyEntrust.setFfees(MathUtils.add(buyEntrust.getFfees(), buyCountFee));
        buyEntrust.setFlastupdattime(Utils.getTimestamp());
        // 卖单成交价
        sellEntrust.setFlastamount(MathUtils.add(sellEntrust.getFlastamount(), successPrize));
        sellEntrust.setFlastcount(sellEntrust.getFlastcount() + 1);
        sellEntrust.setFlast(MathUtils.div(sellEntrust.getFlastamount(), new BigDecimal(sellEntrust.getFlastcount())));
        // 修改卖单
        sellEntrust.setFsuccessamount(MathUtils.add(sellEntrust.getFsuccessamount(), successAmount));
        sellEntrust.setFleftcount(sellLeftCount);
        sellEntrust.setFfees(MathUtils.add(sellEntrust.getFfees(), sellAmountFee));
        sellEntrust.setFlastupdattime(Utils.getTimestamp());
        // 保存日志
        FEntrustLog buyLog = new FEntrustLog();
        buyLog.setFentrustid(buyEntrust.getFid());
        buyLog.setFmatchid(sellEntrust.getFid());
        buyLog.setFentrusttype(buyEntrust.getFtype());
        buyLog.setFtradeid(systemTradeType.getId());
        buyLog.setFamount(successAmount);
        buyLog.setFcount(successCount);
        buyLog.setFprize(successPrize);
        buyLog.setFisactive(buyEntrust.getFcreatetime().getTime() > sellEntrust.getFcreatetime().getTime());
        buyLog.setFcreatetime(Utils.getTimestamp());
        buyLog.setFremark(buyWalletLog);
        buyLog.setVersion(0);
        buyLog.setFuid(buyEntrust.getFuid());
        buyLog.setFfees(buyCountFee);
        buyLog.setCoinid(buyEntrust.getFsellcoinid());
        buyLog.setDeduction(buyDeduction);


        FEntrustLog sellLog = new FEntrustLog();
        sellLog.setFentrustid(sellEntrust.getFid());
        sellLog.setFmatchid(buyEntrust.getFid());
        sellLog.setFentrusttype(sellEntrust.getFtype());
        sellLog.setFtradeid(systemTradeType.getId());
        sellLog.setFamount(successAmount);
        sellLog.setFcount(successCount);
        sellLog.setFprize(successPrize);
        sellLog.setFisactive(sellEntrust.getFcreatetime().getTime() > buyEntrust.getFcreatetime().getTime());
        sellLog.setFcreatetime(Utils.getTimestamp());
        sellLog.setFremark(sellWalletLog);
        sellLog.setVersion(0);
        sellLog.setFuid(sellEntrust.getFuid());
        sellLog.setFfees(sellAmountFee);
        sellLog.setCoinid(sellEntrust.getFbuycoinid());
        sellLog.setDeduction(sellDeduction);

        if (this.fEntrustLogMapper.insert(buyLog) <= 0) {
            throw new Exception("buyLog insert err");
        }
        if (this.fEntrustLogMapper.insert(sellLog) <= 0) {
            throw new Exception("sellLog insert err");
        }
        // 更新委单
        if (this.fEntrustMapper.updateEntrust(buyEntrust) <= 0) {
            throw new Exception("buyEntrust insert err");
        }
        if (this.fEntrustMapper.updateEntrust(sellEntrust) <= 0) {
            throw new Exception("sellEntrust insert err");
        }


        // 买：多扣退钱
        if (buyEntrust.getFstatus().equals(EntrustStateEnum.AllDeal.getCode())) {
            BigDecimal leftAmount = MathUtils.sub(buyEntrust.getFamount(), buyEntrust.getFsuccessamount());
            if (leftAmount.compareTo(BigDecimal.ZERO) > 0) {
                buyWallet.setFrozen(MathUtils.positive2Negative(leftAmount));
                buyWallet.setTotal(leftAmount);
                buyWallet.setGmtModified(Utils.getTimestamp());
                if (this.userCoinWalletMapper.changeFinance(buyWallet) <= 0) {
                    throw new Exception("buyVirtualWallet update giver err");
                }
            }
        }
        // MQ_ENTRUST_STATE
        int successType = 1;
        if (buyEntrust.getFcreatetime().getTime() > sellEntrust.getFcreatetime().getTime()) {
            successType = 0;
        }
        // entrust MQ
        MQEntrustState mqBody = new MQEntrustState();
        mqBody.setTradeId(buyEntrust.getFtradeid());
        mqBody.setType(EntrustChangeEnum.SUCCEED);
        mqBody.setMatchType(successType);
        mqBody.setBuyOrderId(buyEntrust.getFid());
        mqBody.setBuyID(buyEntrust.getFuid());
        mqBody.setBuyPrize(buyEntrust.getFprize());
        mqBody.setSellOrderId(sellEntrust.getFid());
        mqBody.setSellID(sellEntrust.getFuid());
        mqBody.setSellPrize(sellEntrust.getFprize());
        mqBody.setLast(successPrize);
        mqBody.setCount(successCount);
        String key = "TRADE_MATCH_" + buyEntrust.getFid() + "_" + buyEntrust.getFleftcount() + "_" + sellEntrust.getFid() + "_" + sellEntrust.getFleftcount();
        entrustMQSend.offer(MQTopic.ENTRUST_STATE, MQConstant.TAG_ENTRUST_STATE, key, mqBody);
        // Score MQ
        //BigDecimal tradeAmount = MathUtils.mul(successPrize, successCount);
//        if (tradeAmount.compareTo(new BigDecimal("100")) >= 0) {
//            scoreHelper.SendAsync(mqBody.getBuyID(), tradeAmount, ScoreTypeEnum.TRADING.getCode(), "交易-买卖" + mqBody.getTradeId() + ":" + mqBody.getCount());
//            scoreHelper.SendAsync(mqBody.getSellID(), tradeAmount, ScoreTypeEnum.TRADING.getCode(), "交易-买卖" + mqBody.getTradeId() + ":" + mqBody.getCount());
//        }
        // null
        if (buyEntrust.getFstatus().equals(EntrustStateEnum.AllDeal.getCode())) {
            buyEntrustWait = null;
        }
        if (sellEntrust.getFstatus().equals(EntrustStateEnum.AllDeal.getCode())) {
            sellEntrustWait = null;
        }
        return true;
    }

    /**
     * 平台撤单
     * @param fEntrust 委单
     * @return true
     * @throws Exception 执行异常
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public synchronized void updateCancelMatch(FEntrust fEntrust) throws Exception {
        // 锁单
        fEntrust = fEntrustMapper.selectEntrust(fEntrust.getFid());
        if (!fEntrust.getFstatus().equals(EntrustStateEnum.WAITCancel.getCode())) {
            logger.error("cancelMatch status err : {}", fEntrust.getFid());
            return;
        }
        // 根据状态判断
        if (fEntrust.getFtype().equals(EntrustTypeEnum.BUY.getCode())) {
            // 查询钱包
            UserCoinWallet fUserWallet = userCoinWalletMapper.selectByUidAndCoinId(fEntrust.getFuid(), fEntrust.getFbuycoinid());
            if (fUserWallet == null) {
                throw new Exception("userWallet is null");
            }
            // 计算
            BigDecimal leftAmount = MathUtils.sub(fEntrust.getFamount(), fEntrust.getFsuccessamount());
            //BigDecimal walletTotal = MathUtils.add(fUserWallet.getTotal(), leftAmount);
            //BigDecimal walletFrozen = MathUtils.sub(fUserWallet.getFrozen(), leftAmount);
            // 更新钱包
            fUserWallet.setTotal(leftAmount);
            fUserWallet.setFrozen(MathUtils.positive2Negative(leftAmount));
            fUserWallet.setGmtModified(Utils.getTimestamp());
            if (this.userCoinWalletMapper.changeFinance(fUserWallet) <= 0) {
                throw new Exception("userWallet update err");
            }
        } else if (fEntrust.getFtype().equals(EntrustTypeEnum.SELL.getCode())) {
            // 查询钱包
            UserCoinWallet fUserVirtualWallet = userCoinWalletMapper.selectByUidAndCoinId(fEntrust.getFuid(), fEntrust.getFsellcoinid());
            if (fUserVirtualWallet == null) {
                throw new Exception("userVirtualWallet is null");
            }
            // 计算
            BigDecimal leftAmount = fEntrust.getFleftcount();
            //BigDecimal walletTotal = MathUtils.add(fUserVirtualWallet.getTotal(), leftAmount);
            //BigDecimal walletFrozen = MathUtils.sub(fUserVirtualWallet.getFrozen(), leftAmount);
            // 更新钱包
            fUserVirtualWallet.setTotal(leftAmount);
            fUserVirtualWallet.setFrozen(MathUtils.positive2Negative(leftAmount));
            fUserVirtualWallet.setGmtModified(Utils.getTimestamp());
            if (this.userCoinWalletMapper.changeFinance(fUserVirtualWallet) <= 0) {
                throw new Exception("userVirtualWallet update err");
            }
        } else {
            throw new Exception("entrust canel type err");
        }
        fEntrust.setFstatus(EntrustStateEnum.Cancel.getCode());
        fEntrust.setFlastupdattime(Utils.getTimestamp());
        if (fEntrustMapper.updateEntrust(fEntrust) <= 0) {
            throw new Exception("entrust update err");
        }
        // entrust MQ
        MQEntrustState mqBody = new MQEntrustState();
        mqBody.setTradeId(fEntrust.getFtradeid());
        mqBody.setType(EntrustChangeEnum.CANCEL);
        mqBody.setCancelType(fEntrust.getFtype());
        mqBody.setBuyOrderId(fEntrust.getFid());
        mqBody.setBuyID(fEntrust.getFuid());
        mqBody.setBuyPrize(fEntrust.getFprize());
        mqBody.setCount(fEntrust.getFleftcount());
        String key = "TRADE_CANCEL_" + fEntrust.getFid() + "_" + fEntrust.getFleftcount();
        entrustMQSend.offer(MQTopic.ENTRUST_STATE, MQConstant.TAG_ENTRUST_STATE, key, mqBody);
    }

    /**
     * 更新委单历史
     *
     * @param fEntrust 委单
     * @return true
     * @throws Exception 执行异常
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public synchronized boolean updateMatchHistory(FEntrust fEntrust) throws Exception {
        // 锁单
        fEntrust = fEntrustMapper.selectEntrust(fEntrust.getFid());
        // history
        FEntrustHistory fEntrustHistory = CopyHistory(fEntrust);
        int result = fEntrustHistoryMapper.insert(fEntrustHistory);
        if (result <= 0) {
            throw new Exception();
        }
        if (fEntrustMapper.deleteByfId(fEntrust.getFid()) <= 0) {
            throw new Exception();
        }
        return true;
    }

    /**
     * 获取待转移历史列表
     */
    public List<FEntrust> getHistoryEntrust(int tradeId) {
        List<FEntrust> fEntrusts = fEntrustMapper.selectHistoryEntrust(tradeId, HISTORY_MAX);
        return fEntrusts;
    }

    /**
     * 获取待撤单列表
     */
    public List<FEntrust> getWaitCancelEntrust(int tradeId) {
        return fEntrustMapper.selectWaitCancelEntrust(tradeId, CANCEL_MAX);
    }

    /**
     * 获取排序买单
     * @param tradeId 交易ID
     * @return 委单列表
     */
    public List<FEntrust> getBuyEntrusts(int tradeId) {
        List<FEntrust> fEntrusts = fEntrustMapper.selectGoingBuyEntrust(tradeId, MATCH_MAX);
        Collections.sort(fEntrusts, buyCompareDESC);
        return fEntrusts;
    }

    /**
     * 获取排序买单
     * @param tradeId 交易ID
     * @return 委单列表
     */
    public List<FEntrust> getBuyEntrustsByApi(int tradeId) {
        List<FEntrust> fEntrusts = fEntrustMapper.selectGoingBuyEntrustByApi(tradeId, MATCH_MAX);
        Collections.sort(fEntrusts, buyCompareDESC);
        return fEntrusts;
    }

    /**
     * 获取排序卖单
     * @param tradeId 交易ID
     * @return 委单列表
     */
    public List<FEntrust> getSellEntrusts(int tradeId) {
        List<FEntrust> fEntrusts = fEntrustMapper.selectGoingSellEntrust(tradeId, MATCH_MAX);
        Collections.sort(fEntrusts, sellCompareASC);
        return fEntrusts;
    }

    /**
     * 获取排序卖单
     * @param tradeId 交易ID
     * @return 委单列表
     */
    public List<FEntrust> getSellEntrustsByApi(int tradeId) {
        List<FEntrust> fEntrusts = fEntrustMapper.selectGoingSellEntrustByApi(tradeId, MATCH_MAX);
        Collections.sort(fEntrusts, sellCompareASC);
        return fEntrusts;
    }

    /**
     * 降序-买单价格从高到低
     */
    private Comparator<FEntrust> buyCompareDESC = new Comparator<FEntrust>() {
        public int compare(FEntrust o1, FEntrust o2) {
            // 1(大于) 0(等于) -1(小于)
            int prizeResult = MathUtils.compareTo(o2.getFprize(), o1.getFprize());
            if (prizeResult == 0) {
                // 价格相同, 比较时间
                return o1.getFcreatetime().compareTo(o2.getFcreatetime());
            } else {
                return prizeResult;
            }
        }
    };

    /**
     * 升序-卖单价格从低到高
     */
    private Comparator<FEntrust> sellCompareASC = new Comparator<FEntrust>() {
        public int compare(FEntrust o1, FEntrust o2) {
            // 1(大于) 0(等于) -1(小于)
            int prizeResult = MathUtils.compareTo(o1.getFprize(), o2.getFprize());
            if (prizeResult == 0) {
                // 价格相同, 比较时间
                return o1.getFcreatetime().compareTo(o2.getFcreatetime());
            } else {
                return prizeResult;
            }
        }
    };

    /**
     * 历史委单复制
     *
     * @param fEntrust 原始委单
     * @return 历史委单
     */
    private synchronized FEntrustHistory CopyHistory(FEntrust fEntrust) {
        FEntrustHistory fEntrustHistory = new FEntrustHistory();
        fEntrustHistory.setFentrustid(fEntrust.getFid());
        fEntrustHistory.setFbuycoinid(fEntrust.getFbuycoinid());
        fEntrustHistory.setFsellcoinid(fEntrust.getFsellcoinid());
        fEntrustHistory.setFuid(fEntrust.getFuid());
        fEntrustHistory.setFtradeid(fEntrust.getFtradeid());
        fEntrustHistory.setFstatus(fEntrust.getFstatus());
        fEntrustHistory.setFtype(fEntrust.getFtype());
        fEntrustHistory.setFmatchtype(fEntrust.getFmatchtype());
        fEntrustHistory.setFlast(fEntrust.getFlast());
        fEntrustHistory.setFprize(fEntrust.getFprize());
        fEntrustHistory.setFcount(fEntrust.getFcount());
        fEntrustHistory.setFamount(fEntrust.getFamount());
        fEntrustHistory.setFsuccessamount(fEntrust.getFsuccessamount());
        fEntrustHistory.setFleftcount(fEntrust.getFleftcount());
        fEntrustHistory.setFleftfees(fEntrust.getFleftfees());
        fEntrustHistory.setFfees(fEntrust.getFfees());
        fEntrustHistory.setFsource(fEntrust.getFsource());
        fEntrustHistory.setFhuobientrustid(fEntrust.getFhuobientrustid());
        fEntrustHistory.setFhuobiaccountid(fEntrust.getFhuobiaccountid());
        fEntrustHistory.setFlastupdattime(fEntrust.getFlastupdattime());
        fEntrustHistory.setFcreatetime(fEntrust.getFcreatetime());
        fEntrustHistory.setFagentid(fEntrust.getFagentid());
        return fEntrustHistory;
    }

    public FApiAuth isApiAuth(Integer fuid){
        RedisObject redisObject = redisHelper.getRedisObject(RedisConstant.IS_AUTH_API_KEY + fuid);
        if (redisObject == null){
            return null;
        }
        try {
            return JSONObject.parseObject(redisObject.getExtObject().toString(),FApiAuth.class);
        }catch (Exception e){
            logger.error("反序列化authApi的时候报错："+e.getMessage());
        }
        return null;
    }

    /**
     * 计算买的手续费
     * @param buyApiAuth
     * @param successCount
     * @param systemTradeType
     * @param isBuyFee
     * @return
     */
    public BigDecimal calculateBuyFee(FApiAuth buyApiAuth,BigDecimal successCount,SystemTradeType systemTradeType,boolean isBuyFee){
        BigDecimal buyCountFee ;
        if (buyApiAuth != null){
            if (buyApiAuth.getFopenrate() == 1){
                buyCountFee = MathUtils.mul(successCount, buyApiAuth.getFrate());
            }else{
                buyCountFee = BigDecimal.ZERO;
            }
        }else{
            buyCountFee = isBuyFee ? MathUtils.mul(successCount, systemTradeType.getBuyFee()) : BigDecimal.ZERO;
        }
        return buyCountFee;
    }

    /**
     * 计算卖的手续费
     * @param sellApiAuth
     * @param successAmount
     * @param systemTradeType
     * @param isSellFee
     * @return
     */
    public BigDecimal calculateSellFee(FApiAuth sellApiAuth,BigDecimal successAmount,
                                       SystemTradeType systemTradeType,boolean isSellFee){
        BigDecimal sellAmountFee ;
        if (sellApiAuth != null){
            if (sellApiAuth.getFopenrate() == 1){
                sellAmountFee = MathUtils.mul(successAmount, sellApiAuth.getFrate());
            }else{
                //api的默认的如果没有开启自定的手续费就是默认是0
                sellAmountFee = BigDecimal.ZERO;
            }
        }else {
            sellAmountFee = isSellFee ? MathUtils.mul(successAmount, systemTradeType.getSellFee()) : BigDecimal.ZERO;
        }
        return sellAmountFee;
    }

    public BigDecimal getCommissionFeeByUID(){
        RedisObject redisObject = redisHelper.getRedisObject(RedisConstant.ARGS_KET + "commissionFeeKey");
        if (redisObject == null){
            logger.error("获取佣金手续费比例null");
            return null;
        }
        try {
            return BigDecimal.valueOf(Double.parseDouble( redisObject.getExtObject().toString()));
        }catch (Exception e){
            logger.error("获取佣金手续费失败");
        }
        return null;

    }

    //佣金返还
    public void commissionFee(BigDecimal amountFee,FEntrust fEntrust){
        if (MathUtils.compareTo(amountFee,BigDecimal.ZERO)>0){
            FUser introUser = userMapper.getIntroByUID(fEntrust.getFuid());
            if (introUser == null){
                return;
            }
            Integer introUID =introUser.getFintrouid();
            if (introUID != null && introUID.intValue() > 0){
                //佣金返还的比例
                BigDecimal commissionFee = getCommissionFeeByUID();
                if (commissionFee != null){
                    BigDecimal mul = MathUtils.mul(amountFee, commissionFee);
                    if (MathUtils.compareTo(mul,BigDecimal.ZERO)>0 && MathUtils.compareTo(mul,amountFee)<0){
                        CommissionRecord commissionRecord = new CommissionRecord();
                        commissionRecord.setCreatetime(new Date());
                        commissionRecord.setUid(fEntrust.getFuid());
                        commissionRecord.setIntrouid(introUID);
                        commissionRecord.setUpdatetime(Utils.getTimestamp());
                        commissionRecord.setStatus(CommissionRecordStatusEnum.NORMAL);
                        commissionRecord.setAmount(mul);
                        if (fEntrust.getFtype() == EntrustTypeEnum.SELL.getCode()) {
                            commissionRecord.setRemark("sellcoin total:" + amountFee.toString());
                            commissionRecord.setCoinid(fEntrust.getFsellcoinid());
                        }else{
                            commissionRecord.setRemark("buycoin total:"+amountFee.toString());
                            commissionRecord.setCoinid(fEntrust.getFbuycoinid());
                        }
                        commissionRecord.setCoinname(fEntrust.getFcoinname());
                        if (this.commissionRecordMapper.add(commissionRecord)>0){
                            logger.info("add commission record success");
                        }
                    }
                }
            }
        }
    }

    public int[] getFreeFee() {
        return freeFee;
    }

    public void setFreeFee(int[] freeFee) {
        this.freeFee = freeFee;
    }
}
