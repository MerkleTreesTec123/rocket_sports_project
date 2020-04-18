package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.service.admin.bc.dao.FAdminMapper;
import com.qkwl.service.admin.bc.dao.FAgentMapper;
import com.qkwl.service.admin.bc.dao.FRoleMapper;
import com.qkwl.service.admin.bc.dao.FRoleSecurityMapper;
import com.qkwl.service.admin.bc.dao.FSecurityMapper;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.admin.FRole;
import com.qkwl.common.dto.admin.FRoleSecurity;
import com.qkwl.common.dto.admin.FSecurity;
import com.qkwl.common.dto.agent.FAgent;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminManageService;

/**
 * 后台管理员设置接口实现
 * 
 * @author ZKF
 */
@Service("adminManageService")
public class AdminManageServiceImpl implements IAdminManageService {

	@Autowired
	private FAdminMapper adminMapper;
	@Autowired
	private FRoleMapper roleMapper;
	@Autowired
	private FSecurityMapper securityMapper;
	@Autowired
	private FRoleSecurityMapper rsMapper;
	@Autowired
	private FAgentMapper agentMapper;


	/**
	 * 分页查询管理员列表
	 * @param page 分页参数
	 * @param admin 管理员实体
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectAdminPageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.admin.FAdmin)
	 */
	@Override
	public Pagination<FAdmin> selectAdminPageList(Pagination<FAdmin> page, FAdmin admin) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		map.put("keyword", page.getKeyword());
		int count = adminMapper.countAdminByPage(map);
		page.setTotalRows(count);
		if(count <= 0){
			return page;
		}

		List<FAdmin> adminList = adminMapper.selectAdminPageList(map);
		for (FAdmin fAdmin : adminList) {
			FRole role = roleMapper.selectByPrimaryKey(fAdmin.getFroleid());
			if (role != null) {
				fAdmin.setRolename(role.getFname());
			}
			FAgent agent = agentMapper.selectByPrimaryKey(fAdmin.getFagentid());
			if (agent != null) {
				fAdmin.setFagentname(agent.getFname());
			}
		}
		page.setData(adminList);

		return page;
	}

	/**
	 * 新增管理员
	 * @param admin 管理员实体
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#insertAdmin(com.qkwl.common.dto.admin.FAdmin)
	 */
	@Override
	public boolean insertAdmin(FAdmin admin) {
		int i = adminMapper.insert(admin);
		return i > 0 ? true : false;
	}

	/**
	 * 更新管理员
	 * @param admin 管理员
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#updateAdmin(com.qkwl.common.dto.admin.FAdmin)
	 */
	@Override
	public boolean updateAdmin(FAdmin admin) {
		int i = adminMapper.updateByPrimaryKey(admin);
		return i > 0 ? true : false;
	}

	/**
	 * 根据id查询管理员
	 * @param fid 管理员id
	 * @return 管理员实体
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectAdminById(int)
	 */
	@Override
	public FAdmin selectAdminById(int fid) {
		return adminMapper.selectByPrimaryKey(fid);
	}

	/**
	 * 分页获取角色列表
	 * @param page 分页参数
	 * @param role 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectRolePageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.admin.FRole)
	 */
	@Override
	public Pagination<FRole> selectRolePageList(Pagination<FRole> page, FRole role) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("fname", page.getKeyword());

		int count = roleMapper.countRoleByPage(map);
		if(count > 0) {
			List<FRole> roleList = roleMapper.selectRolePageList(map);
			page.setData(roleList);
		}
		page.setTotalRows(count);

		return page;
	}

	/**
	 * 新增角色
	 * @param role 角色实体
	 * @return 角色id 或 空
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#insertRole(com.qkwl.common.dto.admin.FRole)
	 */
	@Override
	public Integer insertRole(FRole role) {
		int i = roleMapper.insert(role);
		return i > 0 ? role.getFid() : null;
	}

	/**
	 * 更新角色
	 * @param role 角色实体
	 * @return  是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#updateRole(com.qkwl.common.dto.admin.FRole)
	 */
	@Override
	public boolean updateRole(FRole role) {
		int i = roleMapper.updateByPrimaryKey(role);
		return i > 0 ? true : false;
	}

	/**
	 * 根据实体查询管理员
	 * @param admin 实体参数
	 * @return 管理员实体
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectAdminByProperty(com.qkwl.common.dto.admin.FAdmin)
	 */
	@Override
	public FAdmin selectAdminByProperty(FAdmin admin) {
		return adminMapper.findByPrimary(admin);
	}
	
	/**
	 * 根据id查询角色
	 * @param fid 角色id
	 * @return 角色实体
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectRoleById(int)
	 */
	@Override
	public FRole selectRoleById(int fid) {
		return roleMapper.selectByPrimaryKey(fid);
	}

	/**
	 * 根据角色id查询权限列表
	 * @param froleid 角色id
	 * @return 权限列表
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectSecurityList(int)
	 */
	@Override
	public List<FSecurity> selectSecurityList(int froleid) {
		return securityMapper.findFSecurityList(froleid);
	}

	/**
	 * 查询所有权限
	 * @return 权限列表
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectSecurityAllList()
	 */
	@Override
	public List<FSecurity> selectSecurityAllList() {
		return securityMapper.selectAll();
	}

	/**
	 * 分页查询权限列表
	 * @param page 分页参数
	 * @param pid 父级id
	 * @return 分页权限列表
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectSecurityPageList(com.qkwl.common.dto.common.Pagination, java.lang.Integer)
	 */
	@Override
	public Pagination<FSecurity> selectSecurityPageList(Pagination<FSecurity> page, Integer pid) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		map.put("keyword", page.getKeyword());

		map.put("fparentid", pid);

		int count = securityMapper.countSecurityByPid(map);
		if(count > 0) {
			List<FSecurity> list = securityMapper.getSecurityByPid(map);
			page.setData(list);
		}
		page.setTotalRows(count);

		return page;
	}

	/**
	 * 新增权限
	 * @param security 权限实体
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#insertSecurity(com.qkwl.common.dto.admin.FSecurity)
	 */
	@Override
	public boolean insertSecurity(FSecurity security) {
		int i = securityMapper.insert(security);
		return i > 0 ? true : false;
	}

	/**
	 * 删除权限
	 * @param id 权限id
 	 * @return 是否删除成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#deleteSecurity(int)
	 */
	@Override
	public boolean deleteSecurity(int id) {
		int i = securityMapper.deleteByPrimaryKey(id);
		return i > 0 ? true : false;
	}

	/**
	 * 更新权限
	 * @param security 权限实体
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#updateSecurity(com.qkwl.common.dto.admin.FSecurity)
	 */
	@Override
	public boolean updateSecurity(FSecurity security) {
		int i = securityMapper.updateByPrimaryKey(security);
		return i > 0 ? true : false;
	}

	/**
	 * 根据id查询权限
	 * @param id 权限id
	 * @return 权限实体
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectSecurityById(int)
	 */
	@Override
	public FSecurity selectSecurityById(int id) {
		FSecurity security = securityMapper.selectByPrimaryKey(id);
		return security;
	}

	/**
	 * 查询权限列表
	 * @return 权限列表 
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectRoleList()
	 */
	@Override
	public List<FRole> selectRoleList() {
		return roleMapper.selectAll();
	}

	/**
	 * 新增角色权限关联
	 * @param role  角色权限列表
	 * @param roleId 角色id
	 * @return 是否执行成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#insertRoleSecurity(java.util.List, java.lang.Integer)
	 */
	@Override
	public boolean insertRoleSecurity(List<FRoleSecurity> role, Integer roleId) {
		rsMapper.deleteByRoleId(roleId);

		Integer i = rsMapper.insert(role);
		return i > 0 ? true : false;
	}

	/**
	 * 删除角色
	 * @param roleId 角色id
	 * @return 是否删除成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#deleteByRoleId(int)
	 */
	@Override
	public boolean deleteByRoleId(int roleId) {
		int i = rsMapper.deleteByRoleId(roleId);
		return i > 0 ? true : false;
	}

	/**
	 * 新增券商
	 * @param agent 券商实体
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#insertAgent(com.qkwl.common.dto.agent.FAgent)
	 */
	@Override
	public boolean insertAgent(FAgent agent) {
		int i = agentMapper.insert(agent);
		return i > 0 ? true : false;
	}

	/**
	 * 更新券商
	 * @param agent 券商实体
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#updateAgent(com.qkwl.common.dto.agent.FAgent)
	 */
	@Override
	public boolean updateAgent(FAgent agent) {
		int i = agentMapper.update(agent);
		return i > 0 ? true : false;
	}

	/**
	 * 删除券商
	 * @param fid 券商id
	 * @return 是否删除成功
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#deleteAgent(int)
	 */
	@Override
	public boolean deleteAgent(int fid) {
		int i = agentMapper.delete(fid);
		return i > 0 ? true : false;
	}

	/**
	 * 根据id券商
	 * @param fid 券商id
	 * @return 券商实体
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectAgent(int)
	 */
	@Override
	public FAgent selectAgent(int fid) {
		return agentMapper.selectByPrimaryKey(fid);
	}

	/**
	 * 分页查询券商列表
	 * @param page 分页参数
	 * @param fagent 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminManageService#selectAgentPageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.agent.FAgent)
	 */
	@Override
	public Pagination<FAgent> selectAgentPageList(Pagination<FAgent> page, FAgent fagent) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		map.put("keyword", page.getKeyword());

		int count = agentMapper.countPageList(map);
		if(count > 0) {
			List<FAgent> list = agentMapper.getPageList(map);
			page.setData(list);
		}
		page.setTotalRows(count);

		return page;
	}

}
