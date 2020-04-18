package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUserContinuous;

/**
 * 用户连续登陆数据操作接口
 * @author LY
 *
 */
@Mapper
public interface FUserContinuousMapper {

	/**
	 * 插入
	 * @param record
	 * @return
	 */
    int insert(FUserContinuous record);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(FUserContinuous record);
    
    /**
     * 根据用户id查询连续登陆记录
     * @param fuid
     * @return
     */
    FUserContinuous selectByUser(Integer fuid);
}