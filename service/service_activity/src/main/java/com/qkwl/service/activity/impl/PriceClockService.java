package com.qkwl.service.activity.impl;

import com.qkwl.service.activity.dao.FUserMapper;
import com.qkwl.service.activity.dao.FUserPriceclockMapper;
import com.qkwl.service.activity.utils.ActivityConstant;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserPriceclock;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("priceClockService")
public class PriceClockService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(PriceClockService.class);

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private FUserPriceclockMapper userPriceclockMapper;
    @Autowired
    private FUserMapper userMapper;
    @Autowired
    private ValidateHelper validateHelper;

    public void updateWork() {
        // 1. 查询所有可用的币种
        List<SystemTradeType> systemTradeTypes = redisHelper.getTradeTypeList(ActivityConstant.BCAgentId);
        for (SystemTradeType systemTradeType : systemTradeTypes) {
            if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
                continue;
            }
            if (!systemTradeType.getIsShare()) {
                continue;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Time tm = Time.valueOf(sdf.format(new Date()));
            boolean isopen = systemTradeType.getOpenTime() != null && tm.before(systemTradeType.getOpenTime());
            boolean isstop = systemTradeType.getStopTime() != null && tm.after(systemTradeType.getStopTime());
            if (systemTradeType.getIsStop() || isopen || isstop) {
                continue;
            }
            // 2. 获取币种当前实时价格
            BigDecimal lastPrice = redisHelper.getLastPrice(systemTradeType.getId());
            // 3. 根据币种查询所有开启的价格闹钟
            FUserPriceclock pclock = new FUserPriceclock();
            pclock.setFtradeid(systemTradeType.getId());
            pclock.setFisopen(true);
            pclock.setFlastprice(lastPrice);
            List<FUserPriceclock> clockList = userPriceclockMapper.selectByCoin(pclock);

            if (clockList == null || clockList.size() <= 0) {
                continue;
            }

            // 4. 比较闹钟最高最低价是否触发，一旦触发，向用户发送短信
            for (FUserPriceclock clock : clockList) {
                try {
                    if (lastPrice.compareTo(clock.getFmaxprice()) >= 0) {
                        sendParamMsg(systemTradeType, clock, clock.getFmaxprice(), lastPrice, true);
                    }
                    if (lastPrice.compareTo(clock.getFminprice()) <= 0) {
                        sendParamMsg(systemTradeType, clock, clock.getFminprice(), lastPrice, false);
                    }
                } catch (Exception e) {
                    logger.error("PriceClockService sendSMS err");
                }
            }
        }
    }

    /**
     * 发送价格闹钟短信
     */
    private void sendParamMsg(SystemTradeType tradeType, FUserPriceclock clock, BigDecimal price, BigDecimal lastprice, Boolean type) throws Exception {
        FUser user = userMapper.selectByPrimaryKey(clock.getFuid());

        Map<Integer,String> shortNameMap = redisHelper.getCoinTypeShortNameMap();

        String coinShortName = shortNameMap.get(tradeType.getSellCoinId()) + "&" + shortNameMap.get(tradeType.getBuyCoinId());
        String opType = type ? "高于" : "低于";
//        if (user.getFistelephonebind() != null && user.getFistelephonebind()) {
//            validateHelper.smsPriceClock(user, SendTypeEnum.SMS_TEXT.getCode(), PlatformEnum.BC.getCode(), BusinessTypeEnum.SMS_PRICE_CLOCK.getCode(),
//                    LocaleEnum.ZH_CN.getCode(), coinShortName, opType, lastprice, price);
//
//        }
        if (user.getFismailbind() != null && user.getFismailbind()) {
            validateHelper.mailSendPriceClock(user.getFemail(), PlatformEnum.BC, LocaleEnum.ZH_CN,
                    BusinessTypeEnum.EMAIL_PRICE_CLOCK, coinShortName, opType, lastprice, price, user.getFid());
        }
    }
}
