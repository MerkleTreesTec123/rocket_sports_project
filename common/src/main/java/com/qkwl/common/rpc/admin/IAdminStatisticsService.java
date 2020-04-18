package com.qkwl.common.rpc.admin;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 按月统计充提买卖
 * @author ZKF
 */
public interface IAdminStatisticsService {
	
	/**
	 * 人民币充提
	 * 
	 */ 
	BigDecimal sumRWrmb(Integer type, Map<String, Object> map);

	/**
	 * 人民币充提(第三方)
	 *
	 */
	BigDecimal sumOtherRmb(Map<String, Object> map);
	
	/**
	 * 虚拟币充提
	 * 
	 */ 
	BigDecimal sumRWcoin(Integer type, Map<String, Object> map, Integer coinid);
	
	/**
	 * 人民币买卖
	 * 
	 */ 
	BigDecimal sumBSrmb(Integer type, Map<String, Object> map);
	
	/**
	 * 虚拟币买卖
	 * 
	 */ 
	BigDecimal sumBScoin(Integer type, Map<String, Object> map, Integer coinid);

}
