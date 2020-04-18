package com.qkwl.service.match.run;

import java.util.List;

import com.qkwl.common.dto.Enum.MatchTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.service.match.utils.MatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.service.match.services.MatchServiceImpl;

import javax.annotation.PostConstruct;

@Component("autoMatch")
public class AutoMatch {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoMatch.class);

    @Autowired
    private MatchUtils matchUtils;
    @Autowired
    private MatchServiceImpl matchService;

    @PostConstruct
    public void init() {
        Thread thread = new Thread(new Work());
        thread.setName("AutoMatch");
        thread.start();
    }

    class Work implements Runnable {
        public void run() {
            while (true) {
                //isNoTrade = true;
                // 获取币种列表
                List<SystemTradeType> systemTradeTypes = matchUtils.getTradeTypeList();
                if (systemTradeTypes == null) {
                    continue;
                }
                // 遍历虚拟币列表
                for (SystemTradeType systemTradeType : systemTradeTypes) {
                    if (systemTradeType == null) {
                        continue;
                    }
                    // 非平台撮合 跳过
                    if (!systemTradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                        continue;
                    }
                    // 币种ID
                    int tradeId = systemTradeType.getId();
                    // 获取排序买卖单
                    List<FEntrust> buyEntrusts = matchService.getBuyEntrusts(tradeId);
                    List<FEntrust> sellEntrusts = matchService.getSellEntrusts(tradeId);
                    if (buyEntrusts == null || buyEntrusts.size() <= 0
                            || sellEntrusts == null || sellEntrusts.size() <= 0) {
                        continue;
                    }
                    if (buyEntrusts.get(0).getFprize().compareTo(sellEntrusts.get(0).getFprize()) < 0) {
                        continue;
                    }

                    for (FEntrust buyEntrustWait : buyEntrusts) {
                        for (FEntrust sellEntrustWait : sellEntrusts) {
                            if (buyEntrustWait == null || sellEntrustWait == null 
                            || buyEntrustWait.getFmatchtype().equals(MatchTypeEnum.HUOBI.getCode())
                            || sellEntrustWait.getFmatchtype().equals(MatchTypeEnum.HUOBI.getCode())){
                                continue;
                            } 
                            try {
                                matchService.updateMatch(systemTradeType, buyEntrustWait, sellEntrustWait);
                               // logger.info("buyPrice = "+buyEntrustWait.getFprize() +" , sellPrice = "+sellEntrustWait.getFprize() + ",result = "+result);
                            } catch (Exception e) {
                                logger.error("math err : {}_{}", buyEntrustWait.getFid(), sellEntrustWait.getFid());
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                }

            }
        }
    }
}
