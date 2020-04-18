package com.qkwl.service.activity.service;

import com.qkwl.common.dto.Enum.*;
import com.qkwl.common.dto.log.FLogUserScore;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.match.MathUtils;
import com.qkwl.service.activity.dao.*;
import com.qkwl.service.activity.utils.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 用户积分
 * @author TT
 */
@Service("userScoreService")
public class UserScoreService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(UserScoreService.class);

    @Autowired
    private FUserScoreMapper fUserScoreMapper;
    @Autowired
    private FUserMapper fUserMapper;
    @Autowired
    private FWalletCapitalOperationMapper walletCapitalOperationMapper;
    @Autowired
    private FVirtualCapitalOperationMapper virtualCapitalOperationMapper;
    @Autowired
    private FLogUserScoreMapper logUserScoreMappper;
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    @Autowired
    private JobUtils jobUtils;

    /**
     * 用户积分修改、积分日志、更新运营数据
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUserScore(FLogUserScore logUserScore) throws Exception {
        int fuid = logUserScore.getFuid();
        int ftype = logUserScore.getFtype();
        BigDecimal amount = logUserScore.getAmount();
        //根据规则获取积分实体
        FUserScore score = this.fUserScoreMapper.selectByUid(fuid);
        Long curScore = score.getFscore();
        score = getUserScoreByRule(score, ftype, amount);
        //判断新增积分是否为0
        if (score.getFaddscore().equals(0)) {
            return;
        }
        // 更新用户积分
        int result = this.fUserScoreMapper.updateByPrimaryKey(score);
        if (result <= 0) {
            throw new Exception("更新用户积分失败！");
        }
        logUserScore.setFscore(score.getFaddscore());
        logUserScore.setFremark(logUserScore.getFremark() + "[修改前：" + curScore + "，修改后：" + score.getFscore() + "]");
        logUserScore.setFcreatetime(new Date());
        // 新增用户积分流水日志
        int i = logUserScoreMappper.insert(logUserScore);
        if (i <= 0) {
            throw new Exception("新增用户积分流水日志失败！");
        }
    }

    /**
     * 积分规则处理
     * @param score  用户积分实体
     * @param ftype  积分类型
     * @param amount 参与总量
     * @return 积分实体
     */
    private FUserScore getUserScoreByRule(FUserScore score, Integer ftype, BigDecimal amount) {
        //登录
        if (ftype.equals(ScoreTypeEnum.LOGIN.getCode())) {
            score.setFaddscore(ScoreTypeEnum.LOGIN.getScore());
            score.setFscore(score.getFscore() + ScoreTypeEnum.LOGIN.getScore());
        }
        //充值（虚拟币需要在发送队列时获取实时价格换算）
        if (ftype.equals(ScoreTypeEnum.RECHARGE.getCode())) {
            int rechargeScore = MathUtils.div(amount, new BigDecimal(ScoreTypeEnum.RECHARGE.getScore())).intValue();
            score.setFaddscore(rechargeScore);
            score.setFscore(score.getFscore() + rechargeScore);
        }
        //交易（虚拟币需要在发送队列时获取实时价格换算）
        if (ftype.equals(ScoreTypeEnum.TRADING.getCode())) {
            int tradeScore = MathUtils.div(amount, new BigDecimal(ScoreTypeEnum.TRADING.getScore())).intValue();
            score.setFaddscore(tradeScore);
            score.setFscore(score.getFscore() + tradeScore);
        }
        //实名
        if (ftype.equals(ScoreTypeEnum.REALNAME.getCode())) {
            score.setFaddscore(ScoreTypeEnum.REALNAME.getScore());
            score.setFscore(score.getFscore() + ScoreTypeEnum.REALNAME.getScore());
        }
        //手机
        if (ftype.equals(ScoreTypeEnum.PHONE.getCode())) {
            score.setFaddscore(ScoreTypeEnum.PHONE.getScore());
            score.setFscore(score.getFscore() + ScoreTypeEnum.PHONE.getScore());
        }
        //邮箱
        if (ftype.equals(ScoreTypeEnum.EMAIL.getCode())) {
            score.setFaddscore(ScoreTypeEnum.EMAIL.getScore());
            score.setFscore(score.getFscore() + ScoreTypeEnum.EMAIL.getScore());
        }
        //谷歌
        if (ftype.equals(ScoreTypeEnum.GOOGLE.getCode())) {
            score.setFaddscore(ScoreTypeEnum.GOOGLE.getScore());
            score.setFscore(score.getFscore() + ScoreTypeEnum.GOOGLE.getScore());
        }
        //首次充值
        if (ftype.equals(ScoreTypeEnum.FIRSTCHARGE.getCode())) {
            score.setFaddscore(ScoreTypeEnum.FIRSTCHARGE.getScore());
            score.setFscore(score.getFscore() + ScoreTypeEnum.FIRSTCHARGE.getScore());
        }
        //净资产(定时任务)
        if (ftype.equals(ScoreTypeEnum.ASSETLIMIT.getCode())) {
            int assetsScore = MathUtils.div(amount, new BigDecimal(ScoreTypeEnum.ASSETLIMIT.getScore())).intValue();
            score.setFaddscore(assetsScore);
            score.setFscore(score.getFscore() + assetsScore);
            //定时任务执行时，清除当天交易积分
            score.setFtradingqty(0);
        }
        // 活动
        if (ftype.equals(ScoreTypeEnum.ACTIVITY.getCode())) {
            int activityScore = (int) amount.doubleValue();
            score.setFaddscore(activityScore);
            score.setFscore(score.getFscore() + activityScore);
        }

        //判断vip6是否到期
        if (score.getFleveltime() != null) {
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(new Date());
            rightNow.add(Calendar.YEAR, -1);// 日期减1年
            if (rightNow.getTime().after(score.getFleveltime())) {
                score.setFleveltime(new Date());
                score.setFlevel(0);
            }
        }

        //判断积分等级
        int level = 0;
        long curscore = score.getFscore();
        if (curscore >= 1000000) {
            level = 5;
        } else if (curscore >= 500000) {
            level = 4;
        } else if (curscore >= 300000) {
            level = 3;
        } else if (curscore >= 100000) {
            level = 2;
        } else if (curscore >= 10000) {
            level = 1;
        }
        //判断等级是否发生改变
        if (score.getFlevel() < level) {
            score.setFlevel(level);
        }
        return score;
    }

    /**
     * 获取所有用户
     */
    public List<FUser> getAllUser() {
        return fUserMapper.selectAll();
    }

    /**
     * 用户净资产积分处理
     * @throws Exception 积分处理异常
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUserAssetsScore(FUser user, Map<Integer, Integer> coinIdToTradeId) throws Exception {
        List<UserCoinWallet> userCoinWallets = userCoinWalletMapper.selectUid(user.getFid());
        if (userCoinWallets == null || userCoinWallets.size() <= 0) {
            logger.error("updateUserAssetsScore fuserVirtualWallets is null");
            return;
        }
        BigDecimal netAsset = getNetAsset(userCoinWallets, coinIdToTradeId);
        if (netAsset.compareTo(BigDecimal.ZERO) > 0) {
            FLogUserScore logUserScore = new FLogUserScore(user.getFid(), netAsset, ScoreTypeEnum.ASSETLIMIT.getCode(), "折合净资产：" + MathUtils.toScaleNum(netAsset, MathUtils.DEF_CNY_SCALE) + "元");
            //根据规则获取积分实体
            FUserScore score = this.fUserScoreMapper.selectByUid(user.getFid());
            Long curScore = score.getFscore();
            score = getUserScoreByRule(score, logUserScore.getFtype(), logUserScore.getAmount());
            //判断新增积分是否为0
            if (!score.getFaddscore().equals(0)) {
                // 更新用户积分
                int result = this.fUserScoreMapper.updateByPrimaryKey(score);
                if (result <= 0) {
                    throw new Exception("更新用户积分失败！");
                }

                logUserScore.setFscore(score.getFaddscore());
                logUserScore.setFremark(logUserScore.getFremark() + "[修改前：" + curScore + "，修改后：" + score.getFscore() + "]");
                logUserScore.setFcreatetime(new Date());
                // 新增用户积分流水日志
                int i = logUserScoreMappper.insert(logUserScore);
                if (i <= 0) {
                    throw new Exception("新增用户积分流水日志失败！");
                }
            }
        }
    }

    /**
     * 获取净资产
     *
     * @param userCoinWallets 钱包列表
     */
    private BigDecimal getNetAsset(List<UserCoinWallet> userCoinWallets, Map<Integer, Integer> coinIdToTradeId) {
        if (userCoinWallets == null || userCoinWallets.size() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal last, vtotal;
        for (UserCoinWallet userCoinWallet : userCoinWallets) {
            Object tradeId = coinIdToTradeId.get(userCoinWallet.getCoinId());
            vtotal = MathUtils.add(userCoinWallet.getTotal(), userCoinWallet.getFrozen());
            if (tradeId != null){
                last = jobUtils.getLastPrice(Integer.valueOf(tradeId.toString()));
                vtotal = MathUtils.mul(vtotal, last);
            }
            total = MathUtils.add(vtotal, total);
        }
        return total;
    }

    /**
     * 是否首充
     * @param fuid 用户ID
     */
    public boolean isFirstCharge(int fuid) {
        Map<String, Object> map = new HashMap<>();
        map.put("fuid", fuid);
        map.put("finouttype", CapitalOperationInOutTypeEnum.IN.getCode());
        map.put("fstatus", CapitalOperationInStatus.Come);
        int countCny = walletCapitalOperationMapper.countWalletCapitalOperation(map);

        map.clear();
        map.put("fuid", fuid);
        map.put("ftype", VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
        map.put("fstatus", VirtualCapitalOperationInStatusEnum.SUCCESS);
        int countCoin = virtualCapitalOperationMapper.countVirtualCapitalOperation(map);

        return countCny + countCoin <= 0;
    }
}
