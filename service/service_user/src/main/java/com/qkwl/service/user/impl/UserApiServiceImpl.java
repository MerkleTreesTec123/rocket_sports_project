package com.qkwl.service.user.impl;

import java.util.Date;
import java.util.List;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.dto.user.RequestUserInfo;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.util.Constant;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.rpc.user.IUserApiService;
import com.qkwl.service.user.dao.FApiAuthMapper;
import com.qkwl.service.user.dao.FUserMapper;
import com.qkwl.service.user.utils.ApiAuthGenerator;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户Api接口实现
 *
 */
@Service("userApiService")
public class UserApiServiceImpl implements IUserApiService{
	
	@Autowired
	private FApiAuthMapper apiAuthMapper;
	@Autowired
	private FUserMapper userMapper;

	@Autowired
	private ValidationCheckHelper validationCheckHelper;

	/**
	 * 增加用户Api
	 * @param fuid 用户ID
	 * @param ip IP地址
	 * @return 实体对象
	 * @throws BCException
	 * @see com.qkwl.common.rpc.user.IUserApiService#insertApi(int, java.lang.String,RequestUserInfo)
	 */
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public FApiAuth insertApi(int fuid,String ip,RequestUserInfo userinfo) throws BCException{
		Result phoneCodeCheck = validationCheckHelper.getPhoneCodeCheck(userinfo.getFareacode(),
				userinfo.getFloginname(), userinfo.getCode(),
				BusinessTypeEnum.SMS_APPLY_API.getCode(), userinfo.getIp(), userinfo.getPlatform().getCode());
		if (!phoneCodeCheck.getSuccess()){
			throw new BCException(phoneCodeCheck.getMsg());
		}

		List<FApiAuth> list = apiAuthMapper.selectByUser(fuid);
		if(list != null && list.size() >= Constant.apinum){
			throw new BCException("超出申请限制!");
		}
		FApiAuth auth = ApiAuthGenerator.getApiSecretKey();
		auth.setFuid(fuid);
		auth.setFip(ip);
		auth.setFcreatetime(new Date());
		auth.setFupdatetime(new Date());
		auth.setFstatus(2);
		auth.setFopenrate(0);
		int i = apiAuthMapper.insert(auth);
		return i > 0 ? auth : null;
	}

	/**
	 * 查询用户Api
	 * @param fuid 用户ID
	 * @return 实体对象列表
	 * @see com.qkwl.common.rpc.user.IUserApiService#selectApiByUser(int)
	 */
	@Override
	public List<FApiAuth> selectApiByUser(int fuid) {
		return apiAuthMapper.selectByUser(fuid);
	}

	/**
	 * 根据api查询实体
	 * @param apikey APIkey
	 * @return 实体对象
	 * @see com.qkwl.common.rpc.user.IUserApiService#selectApiAuthByApi(java.lang.String)
	 */
	@Override
	public FApiAuth selectApiAuthByApi(String apikey) {
		return apiAuthMapper.selectByApi(apikey);
	}
	
	/**
	 * 根据id查询实体
	 * @param fid 主键ID
	 * @return 实体对象
	 * @see com.qkwl.common.rpc.user.IUserApiService#selectApiAuthById(java.lang.Integer)
	 */
	@Override
	public FApiAuth selectApiAuthById(Integer fid) {
		return apiAuthMapper.selectByPrimaryKey(fid);
	}

	/**
	 * 删除api
	 * @param id 主键ID
	 * @return true：成功，false：失败
	 * @see com.qkwl.common.rpc.user.IUserApiService#deleteApi(int)
	 */
	@Override
	public boolean deleteApi(int id) {
		int i = apiAuthMapper.deleteByPrimaryKey(id);
		return i > 0 ? true : false;
	}

	/**
	 * 根据api查询用户实体
	 * @param apikey APIkey
	 * @return 用户实体对象
	 * @see com.qkwl.common.rpc.user.IUserApiService#selectUserByApi(java.lang.String)
	 */
	@Override
	public FUser selectUserByApi(String apikey) {
		FApiAuth auth = apiAuthMapper.selectByApi(apikey);
		if(auth != null){
			return userMapper.selectByPrimaryKey(auth.getFuid());
		}
		return null;
	}

}
