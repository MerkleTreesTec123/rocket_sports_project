package com.qkwl.service.market.job;

import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.service.market.service.MarketServiceImpl;
import com.qkwl.service.market.util.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * K线生成-1分钟
 * @author jany
 */
@Component("autoMarketLocal")
public class MarketJob {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MarketJob.class);

	@Autowired
	private MarketServiceImpl marketService;
	@Autowired
	private JobUtils jobUtils;

	@Scheduled(cron="0 0/1 * * * ?")  
	public void work() {
		List<SystemTradeType> tradeTypes = jobUtils.getTradeTypeList();
		if(tradeTypes == null){
			return;
		}
		for (SystemTradeType tradeType : tradeTypes) {
			// 禁止交易
			if (!tradeType.getIsShare()) {
				continue;
			}
			// data
			int tradeId = tradeType.getId();
			// 初始化币种数据
			if (tradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
				try {
					// 更新K线数据
					marketService.updateMinuteJob(tradeId);
					// 更新行情数据
					marketService.updateTickerJob(tradeId);
					// 更新三日数据
					marketService.updateThreeTickerJob(tradeId);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("----> automarket normal failed : " + tradeId);
					continue;
				}
			}
		}
	}
}
