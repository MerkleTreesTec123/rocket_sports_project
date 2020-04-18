package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.admin.FRole;

/**
 * 系统角色-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FRoleMapper {

	/**
	 * 添加角色
	 * @param record 角色实体
	 * @return 插入记录数
	 */
    int insert(FRole record);

    /**
     * 根据主键id查询角色
     * @param fid 角色id
	 * @return 角色实体
     */
    FRole selectByPrimaryKey(Integer fid);

    /**
     * 查询所以角色
	 * @return 记录列表
     */
    List<FRole> selectAll();

    /**
     * 更新角色信息
     * @param record 角色实体
	 * @return 更新记录数
     */
    int updateByPrimaryKey(FRole record);
    
    /**
     * 分页查询角色信息
     * @param map 参数map
	 * @return 插入记录列表
     */
    List<FRole> selectRolePageList(Map<String, Object> map);
    
    /**
     * 查询角色总数
     * @param map 参数map
	 * @return 插入记录数
     */
    int countRoleByPage(Map<String, Object> map);
}