package com.qkwl.service.admin.bc.dao;



import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUserScore;

/**
 * 用户积分等级-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FUserScoreMapper {
	
	/**
	 * 新增用户积分等级
	 * @param record 积分实体
	 * @return 插入记录数
	 */
    int insert(FUserScore record);

    /**
     * 根据id查询用户积分等级
     * @param fid 积分实体id
     * @return 积分实体
     */
    FUserScore selectByPrimaryKey(Integer fid);
    
    /**
     * 更新用户积分等级
     * @param record 积分实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FUserScore record);
    
    /**
     * 根据用户查找积分等级
     * @param fuid 用户id
     * @return 积分实体
     */
    FUserScore selectByUid(@Param("fuid") int fuid);
}