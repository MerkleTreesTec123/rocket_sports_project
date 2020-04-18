package com.qkwl.service.market.mapper;

import com.qkwl.common.dto.market.FPeriod;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * K线行情
 * @author TT
 */
@Mapper
public interface FPeriodMapper {
	
	/**
	 * 插入行情
	 * @param record
	 * @return
	 */
	
    int insert(FPeriod record);
    
    /**
     * 查找币种行情
     * @param coinId
     * @return
     */
    List<FPeriod> selectforId(int coinId);

}