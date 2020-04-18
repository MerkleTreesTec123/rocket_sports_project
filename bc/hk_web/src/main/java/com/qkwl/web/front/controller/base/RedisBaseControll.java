package com.qkwl.web.front.controller.base;

import java.math.BigDecimal;

import com.qkwl.web.front.comm.AutoMarket;
import org.springframework.beans.factory.annotation.Autowired;

import com.qkwl.common.controller.BaseController;
import com.qkwl.common.util.Utils;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;

public class RedisBaseControll extends BaseController {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private AutoMarket autoMarket;

    public FUser getCurrentUserInfoByToken() {
        String token = sessionContextUtils.getContextToken("token");
        return redisHelper.getCurrentUserInfoByToken(token);
    }

    public FUser getCurrentUserInfoByApiToken(){
        String token = sessionContextUtils.getContextApiToken();
        return redisHelper.getCurrentUserInfoByToken(token);
    }

    public BigDecimal getLastPrice(int coinid) {
        TickerData tickerData = autoMarket.getTickerData(coinid);
        return tickerData.getLast();
    }

    public void deleteUserInfo() {
        String token = sessionContextUtils.getContextToken("token");
        redisHelper.deleteUserInfo(token);
    }

    public void updateUserInfo(FUser user) {
        String token = sessionContextUtils.getContextToken("token");
        redisHelper.updateUserInfo(token, user);
    }

    public String setRedisData(String token, Object restInfo) {
        String redisKey = Utils.UUID();
        sessionContextUtils.addContextToken(token, redisKey);
        redisHelper.setRedisData(redisKey, restInfo);
        return redisKey;
    }

    public void deletRedisData(String token) {
        String redisKey = sessionContextUtils.getContextToken(token);
        redisHelper.deletRedisData(redisKey);
    }

    public String getRedisData(String token) {
        String redisKey = sessionContextUtils.getContextToken(token);
        return redisHelper.getRedisData(redisKey);
    }
}
