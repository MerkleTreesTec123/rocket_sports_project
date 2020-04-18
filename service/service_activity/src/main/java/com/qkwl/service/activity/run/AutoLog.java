package com.qkwl.service.activity.run;

import com.qkwl.service.activity.impl.UserLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 日志定时统计
 *
 * @author TT
 */
@Component("autoLog")
public class AutoLog {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoLog.class);

    @Autowired
    private UserLogService userLogService;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void runSettlement() {
        try {
            // 定时器更新
            userLogService.upCoinJob();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("----> autoclog failed upCoinJob");
        }
        try {
            // 定时器更新
            userLogService.upTradeJob();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("----> autoclog failed upTradeJob");
        }
    }

}
