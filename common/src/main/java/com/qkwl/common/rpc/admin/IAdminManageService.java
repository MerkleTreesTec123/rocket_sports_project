package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.admin.FRole;
import com.qkwl.common.dto.admin.FRoleSecurity;
import com.qkwl.common.dto.admin.FSecurity;
import com.qkwl.common.dto.agent.FAgent;
import com.qkwl.common.dto.common.Pagination;

/**
 * 后台管理员设置接口
 * @author ZKF
 */
public interface IAdminManageService {


	/**
	 * 根据实体查询管理员
	 * @param admin 实体参数
	 * @return 管理员实体
	 */
	FAdmin selectAdminByProperty(FAdmin admin);

	/**
	 * 根据id查询管理员
	 * @param fid 管理员id
	 * @return 管理员实体
	 */
	FAdmin selectAdminById(int fid);

	/**
	 * 分页查询管理员列表
	 * @param page 分页参数
	 * @param admin 管理员实体
	 * @return 分页查询记录列表
	 */
	Pagination<FAdmin> selectAdminPageList(Pagination<FAdmin> page, FAdmin admin);

	/**
	 * 新增管理员
	 * @param admin 管理员实体
	 * @return 是否新增成功
	 */
	boolean insertAdmin(FAdmin admin);

	/**
	 * 更新管理员
	 * @param admin 管理员
	 * @return 是否更新成功
	 */
	boolean updateAdmin(FAdmin admin);

	/**
	 * 分页获取角色列表
	 * @param page 分页参数
	 * @param role 实体参数
	 * @return 分页查询记录列表
	 */
	Pagination<FRole> selectRolePageList(Pagination<FRole> page, FRole role);

	/**
	 * 新增角色
	 * @param role 角色实体
	 * @return 角色id 或 空
	 */
	Integer insertRole(FRole role);

	/**
	 * 更新角色
	 * @param role 角色实体
	 * @return  是否更新成功
	 */
	boolean updateRole(FRole role);
	
	/**
	 * 新增角色权限关联
	 * @param role  角色权限列表
	 * @param roleId 角色id
	 * @return 是否执行成功
	 */
	boolean insertRoleSecurity(List<FRoleSecurity> role,Integer roleId);

	/**
	 * 根据id查询角色
	 * @param fid 角色id
	 * @return 角色实体
	 */
	FRole selectRoleById(int fid);
	
	/**
	 * 查询权限列表
	 * @return 权限列表 
	 */
	List<FRole> selectRoleList();
	
	/**
	 * 删除角色
	 * @param roleId 角色id
	 * @return 是否删除成功
	 */
	boolean deleteByRoleId(int roleId);

	/**
	 * 根据角色id查询权限列表
	 * @param froleid 角色id
	 * @return 权限列表
	 */
	List<FSecurity> selectSecurityList(int froleid);
	
	/**
	 * 查询所有权限
	 * @return 权限列表
	 */
	List<FSecurity> selectSecurityAllList();

	/**
	 * 分页查询权限列表
	 * @param page 分页参数
	 * @param pid 父级id
	 * @return 分页权限列表
	 */
	Pagination<FSecurity> selectSecurityPageList(Pagination<FSecurity> page, Integer pid);

	/**
	 * 新增权限
	 * @param security 权限实体
	 * @return 是否新增成功
	 */
	boolean insertSecurity(FSecurity security);

	/**
	 * 删除权限
	 * @param id 权限id
 	 * @return 是否删除成功
	 */
	boolean deleteSecurity(int id);

	/**
	 * 更新权限
	 * @param security 权限实体
	 * @return 是否更新成功
	 */
	boolean updateSecurity(FSecurity security);

	/**
	 * 根据id查询权限
	 * @param id 权限id
	 * @return 权限实体
	 */
	FSecurity selectSecurityById(int id);
	
	/**
	 * 新增券商
	 * @param agent 券商实体
	 * @return 是否新增成功
	 */
	boolean insertAgent(FAgent agent);
	
	/**
	 * 更新券商
	 * @param agent 券商实体
	 * @return 是否更新成功
	 */
	boolean updateAgent(FAgent agent);
	
	/**
	 * 删除券商
	 * @param fid 券商id
	 * @return 是否删除成功
	 */
	boolean deleteAgent(int fid);
	
	/**
	 * 根据id券商
	 * @param fid 券商id
	 * @return 券商实体
	 */
	FAgent selectAgent(int fid);
	
	/**
	 * 分页查询券商列表
	 * @param page 分页参数
	 * @param fagent 实体参数
	 * @return 分页查询记录列表
	 */
	Pagination<FAgent> selectAgentPageList(Pagination<FAgent> page, FAgent fagent);
	
}
