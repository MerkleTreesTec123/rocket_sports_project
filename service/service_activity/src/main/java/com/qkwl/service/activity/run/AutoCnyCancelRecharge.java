package com.qkwl.service.activity.run;


import com.qkwl.service.activity.impl.BankCapitalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 自动取消人民币充值
 */
@Component("autoCnyCancelRecharge")
public class AutoCnyCancelRecharge {

    private final static Logger logger = LoggerFactory.getLogger(AutoCnyCancelRecharge.class);

    @Autowired
    private BankCapitalService bankCapitalService;

    //@Scheduled(cron = "0 0/1 * * * ?")
    public void cancelRecharge() {
//        try {
//            bankCapitalService.cancelCnyRecharge();
//        } catch (Exception ex) {
//            logger.error("", ex);
//        }
    }
}
