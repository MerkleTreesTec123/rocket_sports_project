package com.qkwl.service.user.dao;


import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUserIdentity;

/**
 * 用户实名信息
 * @author ZKF
 */
@Mapper
public interface FUserIdentityMapper {
	
    int insert(FUserIdentity record);

    int updateByPrimaryKey(FUserIdentity record);
    
    /**
     * 根据用户查询信息
     */
    FUserIdentity selectByUser(Integer fuid);
    
    FUserIdentity selectByCode(String code);
}