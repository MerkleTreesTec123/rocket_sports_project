package com.qkwl.common.rpc.admin;


import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUserIdentity;

/**
 * 后台用户实名信息接口
 * @author ZKF
 */
public interface IAdminUserIdentityService {
	
	/**
	 * 查询分页
	 */
	public Pagination<FUserIdentity> selectByPage(Pagination<FUserIdentity> page, FUserIdentity identity);
	
	
	/**
	 * 更新信息
	 * @throws Exception 
	 */
	public Boolean updateIdentity(FUserIdentity identity) throws Exception;
	
	
	/**
	 * 根据id查询信息
	 */
	public FUserIdentity selectById(Integer fid);
	

}
