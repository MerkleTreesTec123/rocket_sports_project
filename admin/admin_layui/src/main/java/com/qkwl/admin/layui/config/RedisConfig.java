package com.qkwl.admin.layui.config;

import com.qkwl.common.framework.limit.LimitHelper;
import com.qkwl.common.framework.pre.PreValidationHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.properties.RedisProperties;
import com.qkwl.common.redis.JedisPoolConfig;
import com.qkwl.common.redis.MemCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * @Author jany
 * @Date 17-4-20
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    @Bean
    public JedisPoolConfig jedisPoolConfig(RedisProperties redisProperties) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisProperties.getPool().getMaxTotal());
        jedisPoolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWaitMillis());
        jedisPoolConfig.setTestWhileIdle(redisProperties.getPool().isTestWhileIdle());
        jedisPoolConfig.setTestOnBorrow(redisProperties.getPool().isTestOnBorrow());
        jedisPoolConfig.setTestOnReturn(redisProperties.getPool().isTestOnReturn());
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jdisPool(JedisPoolConfig jedisPoolConfig, RedisProperties redisProperties) {
        return new JedisPool(jedisPoolConfig, redisProperties.getHost(), redisProperties.getPort(),
                redisProperties.getTimeout(), redisProperties.getPassword());
    }

    @Bean
    public MemCache memCache(JedisPool jedisPool) {
        MemCache memCache = new MemCache();
        memCache.setJedisPool(jedisPool);
        return memCache;
    }

    @Bean
    public RedisHelper redisHelper(MemCache memCache) {
        RedisHelper redisHelper = new RedisHelper();
        redisHelper.setMemCache(memCache);
        return redisHelper;
    }

    @Bean
    public LimitHelper limitHelper(MemCache memCache){
        LimitHelper limitHelper = new LimitHelper();
        limitHelper.setMemCache(memCache);
        return limitHelper;
    }

    @Bean
    public PreValidationHelper preValidationHelper(){
        return new PreValidationHelper();
    }

    @Bean
    public ValidationCheckHelper validationCheckHelper(LimitHelper limitHelper, ValidateHelper validateHelper){
        ValidationCheckHelper validationCheckHelper = new ValidationCheckHelper();
        validationCheckHelper.setLimitHelper(limitHelper);
        validationCheckHelper.setValidateHelper(validateHelper);
        return validationCheckHelper;
    }
}
