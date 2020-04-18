package com.qkwl.service.admin.bc.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FUserVirtualAddressWithdrawDTO;
import com.qkwl.service.admin.bc.dao.*;
import com.qkwl.common.dto.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminUserService;

/**
 * 后台用户接口实现
 */
@Service("adminUserService")
public class AdminUserServiceImpl implements IAdminUserService {

	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private FUserVirtualAddressMapper userVirtualAddressMapper;
	@Autowired
	private FUserVirtualAddressWithdrawMapper userVirtualAddressWithdrawMapper;
	@Autowired
	private FUserScoreMapper userScoreMapper;
	@Autowired
	private FUserIdentityMapper userIdentityMapper;

	/**
	 * 分页查询用户
	 * @param pageParam 分页参数
	 * @param fuser 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserService#selectUserPageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.user.FUser)
	 */
	@Override
	public Pagination<FUser> selectUserPageList(Pagination<FUser> pageParam, FUser fuser) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("keyword", pageParam.getKeyword());
		map.put("fuid", fuser.getFid());
		map.put("fstatus", fuser.getFstatus());
		map.put("fintrouid", fuser.getFintrouid());
		map.put("fregistertime", fuser.getFregistertime());
		map.put("flastlogintime", fuser.getFlastlogintime());
		map.put("fagentid", fuser.getFagentid());
		map.put("fbirth", fuser.getFbirth());

		map.put("beginDate", pageParam.getBegindate());
		map.put("endDate", pageParam.getEnddate());
		// 查询总用户数
		int count = userMapper.countUserListByParam(map);
		if(count > 0) {
			// 查询用户列表
			List<FUser> userList = userMapper.getUserPageList(map);
			// 设置返回数据
			pageParam.setData(userList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	/**
	 * 根据id查询用户
	 * @param fuid 用户id
	 * @return 用户实体
	 */
	@Override
	public FUser selectById(int fuid) {
		return userMapper.selectByPrimaryKey(fuid);
	}

	/**
	 * 更新用户信息
	 * @param user 用户实体
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminUserService#updateUserInfo(com.qkwl.common.dto.user.FUser)
	 */
	@Override
	public boolean updateUserInfo(FUser user) {
		int result = userMapper.updateByPrimaryKey(user);
		return result > 0 ? true : false;
	}

	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean updateUserIdentity(FUser user) throws Exception{
		if(userMapper.updateByPrimaryKey(user)<=0){
			return false;
		}
		if(user.getFhasrealvalidate()){
			FUserIdentity userIdentity = userIdentityMapper.selectByUser(user.getFid());
			if(userIdentity == null){
				userIdentity = new FUserIdentity();
				userIdentity.setFcode(user.getFidentityno());
				userIdentity.setFname(user.getFrealname());
				userIdentity.setFtype(user.getFidentitytype());
				userIdentity.setFstatus(1);
				userIdentity.setFuid(user.getFid());
				userIdentity.setFcreatetime(new Date());
				userIdentity.setFupdatetime(new Date());
				if(userIdentityMapper.insert(userIdentity)<=0){
					throw new  Exception("用户身份证表插入失败！");
				}
			}else{
				userIdentity.setFcode(user.getFidentityno());
				userIdentity.setFname(user.getFrealname());
				userIdentity.setFupdatetime(new Date());
				userIdentity.setFstatus(1);
				if(userIdentityMapper.updateByPrimaryKey(userIdentity)<=0){
					throw new  Exception("用户身份证表更新失败！");
				}
			}
		}else{
			FUserIdentity userIdentity = userIdentityMapper.selectByUser(user.getFid());
			if(userIdentity!=null){
				if(userIdentityMapper.deleteByPrimaryKey(userIdentity.getFid())<=0){
					throw new  Exception("用户身份证表删除失败！");
				}
			}
		}
		return true;
	}

	/**
	 * 用户虚拟币地址
	 * @param address 地址实体
	 * @return 地址列表
	 */
	@Override
	public List<FUserVirtualAddressDTO> selectVirtualAddressByUser(FUserVirtualAddressDTO address) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(address.getFuid() != null){
			map.put("fuid", address.getFuid());
		}
		if (address.getFcoinid() != null) {
			map.put("fcoinid", address.getFcoinid());
		}
		if(address.getFadderess() != null){
			map.put("fadderess", address.getFadderess());
		}
		return userVirtualAddressMapper.selectByMap(map);
	}

	/**
	 * 获取用户提现地址列表
	 * @param address 地址实体
	 * @return 提现地址列表
	 * @see com.qkwl.common.rpc.admin.IAdminUserService#selectVirtualWithdrawAddressByUser(FUserVirtualAddressWithdrawDTO)
	 */
	@Override
	public List<FUserVirtualAddressWithdrawDTO> selectVirtualWithdrawAddressByUser(FUserVirtualAddressWithdrawDTO address) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", address.getFuid());
		if (address.getFcoinid() != null) {
			map.put("fcoinid", address.getFcoinid());
		}
		if(address.getFadderess() != null){
			map.put("fadderess", address.getFadderess());
		}
		return userVirtualAddressWithdrawMapper.selectByMap(map);
	}

	/**
	 * 查询用户是否存在
	 * @param user 用户实体
	 * @return 是否存在
	 * @see com.qkwl.common.rpc.admin.IAdminUserService#selectIsExistByParam(com.qkwl.common.dto.user.FUser)
	 */
	@Override
	public boolean selectIsExistByParam(FUser user) {
		List<FUser> list = userMapper.getUserListByParam(user);
		if(list != null ){
			return list.size() > 0 ? true : false;
		}
		return false;
	}

	@Override
	public int selectRegisterByDate() {
		return userMapper.countUserRegister();
	}

	@Override
	public boolean selectBeautiful(Integer fbid) {
		int result = userMapper.selectFuserByFshowid(fbid);
		return result > 0 ? true : false;
	}


	@Override
	public Boolean updateUserLevel(Integer uid, Integer level) {
		FUserScore score = userScoreMapper.selectByUid(uid);
		if(score == null){
			return false;
		}

		score.setFlevel(level);
		score.setFleveltime(new Date());

		return userScoreMapper.updateByPrimaryKey(score) > 0;
	}
}
