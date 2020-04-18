package com.qkwl.service.activity.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisDBConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.service.activity.model.ValidateAccountDO;
import com.qkwl.service.activity.model.ValidatePlatformDO;
import com.qkwl.service.activity.model.ValidateTemplateDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


/**
 * 工具类
 *
 * @author jany
 */
@Component("utils")
public class JobUtils {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private MemCache memCache;

    public BigDecimal getLastPrice(Integer tradeId) {
        return redisHelper.getLastPrice(tradeId);
    }

    public String getRedisData(int db, String key) {
        return memCache.get(db, key);
    }

    public void setRedisData(int db, String key, String value, int expireSeconds) {
        memCache.set(db, key, value, expireSeconds);
    }

    public ValidatePlatformDO getValidatePlatform(Integer platformId){
        String json = memCache.get(RedisDBConstant.REDIS_DB_DEFAULT,
                RedisConstant.VALIDATE_PLATFORM + platformId);
        if(!StringUtils.isEmpty(json)){
            RedisObject ro = JSON.parseObject(json, RedisObject.class);
            return JSON.parseObject(ro.getExtObject().toString(), ValidatePlatformDO.class);
        }else{
            return null;
        }
    }

    public ValidateAccountDO getValidateAccount(Integer accountId){
        String json = memCache.get(RedisDBConstant.REDIS_DB_DEFAULT,
                RedisConstant.VALIDATE_ACCOUNT + accountId);
        if(!StringUtils.isEmpty(json)){
            RedisObject ro = JSON.parseObject(json, RedisObject.class);
            return JSON.parseObject(ro.getExtObject().toString(), ValidateAccountDO.class);
        }else{
            return null;
        }

    }

    public ValidateTemplateDO getValidateTemplate(Integer platformType, Integer sendType, Integer businessType,
                                                  Integer language){

        String json = memCache.get(RedisDBConstant.REDIS_DB_DEFAULT,
                RedisConstant.VALIDATE_TEMPLATE + platformType + "_" + sendType + "_" + businessType + "_" + language);
        if(!StringUtils.isEmpty(json)){
            RedisObject ro = JSON.parseObject(json, RedisObject.class);
            return JSON.parseObject(ro.getExtObject().toString(), ValidateTemplateDO.class);
        }else{
            return null;
        }
    }

    public String getSystemArgs(String key) {
        String args = memCache.get(RedisConstant.ARGS_KET + key);
        if (StringUtils.isEmpty(args)) {
            return null;
        }
        JSONObject obj = JSON.parseObject(args);
        String value = obj.get("extObject").toString();
        return value;
    }

}
