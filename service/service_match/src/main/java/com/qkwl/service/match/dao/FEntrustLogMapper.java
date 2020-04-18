package com.qkwl.service.match.dao;

import com.qkwl.common.dto.entrust.FEntrustLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FEntrustLogMapper {
	
	/**
	 * 插入日志
	 * @param record 委单日志
	 * @return 1 or 0
	 */
    int insert(FEntrustLog record);
}