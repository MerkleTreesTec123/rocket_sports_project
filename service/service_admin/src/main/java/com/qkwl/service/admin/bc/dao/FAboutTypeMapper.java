package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.web.FAboutType;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 关于我们类型-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FAboutTypeMapper {
	
    /**
     * 查询
     * @param agentid 券商id
     * @return	 关于我们类型列表
     */
	List<FAboutType> selectAll(@Param("lanId") Integer lanId, @Param("agentid") Integer agentid);
}