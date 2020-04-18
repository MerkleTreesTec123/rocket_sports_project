package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.log.FLogUserAction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户日志
 * @author TT
 */
@Mapper
public interface FLogUserActionMapper {

	/**
	 * 插入日志
	 * @param record
	 * @return
	 */
    int insert(FLogUserAction record);
}