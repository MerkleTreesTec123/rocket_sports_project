package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.log.FLogUserScore;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分流水
 * @author ZKF
 */
@Mapper
public interface FLogUserScoreMapper {
	
	/**
	 * 增加积分流水
	 * @param record
	 * @return
	 */
    int insert(FLogUserScore record);
}