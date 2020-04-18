package com.qkwl.service.activity.run;

import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.service.activity.dao.ValidateSmsMapper;
import com.qkwl.service.activity.model.ValidateSmsDO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 短信黑名单
 */
@Component("autoBlackList")
public class AutoBlackList {

    private static final Logger logger = LoggerFactory.getLogger(AutoBlackList.class);

    @Autowired
    private ValidateSmsMapper validateSmsMapper;
    @Autowired
    private RedisHelper redisHelper;

    private static List<ValidateSmsDO> blackList = new ArrayList<>();

    public List<ValidateSmsDO> getBlackList() {
        return blackList;
    }

    //@Scheduled(cron="0/30 * * * * ?")
    public void work() {
        String times = redisHelper.getSystemArgs(ArgsConstant.SMSBLACKLIST);
        if(!StringUtils.isEmpty(times) && StringUtils.isNumeric(times)){
            blackList = validateSmsMapper.selectBlackList(Integer.valueOf(times));
        }else{
            logger.warn("短信黑名单阈值异常！");
        }
    }
}
