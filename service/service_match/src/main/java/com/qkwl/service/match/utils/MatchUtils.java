package com.qkwl.service.match.utils;

import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 工具类
 *
 * @author jany
 */
@Component("utils")
public class MatchUtils {

    /**
     * 默认卷商ID
     */
    public static final int AgentId = 0;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 获取未禁用交易列表
     */
    public List<SystemTradeType> getTradeTypeList() {
        // 获取交易列表
        List<SystemTradeType> fSystemTradeTypes = redisHelper.getTradeTypeList(AgentId);
        return fSystemTradeTypes;
    }

    /**
     * 获取所有交易列表
     */
    public List<SystemTradeType> getAllTradeTypeList() {
        List<SystemTradeType> fSystemTradeTypes = redisHelper.getAllTradeTypeList(AgentId);
        return fSystemTradeTypes;
    }

    public SystemTradeType getTradeType(int tradeId) {
        return redisHelper.getTradeType(tradeId, AgentId);
    }
}
