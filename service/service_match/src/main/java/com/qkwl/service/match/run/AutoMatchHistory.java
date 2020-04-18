package com.qkwl.service.match.run;

import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.service.match.services.MatchServiceImpl;
import com.qkwl.service.match.utils.MatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component("autoMatchHistory")
public class AutoMatchHistory {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AutoMatchHistory.class);
	
	@Autowired
	private MatchUtils matchUtils;
	@Autowired
	private MatchServiceImpl matchService;

	@PostConstruct
	public void init() {
		Thread thread = new Thread(new Work());
		thread.setName("AutoMatchHistory");
		thread.start();
	}

	class Work implements Runnable {
		public void run() {
			while (true) {
				// 获取币种列表
				List<SystemTradeType> systemTradeTypes = matchUtils.getAllTradeTypeList();
				if (systemTradeTypes == null) {
					continue;
				}
				// 遍历虚拟币列表
				for (SystemTradeType systemTradeType : systemTradeTypes) {
					if (systemTradeType == null) {
						continue;
					}
					// 火币撮合跳过
					if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
						continue;
					}
					// 币种ID
					int tradeId = systemTradeType.getId();
					// 强事务锁单更新
					List<FEntrust> fEntrusts = matchService.getHistoryEntrust(tradeId);
					for (FEntrust fEntrust : fEntrusts) {
						try {
							matchService.updateMatchHistory(fEntrust);
						} catch (Exception e) {
							logger.error("AutoMatchHistory failed : {}", fEntrust.getFtradeid());
							e.printStackTrace();
							continue;
						}
					}
				}
			}
		}
	}
}
