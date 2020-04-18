package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.admin.FAdmin;

/**
 * 管理员-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FAdminMapper {

	/**
	 * 新增管理员
	 * @param record 管理员实体
	 * @return 插入记录数
	 */
	int insert(FAdmin record);

	/**
	 * 查询管理员
	 * @param fid 管理员id
	 * @return	管理员实体
	 */
	FAdmin selectByPrimaryKey(Integer fid);

	/**
	 * 更新管理员
	 * @param record 管理员实体
	 * @return 更新记录数
	 */
	int updateByPrimaryKey(FAdmin record);

	/**
	 * 根据实体查询管理员
	 * @param record 管理员实体参数
	 * @return FAdmin 管理员实体
	 */
	FAdmin findByPrimary(FAdmin record);

	/**
	 * 分页查询管理员
	 * @param map 参数map
	 * @return 管理员列表
	 */
	List<FAdmin> selectAdminPageList(Map<String, Object> map);

	/**
	 * 分页查询管理员的总记录数
	 * @param map 参数map
	 * @return 管理员列表总记录数
	 */
	int countAdminByPage(Map<String, Object> map);

}