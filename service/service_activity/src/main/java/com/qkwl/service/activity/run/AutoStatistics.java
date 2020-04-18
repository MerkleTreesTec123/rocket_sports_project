package com.qkwl.service.activity.run;

import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.service.activity.dao.ValidateStatisticsMapper;
import com.qkwl.service.activity.model.ValidateStatisticsDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component("autoStatistics")
public class AutoStatistics {

    private static final Logger logger = LoggerFactory.getLogger(AutoStatistics.class);

    @Autowired
    private ValidateStatisticsMapper validateStatisticsMapper;

    /**
     * 实时统计
     */
    private static Map<String, Integer> counterMap = new HashMap<>();

    public void addCounterMap(String key){
        if(counterMap.containsKey(key)){
            counterMap.put(key, Integer.valueOf(counterMap.get(key)) + 1);
        }else{
            counterMap.put(key, 1);
        }
    }

    public void clearCounterMap(String key){
        if(counterMap.containsKey(key)){
            counterMap.put(key, 0);
        }
    }

    @Scheduled(cron="0 * * * * ?")
    public void work() {
        for(PlatformEnum pe : PlatformEnum.values()){
            for(SendTypeEnum se : SendTypeEnum.values()){
                ValidateStatisticsDO vs = new ValidateStatisticsDO();

                String key = RedisConstant.STATISTICS_VALIDATE_KEY + pe.getCode() + "_" + se.getCode();
                if(counterMap.containsKey(key)){
                    int times = Integer.valueOf(counterMap.get(key));
                    if(times > 0){
                        vs.setTimes(times);
                        vs.setPlatform(pe.getCode());
                        vs.setSendType(se.getCode());
                        vs.setGmtCreate(new Date());
                        vs.setGmtModified(new Date());
                        vs.setVersion(0);
                        if(validateStatisticsMapper.insert(vs) <= 0 ){
                            logger.error("AutoStatistics save error: platform = " + pe.getCode() + ", sendType = "+ se.getCode() + " ;" );
                        }else{
                            clearCounterMap(key);
                        }
                    }
                }
            }
        }
    }
}
