package com.qkwl.service.activity.run;

import com.qkwl.common.dto.Enum.UserFinancesStateEnum;
import com.qkwl.common.dto.finances.FUserFinancesDTO;
import com.qkwl.service.activity.dao.FUserFinancesMapper;
import com.qkwl.service.activity.impl.UserFinancesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component("autoFinances")
public class AutoFinances {
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AutoFinances.class);
	@Autowired
	FUserFinancesMapper userFinancesMapper;
	@Autowired
	private UserFinancesService userFinancesService;

//	@Scheduled(cron = "0 0/10 * * * ?")
//	public void runSettlement(){
//		List<FUserFinancesDTO> financesList = userFinancesMapper.selectUserFinancesByState(UserFinancesStateEnum.FROZEN.getCode(),new Date());
//		for (FUserFinancesDTO fUserFinances : financesList) {
//			if(fUserFinances.getFstate().equals(UserFinancesStateEnum.FROZEN.getCode()) && fUserFinances.getFupdatetime().before(new Date())){
//				try{
//					userFinancesService.updateUserFinances(fUserFinances);
//				}catch (Exception e) {
//					logger.error("updateUserFinances err id:"+fUserFinances.getFid());
//				}
//			}
//		}
//	}
}
