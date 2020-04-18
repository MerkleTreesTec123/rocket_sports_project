package com.qkwl.service.activity.run;

import com.qkwl.service.activity.impl.ActivityRecordService;
import com.qkwl.service.activity.impl.UserLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 日志定时统计
 *
 */
@Component("autoActivityRecord")
public class AutoActivityRecord {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoActivityRecord.class);

    @Autowired
    private ActivityRecordService activityRecordService;

    @Scheduled(cron = "0 0 0 1/1 * ? ")
    public void runSettlement() {
        try {
            // 定时器更新
            activityRecordService.updateWork();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("----> AutoActivityRecord failed updateWork");
        }
    }

}
