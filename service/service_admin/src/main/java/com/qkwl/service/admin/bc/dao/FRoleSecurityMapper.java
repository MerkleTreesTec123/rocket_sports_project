package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.admin.FRoleSecurity;

/**
 * 系统角色权限关联表-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FRoleSecurityMapper {

	/**
	 * 添加角色的权限信息
	 * @param record 角色权限关联列表
	 * @return 插入记录数
	 */
    int insert(List<FRoleSecurity> record);

    /**
     * 删除角色的权限信息
     * @param roleId 角色id
     * @return 删除记录数 
     */
    int deleteByRoleId(Integer roleId);
    
    /**
     * 根据角色ID查询角色权限
     * @param roleId 角色id
     * @return 查询记录列表
     */
    List<FRoleSecurity> selectByRoleId(Integer roleId);
    
    
}