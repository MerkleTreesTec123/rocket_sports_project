package com.qkwl.service.coin.util;

import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.framework.redis.RedisHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;


/**
 * 工具类
 *
 * @author jany
 */
@Component("utils")
public class JobUtils {

    @Autowired
    private RedisHelper redisHelper;

    public List<SystemCoinType> getCoinTypeList() {
        List<SystemCoinType> coinTypes = redisHelper.getCoinTypeListSystem();
        return coinTypes;

    }
    public SystemCoinType getCoinTypeShortName(String shortName) {
        return redisHelper.getCoinTypeShortNameSystem(shortName);
    }

    public String getSystemArgs(String key) {
        return redisHelper.getSystemArgs(key);
    }

    public BigDecimal getLastPrice(Integer tradeId) {
        return redisHelper.getLastPrice(tradeId);
    }

}
