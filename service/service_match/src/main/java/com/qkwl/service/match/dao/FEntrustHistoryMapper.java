package com.qkwl.service.match.dao;

import com.qkwl.common.dto.entrust.FEntrustHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FEntrustHistoryMapper {
	
	/**
	 * 插入
	 * @param record 历史委单
	 * @return 1 or 0
	 */
    int insert(FEntrustHistory record);
}