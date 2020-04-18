package com.qkwl.service.activity.run;

import com.qkwl.service.activity.impl.PriceClockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 价格闹钟定时任务
 *
 */
@Component("autoPriceClock")
public class AutoPriceClock {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoPriceClock.class);

    @Autowired
    private PriceClockService priceClockService;

    //@Scheduled(cron = "0 0/30 * * * ?")
    public void runSettlement() {
//        try {
//            priceClockService.updateWork();
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("---> autoPriceClock failed");
//        }
    }
}
