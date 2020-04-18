package com.qkwl.service.activity.config;

import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.oss.OssHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean 配置文件
 */
@Configuration
public class BeanConfig {
    @Bean
    public OssHelper ossHelper(RedisHelper redisHelper) {
        OssHelper ossHelper = new OssHelper();
        ossHelper.setRedisHelper(redisHelper);
        return ossHelper;
    }
}
