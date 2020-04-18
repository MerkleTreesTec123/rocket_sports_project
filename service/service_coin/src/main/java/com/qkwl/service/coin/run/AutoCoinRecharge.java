package com.qkwl.service.coin.run;

import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.service.coin.service.CoinService;
import com.qkwl.service.coin.util.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component("autoCoinRecharge")
public class AutoCoinRecharge {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoCoinRecharge.class);

    @Autowired
    private CoinService coinService;
    @Autowired
    private JobUtils jobUtils;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        Thread thread = new Thread(new Work());
        thread.setName("AutoCoinRecharge");
        thread.start();
    }

    /**
     * Work
     */
    class Work implements Runnable {
        public void run() {
            while (true) {
                List<SystemCoinType> coinTypes = jobUtils.getCoinTypeList();
                if (coinTypes == null) {
                    continue;
                }
                for (SystemCoinType coinType : coinTypes) {
                    if (coinType == null) {
                        continue;
                    }
                    if (!coinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
                        continue;
                    }
                    if (coinType.getStatus().equals(SystemCoinStatusEnum.ABNORMAL.getCode()) || !coinType.getIsRecharge()) {
                        continue;
                    }
                    if (coinType.getCoinType().equals(SystemCoinSortEnum.ETH.getCode()) ||
                        coinType.getCoinType().equals(SystemCoinSortEnum.ETC.getCode())) {
                        continue;
                    }
                    try {
                        coinService.updateRecharge(coinType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("coin recharge failed");
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
