package com.qkwl.service.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.log.FLogUserScore;

/**
 * 积分流水
 * @author ZKF
 */
@Mapper
public interface FLogUserScoreMapper {
	
	/**
	 * 分页积分流水查询
	 * @param map 参数列表
	 * @return 流水列表
	 */
    List<FLogUserScore> selectByPage(Map<String, Object> map);

    /**
	 * 分页积分流水查询个数
	 * @param map 参数列表
	 * @return 查询个数
	 */
    int countByPage(Map<String, Object> map);
}