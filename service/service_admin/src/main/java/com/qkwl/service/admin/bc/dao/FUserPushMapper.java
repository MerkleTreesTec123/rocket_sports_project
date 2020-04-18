package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.capital.FUserPushDTO;

/**
 * PUSH资产数据操作接口
 * 
 * @author LY
 *
 */
@Mapper
public interface FUserPushMapper {
	
	/**
	 * 分页查询PUSH资产数据
	 * @param map 条件MAP
	 * @return
	 */
	List<FUserPushDTO> selectUserPushList(Map<String, Object> map);
	
	/**
	 * 分页查询PUSH资产数据条数
	 * @param map  条件MAP
	 * @return
	 */
	int selectUserPushCount(Map<String, Object> map);
	
	/**
	 * 平衡统计查询
	 * @param map
	 * @return
	 */
	FUserPushDTO selectUserPushBalance(Map<String, Object> map);
}