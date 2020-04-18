package com.qkwl.service.user.dao;

import com.qkwl.common.dto.user.CommissionRecord;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户收益
 */
@Mapper
public interface CommissionRecordMapper {
	

	/**
	 * 分页查询数据	
	 * @param map 参数map
	 * @return 查询记录列表
	 */
	List<CommissionRecord> listPage(Map<String, Object> map);

	/**
	 * 分页查询数据总条数	
	 * @param map 参数map
	 * @return 查询记录数
	 */
	int countPage(Map<String, Object> map);

	/**
	 * 查询总共的收益
	 *
	 * @param map
	 * @return
	 */
	BigDecimal totalCommission(Map<String, String> map);

}