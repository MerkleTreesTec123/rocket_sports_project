package com.qkwl.service.admin.bc.dao;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 按月统计
 * @author ZKF
 */
@Mapper
public interface FStatisticsMapper {
	
	/**
	 * 人民币充提
	 * @param map 参数
	 * @return 统计数值
	 */
	BigDecimal sumRWrmb(Map<String, Object> map);

	/**
	 * 人民币充提(第三方)
	 * @param map 参数
	 * @return 统计数值
	 */
	BigDecimal sumOtherRmb(Map<String, Object> map);
	
	/**
	 * 虚拟币充提
	 * @param map 参数
	 * @return 统计数值
	 */
	BigDecimal sumRWcoin(Map<String, Object> map);
	
	/**
	 * 人民币买卖
	 * @param map 参数
	 * @return 统计数值
	 */
	BigDecimal sumBSrmb(Map<String, Object> map);
	
	/**
	 * 虚拟币买卖
	 * @param map 参数
	 * @return 统计数值
	 */
	BigDecimal sumBScoin(Map<String, Object> map);

}
