package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserInfo;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressWithdrawDTO;

/**
 * 后台用户接口
 * @author ZKF
 */
public interface IAdminUserService {

	/**
	 * 分页查询用户
	 * @param pageParam 分页参数
	 * @param fUser 实体参数
	 * @return 分页查询记录列表
	 */
	public Pagination<FUser> selectUserPageList(Pagination<FUser> pageParam, FUser fUser);

	/**
	 * 根据id查询用户
	 * @param fuid 用户id
	 * @return 用户实体
	 */
	public FUser selectById(int fuid);

	/**
	 * 查询用户是否存在
	 * @param user 用户实体
	 * @return 是否存在
	 */
	public boolean selectIsExistByParam(FUser user);
	
	/**
	 * 更新用户信息
	 * @param user 用户实体
	 * @return 是否更新成功
	 */
	public boolean updateUserInfo(FUser user);

	/**
	 * 更新用户信息(处理身份证)
	 * @param user 用户实体
	 * @return 是否更新成功
	 */
	boolean updateUserIdentity(FUser user) throws Exception;

	/**
	 * 用户虚拟币地址
	 * @param address 地址实体
	 * @return 地址列表
	 */
	public List<FUserVirtualAddressDTO> selectVirtualAddressByUser(FUserVirtualAddressDTO address);

	/**
	 * 获取用户提现地址列表
	 * @param address 地址实体
	 * @return 提现地址列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserService#selectVirtualWithdrawAddressByUser(FUserVirtualAddressWithdrawDTO)
	 */
	public List<FUserVirtualAddressWithdrawDTO> selectVirtualWithdrawAddressByUser(FUserVirtualAddressWithdrawDTO address);
	
	/**
	 * 获取当日前后五天注册总人数
	 * @return 总人数
	 */
	public int selectRegisterByDate();
	
	/**
	 * 查询靓号是否使用
	 * @param fbid
	 * @return
	 */
	public boolean selectBeautiful(Integer fbid);
	
	/**
	 * 修改用户积分等级
	 */
	Boolean updateUserLevel(Integer uid, Integer level);
}
