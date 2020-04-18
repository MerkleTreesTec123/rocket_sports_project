package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FIdentityInfo;

import java.util.List;
import java.util.Map;

/**
 * 身份证信息数据接口
 */
@Mapper
public interface FIdentityInfoMapper {
	
    /**
     * 修改身份证信息
     */
    int updateByPrimaryKey(FIdentityInfo record);
    
    /**
     * 根据id查询身份证信息
     */
    FIdentityInfo selectByPrimaryKey(Integer fid);

    /**
     * 查询身份证信息
     */
    List<FIdentityInfo> selectByPage(Map<String, Object> map);

    /**
     * 查询身份证信息
     */
    int countByPage(Map<String, Object> map);
}