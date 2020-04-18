package com.qkwl.service.coin.config;

import com.qkwl.common.framework.redis.RedisHelper;
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
}
