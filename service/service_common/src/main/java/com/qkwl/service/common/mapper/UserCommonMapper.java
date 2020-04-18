package com.qkwl.service.common.mapper;

import com.qkwl.common.dto.user.FUser;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户公共MAPPER
 */
@Mapper
public interface UserCommonMapper {
    /**
     * 查询单个用户
     *
     * @param id 用户ID
     * @return FUser
     */
    @Select("select * from f_user where fid = #{id}")
    FUser selectOneById(@Param("id") Integer id);

    /**
     * 查询单个用户
     *
     * @param showId 显示ID
     * @return FUser
     */
    @Select("select * from f_user where fshowid = #{showId}")
    FUser selectOneByShowId(@Param("showId") Integer showId);

    /**
     * 查询用户VIP等级
     *
     * @param userId 用户ID
     * @return FUser
     */
    @Select("select flevel from f_user_score where fuid = #{userId}")
    Integer selectVipLevel(@Param("userId") Integer userId);


}
