package com.qkwl.admin.layui.run;

import com.qkwl.common.framework.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("currencyRate")
public class CurrencyRate {

    @Autowired
    RedisHelper redisHelper;


    public void fetchCurrencyRate() {

    }

}
