package com.qkwl.common.rpc.user;

import java.util.List;

import com.qkwl.common.dto.user.RequestUserInfo;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.user.FUser;

/**
 * 用户api接口
 * @author ZKF
 */
public interface IUserApiService {

	/**
	 * 增加用户Api
	 * @param fuid 用户ID
	 * @param ip IP地址
	 * @return 实体对象
	 * @throws BCException 执行失败
	 */
	public FApiAuth insertApi(int fuid, String ip, RequestUserInfo info) throws BCException;

	
	/**
	 * 查询用户Api
	 * @param fuid 用户ID
	 * @return 实体对象列表
	 */
	public List<FApiAuth> selectApiByUser(int fuid);
	
	
	/**
	 * 根据api查询实体
	 * @param apikey APIkey
	 * @return 实体对象
	 */
	public FApiAuth selectApiAuthByApi(String apikey);
	
	/**
	 * 根据id查询实体
	 * @param fid 主键ID
	 * @return 实体对象
	 */
	public FApiAuth selectApiAuthById(Integer fid);
	
	/**
	 * 根据api查询用户实体
	 * @param apikey APIkey
	 * @return 用户实体对象
	 */
	public FUser selectUserByApi(String apikey);
	
	/**
	 * 删除api
	 * @param id 主键ID
	 * @return true：成功，false：失败
	 */
	public boolean deleteApi(int id);
}
