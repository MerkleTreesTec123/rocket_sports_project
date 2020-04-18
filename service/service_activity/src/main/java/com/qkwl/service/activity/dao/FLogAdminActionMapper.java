package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.log.FLogAdminAction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FLogAdminActionMapper {
	
	/**
	 * 插入数据
	 * @param record
	 * @return
	 */
    int insert(FLogAdminAction record);
}