package com.qkwl.service.match.run;

import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.service.match.services.MatchServiceImpl;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 免手续费
 */
@Component
public class AutoFee {

    private static final String FREE_FEE_KEY = "MatchFreeFee";

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private MatchServiceImpl matchService;

    @PostConstruct
    public void init() {
        Thread thread = new Thread(new Work());
        thread.setName("AutoFee");
        thread.start();
    }

    class Work implements Runnable {
        public void run() {
            while (true) {
                String freeFeeKey = redisHelper.getSystemArgs(FREE_FEE_KEY);
                if (freeFeeKey == null) {
                    continue;
                }
                String[] keys = freeFeeKey.split("#");
                if (keys.length <= 0) {
                    continue;
                }
                try {
                    int[] freeUid = new int[keys.length];
                    for (int i = 0; i < keys.length; i++) {
                        if (!TextUtils.isEmpty(keys[i])) {
                            freeUid[i] = Integer.parseInt(keys[i]);
                        }
                    }
                    matchService.setFreeFee(freeUid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // sleep
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
