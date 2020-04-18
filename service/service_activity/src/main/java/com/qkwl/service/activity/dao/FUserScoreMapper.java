package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.user.FUserScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户积分等级数据操作接口
 * @author ZKF
 */
@Mapper
public interface FUserScoreMapper {
    
    /**
     * 更新用户积分等级
     * @param record
     * @return
     */
    int updateByPrimaryKey(FUserScore record);
    
    /**
     * 根据用户查找积分等级
     */
    FUserScore selectByUid(@Param("fuid") int fuid);
}