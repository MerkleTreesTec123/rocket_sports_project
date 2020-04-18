package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.admin.FSecurity;

/**
 * 系统权限-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FSecurityMapper {
	
	/**
	 * 删除权限
	 * @param fid 权限id
	 * @return 删除记录数
	 */
	int deleteByPrimaryKey(Integer fid);

	/**
	 * 添加权限
	 * @param record 权限实体
	 * @return 插入记录数
	 */
	int insert(FSecurity record);

	/**
	 * 根据id查询权限
	 * @param fid 权限id
	 * @return 权限实体
	 */
	FSecurity selectByPrimaryKey(Integer fid);

	/**
	 * 查询所有的权限
	 * @return 权限列表
	 */
	List<FSecurity> selectAll();

	/**
	 * 根据id更新权限
	 * @param record 权限实体
	 * @return 更新记录数
	 */
	int updateByPrimaryKey(FSecurity record);

	/***** Admin ******/

	/**
	 * 根据角色查询所以的权限
	 * @param froleid 角色id
	 * @return 权限列表
	 */
	List<FSecurity> findFSecurityList(@Param("froleid") int froleid);

	/**
	 * 分页查询权限记录
	 * @param map 参数map
     * @return 查询记录列表
	 */
	List<FSecurity> getSecurityByPid(Map<String, Object> map);

	/**
	 * 分页查询权限记录数量
	 * @param map 参数map
     * @return 查询记录数 
	 */
	int countSecurityByPid(Map<String, Object> map);
	
}