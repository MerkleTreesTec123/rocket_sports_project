package com.qkwl.common.framework.limit;

import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisDBConstant;

import org.apache.commons.lang3.StringUtils;

/**
 * 访问限制
 * Created by ZKF on 2017/7/5.
 */
public class LimitHelper {

    // redis数据库索引
    public static final Integer db = RedisDBConstant.REDIS_LIMIT;

    // redis操作工具
    private MemCache memCache;

    // 访问限制前缀
    public static final String prefix = "IpLimit_";

    // 限制时间
    private static final int limitTime = 2 * 60 * 60;

    // 最大错误次数
    private static final int max = 10;

    public void setMemCache(MemCache memCache) {
        this.memCache = memCache;
    }

    /**
     * 更新限制
     */
    public Integer updateLimit(String ip, Integer type) {
        String key = prefix + ip + "_" + type;
        Integer limit = getLimit(key);
        if (limit != null) {
            ++limit;
        } else {
            limit = 1;
        }
        saveLimit(key, limit);
        return max - limit;
    }

    /**
     * 删除限制
     */
    public void deleteLimit(String ip, Integer type) {
        String key = prefix + ip + "_" + type;
        memCache.delete(db, key);
    }

    /**
     * 查看限制
     */
    public Boolean checkLimit(String ip, Integer type) {
        String key = prefix + ip + "_" + type;
        Integer limit = getLimit(key);
        return (limit == null || limit < max);
    }

    /**
     * 查看限制
     */
    public Boolean checkLimit(String ip, Integer type, Integer limitTime) {
        String key = prefix + ip + "_" + type;
        Integer limit = getLimit(key);
        return (limit == null || limit < limitTime);
    }

    /**
     * 保存限制数据
     */
    private void saveLimit(String key, Integer limit) {
        memCache.set(db, key, String.valueOf(limit), limitTime);
    }

    /**
     * 获取缓存数据
     */
    private Integer getLimit(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        String rest = memCache.get(db, key);
        if (StringUtils.isEmpty(rest)) {
            return null;
        }
        return Integer.valueOf(rest);
    }
}
