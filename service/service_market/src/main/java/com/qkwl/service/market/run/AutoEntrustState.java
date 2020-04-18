package com.qkwl.service.market.run;

import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.mq.MQEntrustState;
import com.qkwl.service.market.service.MarketServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component("entrustStateQueue")
public class AutoEntrustState {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AutoEntrustState.class);

	/**
	 * 委单队列
	 */
	private static ConcurrentLinkedQueue<MQEntrustState> mqQueue = new ConcurrentLinkedQueue<>();

	@Autowired
	private MarketServiceImpl marketService;

	

	@PostConstruct
	public void init() {
		Thread thread = new Thread(new Work());
		thread.setName("AutoEntrustState");
		thread.start();
		
	}

	public boolean addEntrustState(MQEntrustState entrustState) {
		return mqQueue.offer(entrustState);
	}
	
	class Work implements Runnable {
        public void run() {
        	while (true) {
				if (mqQueue.isEmpty()) {
					continue;
				}
				MQEntrustState entrustState = mqQueue.poll();
				if (entrustState == null) {
					continue;
				}
				if (marketService.getSkipTrade().contains(entrustState.getTradeId())){
					continue;	
				}		
				try {
					// 处理MQ
    				switch (entrustState.getType()) {
    				case BUY:
    					// 更新买深度
    					marketService.updateBuyDepthMapMQ(true, entrustState.getTradeId(), entrustState.getBuyPrize(), entrustState.getCount());
    					break;
    				case SELL:
    					// 更新卖深度
    					marketService.updateSellDepthMapMQ(true, entrustState.getTradeId(), entrustState.getBuyPrize(), entrustState.getCount());
    					break;
    				case CANCEL:
    					if (entrustState.getCancelType().equals(EntrustTypeEnum.BUY.getCode())) {
    						// 更新买深度
    						marketService.updateBuyDepthMapMQ(false, entrustState.getTradeId(), entrustState.getBuyPrize(), entrustState.getCount());
    					} 
    					else if (entrustState.getCancelType().equals(EntrustTypeEnum.SELL.getCode())) {
    						// 更新卖深度
    						marketService.updateSellDepthMapMQ(false, entrustState.getTradeId(), entrustState.getBuyPrize(), entrustState.getCount());
    					}
    					break;
    				case SUCCEED:
    					// 更新买深度
    					marketService.updateBuyDepthMapMQ(false, entrustState.getTradeId(), entrustState.getBuyPrize(), entrustState.getCount());
    					// 更新卖深度
    					marketService.updateSellDepthMapMQ(false, entrustState.getTradeId(), entrustState.getSellPrize(), entrustState.getCount());
    					// 更新Kline
    					marketService.updateKlineMQ(entrustState.getTradeId(), entrustState.getLast(), entrustState.getCount());
    					// 更新Ticker
    					marketService.updateTickerMQ(entrustState.getTradeId(), entrustState.getLast(), entrustState.getCount());
    					// 更新成功Map
    					marketService.updateSuccessMQ(entrustState.getTradeId(), entrustState.getMatchType(), entrustState.getCount(), entrustState.getLast());
    					break;
					default:
						break;
    				}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("MQ处理失败");
				}
			}
        }
    }
}
