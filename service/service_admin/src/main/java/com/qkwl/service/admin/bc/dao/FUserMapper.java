package com.qkwl.service.admin.bc.dao;


import java.util.List;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUser;

/**
 * 用户-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FUserMapper {
	

    /**
     * 根据id查询用户
     * @param fid 用户id
     * @return 用户实体
     */
    FUser selectByPrimaryKey(Integer fid);

    /**
     * 更新用户 
     * @param record 用户实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FUser record);
    
    /**
     * 根据实体查询用户列表
     * @param user 用户实体
     * @return 用户列表
     */
    List<FUser> getUserListByParam(FUser user);
    
    /**
     * 用户分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<FUser> getUserPageList(Map<String, Object> map);
    
    /**
     * 用户分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countUserListByParam(Map<String, Object> map);
    
    /**
     * 查询当前时间的总注册人数
     * @return 查询总记录数
     */
    int countUserRegister();
    
    /**
     * 查询靓号是否被使用
     * @return 
     */
    int selectFuserByFshowid(@Param("fshowid") Integer fshowid);

    /**
     * 查询所有用户
     */
    List<FUser> selectAll();
}