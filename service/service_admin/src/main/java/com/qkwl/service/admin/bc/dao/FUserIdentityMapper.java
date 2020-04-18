package com.qkwl.service.admin.bc.dao;


import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUserIdentity;

import java.util.List;
import java.util.Map;

/**
 * 用户实名信息
 * @author ZKF
 */
@Mapper
public interface FUserIdentityMapper {

    int insert(FUserIdentity record);

    int deleteByPrimaryKey(Integer fid);

    FUserIdentity selectByPrimaryKey(Integer fid);

    List<FUserIdentity> selectAll();

    int updateByPrimaryKey(FUserIdentity record);
    
    /**
     * 查询信息列表
     */
    List<FUserIdentity> selectByPage(Map<String, Object> map);
    
    /**
     * 查询信息列表个数
     */
    Integer countByPage(Map<String, Object> map);
    
    /**
     * 根据用户查询信息
     */
    FUserIdentity selectByUser(Integer fuid);
}