package com.qkwl.service.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FIdentityInfo;
/**
 * 身份证信息数据接口
 * @author LY
 *
 */
@Mapper
public interface FIdentityInfoMapper {
	

    /**
     * 插入身份证信息
     * @param record 身份证信息实体
     * @return 成功条数
     */
    int insert(FIdentityInfo record);
    
    /**
     * 查询身份证信息
     * @param record 身份证信息实体
     * @return 身份证信息实体
     */
    List<FIdentityInfo> selectByReal(FIdentityInfo record);
}