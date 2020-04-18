package com.qkwl.service.admin.bc.impl;

import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.rpc.admin.IAdminApiAuthService;
import com.qkwl.common.rpc.entrust.IEntrustServer;
import com.qkwl.common.util.Constant;
import com.qkwl.service.admin.bc.dao.*;
import com.qkwl.service.admin.bc.utils.ApiAuthGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 后台用户接口实现
 */
@Service("adminApiAuthService")
public class AdminApiAuthServiceImpl implements IAdminApiAuthService {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AdminApiAuthServiceImpl.class);

	@Autowired
	private FApiAuthMapper fApiAuthMapper;

	@Override
	public FApiAuth insertAuth(Integer fuid) throws BCException {
		List<FApiAuth> list = fApiAuthMapper.selectByPage(fuid);
		if(list != null && list.size() >= Constant.apinum){
			throw new BCException("超出申请限制!");
		}
		FApiAuth auth = ApiAuthGenerator.getApiSecretKey();
		auth.setFuid(fuid);
		auth.setFip("0");
		auth.setFcreatetime(new Date());
		auth.setFupdatetime(new Date());
		auth.setFstatus(2);
		auth.setFopenrate(0);
		auth.setFrate(BigDecimal.ZERO);
		fApiAuthMapper.insert(auth);
		logger.info("添加AuthApi结果："+auth.getFid());
		return auth.getFid() > 0 ? auth : null;
	}

	@Override
	public List<FApiAuth> selectFApiAuthListByUID(Integer fuid) {
		return fApiAuthMapper.selectByPage(fuid);
	}

	@Override
	public List<FApiAuth> selectFApiAuthListByID(Integer fid) {
		FApiAuth apiAuth = fApiAuthMapper.selectByPrimaryKey(fid);
		List<FApiAuth> fApiAuths = new ArrayList<>();
		fApiAuths.add(apiAuth);
		return fApiAuths;
	}

	@Override
	public List<FApiAuth> selectAll() {
		return fApiAuthMapper.selectAll();
	}

	@Override
	public int updateByUser(FApiAuth api) {
		return fApiAuthMapper.updateByUser(api);
	}
}
