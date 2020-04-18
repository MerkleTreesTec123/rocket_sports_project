package com.qkwl.service.admin.bc.dao;

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
     * 分页查询用户积分日志
     * @param map 参数map
     * @return 查询记录列表
     */
    List<FLogUserScore> getPageList(Map<String, Object> map);
    
    /**
     * 分页查询用户积分日志的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countPageList(Map<String, Object> map);
}