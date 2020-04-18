package com.qkwl.service.market.mapper;

import com.qkwl.common.dto.entrust.FEntrust;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 委单
 * @author TT
 */
@Mapper
public interface FEntrustMapper {

	/**
	 * 根据类型获取委单
	 * @param tradeId
	 * @param type
	 * @return
	 */
	List<FEntrust> selectGoingByType(@Param("ftradeid") int tradeId, @Param("ftype") int type);
}