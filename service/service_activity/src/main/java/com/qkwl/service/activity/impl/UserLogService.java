package com.qkwl.service.activity.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.qkwl.service.activity.dao.UserCoinWalletMapper;
import com.qkwl.service.activity.utils.ActivityConstant;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.service.activity.dao.FEntrustHistoryMapper;
import com.qkwl.service.activity.dao.FLogUserActionMapper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.Enum.CapitalOperationTypeEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.daylog.FDayCapitalCoin;
import com.qkwl.common.dto.daylog.FDayCapitalRmb;
import com.qkwl.common.dto.daylog.FDayOperat;
import com.qkwl.common.dto.daylog.FDaySum;
import com.qkwl.common.dto.daylog.FDayTradeCoin;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.framework.redis.RedisHelper;

@Service("userLogService")
public class UserLogService {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private FLogUserActionMapper fLogUserActionMapper;
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private FEntrustHistoryMapper fEntrustHistoryMapper;
    @Autowired
    private LogActionTx logActionTx;

    /**
     * MQ保存
     *
     * @param fLogUserAction 用户日志
     * @throws Exception 执行异常
     */
    public synchronized void insertMQ(FLogUserAction fLogUserAction) throws Exception {
        // 保存日志
        fLogUserAction.setFcreatetime(Utils.getTimestamp());
        fLogUserAction.setFupdatetime(Utils.getTimestamp());
        fLogUserActionMapper.insert(fLogUserAction);
    }

    /**
     * 更新每日数据
     *
     * @param fLogUserAction 用户日志
     * @throws Exception 执行异常
     */
    public synchronized void upDayMQ(FLogUserAction fLogUserAction) throws Exception {
        // 当天时间
        String nowTime = Utils.dateFormatYYYYMMDD(Utils.getTimestamp());
        // 运营 会员登录
        if (fLogUserAction.getFtype().equals(LogUserActionEnum.LOGIN.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFlogin(fdDayOperat.getFlogin() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 会员注册
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.REGISTER.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFregister(fdDayOperat.getFregister() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 会员实名
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.BIND_IDCARD.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFrealname(fdDayOperat.getFrealname() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 短信
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.SEND_SMS.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFsms(fdDayOperat.getFsms() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 邮箱
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.SEND_MAIL.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFmail(fdDayOperat.getFmail() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 购买VIP6
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.BUY_VIP6.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFvip6(fdDayOperat.getFvip6() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 充值码
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.USE_CODE.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFcode(fdDayOperat.getFcode() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 积分
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.SCORE.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFscore(fdDayOperat.getFscore() + fLogUserAction.getFdata().intValue());
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 提交提问
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.QUESTION_SUBMIT.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFsubmitquestion(fdDayOperat.getFsubmitquestion() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 运营 回复提问
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.QUESTION_REPLY.getCode())) {
            FDayOperat fdDayOperat = logActionTx.insertfDayOperat(nowTime, fLogUserAction.getFagentid());
            fdDayOperat.setFreplyquestion(fdDayOperat.getFreplyquestion() + 1);
            fdDayOperat.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayOperat(fdDayOperat);
        }
        // 资金 RMB充值成功
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.RMB_RECHARGE.getCode())) {
            FDayCapitalRmb fdCapitalRmb = logActionTx.insertFDayCapitalRmb(nowTime, fLogUserAction.getFagentid());
            if (fLogUserAction.getFdatatype() == CapitalOperationTypeEnum.ALIPAY_INT) { // 支付宝
                fdCapitalRmb.setFzfb(MathUtils.add(fdCapitalRmb.getFzfb(), fLogUserAction.getFdata()));
            } else if (fLogUserAction.getFdatatype() == CapitalOperationTypeEnum.WECHAT_INT) { // 微信
                fdCapitalRmb.setFwx(MathUtils.add(fdCapitalRmb.getFwx(), fLogUserAction.getFdata()));
            } else if (fLogUserAction.getFdatatype() == CapitalOperationTypeEnum.ZSJF_IN) { // 丰付
                fdCapitalRmb.setFsuma(MathUtils.add(fdCapitalRmb.getFsuma(), fLogUserAction.getFdata()));
            } else if (fLogUserAction.getFdatatype() == CapitalOperationTypeEnum.RMB_IN) { // 银行
                fdCapitalRmb.setFbank(MathUtils.add(fdCapitalRmb.getFbank(), fLogUserAction.getFdata()));
            }
            fdCapitalRmb.setFupdatetime(Utils.getTimestamp());
            logActionTx.updateFDayCapitalRmb(fdCapitalRmb);
        }
        // 资金 RMB提现成功
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.RMB_WITHDRAW.getCode())) {
            FDayCapitalRmb fdCapitalRmb = logActionTx.insertFDayCapitalRmb(nowTime, fLogUserAction.getFagentid());
            if (fLogUserAction.getFdatatype() == CapitalOperationTypeEnum.RMB_OUT) { // 银行
                fdCapitalRmb.setFwithdraw(MathUtils.add(fdCapitalRmb.getFwithdraw(), MathUtils.sub(fLogUserAction.getFdata(), fLogUserAction.getFfees())));
                fdCapitalRmb.setFwithdrawwait(MathUtils.sub(fdCapitalRmb.getFwithdrawwait(), MathUtils.sub(fLogUserAction.getFdata(), fLogUserAction.getFfees())));
                fdCapitalRmb.setFfees(MathUtils.add(fdCapitalRmb.getFfees(), fLogUserAction.getFfees()));
            }
            fdCapitalRmb.setFupdatetime(Utils.getTimestamp());
            logActionTx.updateFDayCapitalRmb(fdCapitalRmb);
        }
        // 资金 RMB提现成功-自动提现
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.RMB_WITHDRAW_ONLINE.getCode())) {
            FDayCapitalRmb fdCapitalRmb = logActionTx.insertFDayCapitalRmb(nowTime, fLogUserAction.getFagentid());
            if (fLogUserAction.getFdatatype() == CapitalOperationTypeEnum.RMB_OUT) { // 银行
                fdCapitalRmb.setFwithdrawother(MathUtils.add(fdCapitalRmb.getFwithdrawother(), MathUtils.sub(fLogUserAction.getFdata(), fLogUserAction.getFfees())));
                fdCapitalRmb.setFwithdrawwait(MathUtils.sub(fdCapitalRmb.getFwithdrawwait(), MathUtils.sub(fLogUserAction.getFdata(), fLogUserAction.getFfees())));
                fdCapitalRmb.setFfees(MathUtils.add(fdCapitalRmb.getFfees(), fLogUserAction.getFfees()));
            }
            fdCapitalRmb.setFupdatetime(Utils.getTimestamp());
            logActionTx.updateFDayCapitalRmb(fdCapitalRmb);
        }
        // 资金 RMB等待提现
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.RMB_WITHDRAW_WAIT.getCode())) {
            FDayCapitalRmb fdCapitalRmb = logActionTx.insertFDayCapitalRmb(nowTime, fLogUserAction.getFagentid());
            fdCapitalRmb.setFwithdrawwait(MathUtils.add(fdCapitalRmb.getFwithdrawwait(), fLogUserAction.getFdata()));
            fdCapitalRmb.setFupdatetime(Utils.getTimestamp());
            logActionTx.updateFDayCapitalRmb(fdCapitalRmb);
        }
        // 资金 RMB撤销提现
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.RMB_WITHDRAW_CANCEL.getCode())) {
            FDayCapitalRmb fdCapitalRmb = logActionTx.insertFDayCapitalRmb(nowTime, fLogUserAction.getFagentid());
            fdCapitalRmb.setFwithdrawwait(MathUtils.sub(fdCapitalRmb.getFwithdrawwait(), fLogUserAction.getFdata()));
            fdCapitalRmb.setFupdatetime(Utils.getTimestamp());
            logActionTx.updateFDayCapitalRmb(fdCapitalRmb);
        }
        // 资金 虚拟币充值
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.COIN_RECHARGE.getCode())) {
            FDayCapitalCoin fdCapitalCoin = logActionTx.insertfDayCapitalCoin(fLogUserAction.getFdatatype(), nowTime, fLogUserAction.getFagentid());
            fdCapitalCoin.setFrecharge(MathUtils.add(fdCapitalCoin.getFrecharge(), fLogUserAction.getFdata()));
            fdCapitalCoin.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayCapitalCoin(fdCapitalCoin);
        }
        // 资金 虚拟币提现
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.COIN_WITHDRAW.getCode())) {
            FDayCapitalCoin fdCapitalCoin = logActionTx.insertfDayCapitalCoin(fLogUserAction.getFdatatype(), nowTime, fLogUserAction.getFagentid());
            BigDecimal withdraw = MathUtils.sub(MathUtils.sub(fLogUserAction.getFdata(), fLogUserAction.getFfees()), fLogUserAction.getFbtcfees());
            fdCapitalCoin.setFwithdraw(MathUtils.add(fdCapitalCoin.getFwithdraw(), withdraw));
            fdCapitalCoin.setFwithdrawwait(MathUtils.sub(fdCapitalCoin.getFwithdrawwait(), withdraw));
            fdCapitalCoin.setFfees(MathUtils.add(fdCapitalCoin.getFfees(), fLogUserAction.getFfees()));
            fdCapitalCoin.setFfees(MathUtils.add(fdCapitalCoin.getFfees(), fLogUserAction.getFbtcfees()));
            fdCapitalCoin.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayCapitalCoin(fdCapitalCoin);
        }
        // 资金 虚拟币等待提现
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.COIN_WITHDRAW_WAIT.getCode())) {
            FDayCapitalCoin fdCapitalCoin = logActionTx.insertfDayCapitalCoin(fLogUserAction.getFdatatype(), nowTime, fLogUserAction.getFagentid());
            fdCapitalCoin.setFwithdrawwait(MathUtils.add(fdCapitalCoin.getFwithdrawwait(), fLogUserAction.getFdata()));
            fdCapitalCoin.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayCapitalCoin(fdCapitalCoin);
        }
        // 资金 虚拟币撤销提现
        else if (fLogUserAction.getFtype().equals(LogUserActionEnum.COIN_WITHDRAW_CANCEL.getCode())) {
            FDayCapitalCoin fdCapitalCoin = logActionTx.insertfDayCapitalCoin(fLogUserAction.getFdatatype(), nowTime, fLogUserAction.getFagentid());
            fdCapitalCoin.setFwithdrawwait(MathUtils.sub(fdCapitalCoin.getFwithdrawwait(), fLogUserAction.getFdata()));
            fdCapitalCoin.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayCapitalCoin(fdCapitalCoin);
        }
    }

    /**
     * 定时器更新币种存量
     */
    public void upCoinJob() {
        // 币种
        List<SystemCoinType> coinTypes = redisHelper.getCoinTypeListSystem();
        for (SystemCoinType coinType : coinTypes) {
            // 当前时间
            String nowTime = Utils.dateFormatYYYYMMDD(Utils.getTimestamp());
            // 更新虚拟币存量
            FDaySum fDaySum = logActionTx.insertfDaySum(coinType.getId(), nowTime);
            UserCoinWallet sumWallet = userCoinWalletMapper.selectSum(coinType.getId());
            fDaySum.setFtotle(sumWallet.getTotal());
            fDaySum.setFrozen(sumWallet.getFrozen());
            fDaySum.setFcreatetime(Utils.getTimestamp());
            logActionTx.updatefDaySum(fDaySum);
        }
    }
    /**
     * 定时器更新交易统计
     */
    public void upTradeJob() {
        // 币种
        List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(ActivityConstant.BCAgentId);
        for (SystemTradeType tradeType : tradeTypes) {
            // 当前时间
            String nowTime = Utils.dateFormatYYYYMMDD(Utils.getCurTimestamp(-1));
            int tradeId = tradeType.getId();
            // 更新买卖单
            FDayTradeCoin fDayTradeCoin = logActionTx.insertfDayTradeCoin(tradeId, nowTime, tradeType.getAgentId());
            Map<String, Object> entrustTotal = fEntrustHistoryMapper.selectTotal(tradeId, nowTime);
            Map<String, Object> entrustPerson = fEntrustHistoryMapper.selectPerson(tradeId, nowTime);
            fDayTradeCoin.setFbuy(BigDecimal.valueOf(Double.parseDouble(entrustTotal != null ? entrustTotal.get("sumBuy").toString() : "0")));
            fDayTradeCoin.setFsell(BigDecimal.valueOf(Double.parseDouble(entrustTotal != null ? entrustTotal.get("sumSell").toString() : "0")));
            fDayTradeCoin.setFbuyfees(BigDecimal.valueOf(Double.parseDouble(entrustTotal != null ? entrustTotal.get("feesBuy").toString() : "0")));
            fDayTradeCoin.setFsellfees(BigDecimal.valueOf(Double.parseDouble(entrustTotal != null ? entrustTotal.get("feesSell").toString() : "0")));
            fDayTradeCoin.setFbuyentrust(Integer.parseInt(entrustTotal != null ? entrustTotal.get("buyCount").toString() : "0"));
            fDayTradeCoin.setFsellentrust(Integer.parseInt(entrustTotal != null ? entrustTotal.get("sellCount").toString() : "0"));
            fDayTradeCoin.setFbuyperson(Integer.parseInt(entrustPerson != null ? entrustPerson.get("buyCount").toString() : "0"));
            fDayTradeCoin.setFsellperson(Integer.parseInt(entrustPerson != null ? entrustPerson.get("sellCount").toString() : "0"));
            fDayTradeCoin.setFupdatetime(Utils.getTimestamp());
            logActionTx.updatefDayTradeCoin(fDayTradeCoin);
        }
    }
}
