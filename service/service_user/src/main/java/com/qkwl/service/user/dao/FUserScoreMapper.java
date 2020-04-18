package com.qkwl.service.user.dao;


import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUserScore;

/**
 * 用户积分等级数据操作接口
 * @author ZKF
 */
@Mapper
public interface FUserScoreMapper {
	
	/**
	 * 新增用户积分等级
	 * @param record 实体对象
	 * @return 成功条数
	 */
    int insert(FUserScore record);

    /**
     * 根据id查询用户积分等级
     * @param fid 主键ID
     * @return 实体对象
     */
    FUserScore selectByPrimaryKey(Integer fid);
    
    /**
     * 更新用户积分等级
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(FUserScore record);
    
    /**
     * 根据用户查找积分等级
     * @param fuid 用户ID
     * @return 实体对象
     */
    FUserScore selectByUid(@Param("fuid") int fuid);
}