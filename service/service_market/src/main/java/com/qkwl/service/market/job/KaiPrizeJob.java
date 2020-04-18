package com.qkwl.service.market.job;

import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.service.market.service.MarketServiceImpl;
import com.qkwl.service.market.util.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author work
 * @date 17-5-27
 */
@Component
public class KaiPrizeJob {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(KaiPrizeJob.class);

    @Autowired
    private MarketServiceImpl marketService;
    @Autowired
    private JobUtils jobUtils;

    @Scheduled(cron="0 0 0 * * ?")
    public void work() {
        List<SystemTradeType> tradeTypes = jobUtils.getTradeTypeList();
        if(tradeTypes == null){
            return;
        }
        for (SystemTradeType tradeType : tradeTypes) {
            // data
            int tradeId = tradeType.getId();
            // 初始化币种数据
            if (tradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                try {
                    // 更新开盘价
                    marketService.updateKaiJob(tradeId);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("KaiPrizeJob failed : " + tradeId);
                    continue;
                }
            }
        }
    }
}
