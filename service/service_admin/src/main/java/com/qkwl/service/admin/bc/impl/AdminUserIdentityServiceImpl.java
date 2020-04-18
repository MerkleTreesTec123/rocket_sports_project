package com.qkwl.service.admin.bc.impl;

import com.qkwl.service.admin.bc.dao.FUserIdentityMapper;
import com.qkwl.service.admin.bc.dao.FUserMapper;
import com.qkwl.common.dto.Enum.IdentityStatusEnum;
import com.qkwl.common.dto.Enum.IdentityTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserIdentity;
import com.qkwl.common.rpc.admin.IAdminUserIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户实名信息接口实现
 */
@Service("adminUserIdentityService")
public class AdminUserIdentityServiceImpl implements IAdminUserIdentityService {
	
	@Autowired
	private FUserIdentityMapper mapper;
	@Autowired
	private FUserMapper userMapper;

	@Override
	public Pagination<FUserIdentity> selectByPage(Pagination<FUserIdentity> page, FUserIdentity identity) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderfield", page.getOrderField());
		map.put("orderdirection", page.getOrderDirection());
		map.put("fstatus", identity.getFstatus());
		map.put("ftype", identity.getFtype());

		int count = mapper.countByPage(map);
		if(count > 0) {
			List<FUserIdentity> list = mapper.selectByPage(map);
			for (FUserIdentity userIdentity : list) {
				userIdentity.setFstatus_s(IdentityStatusEnum.getTypeValueByCode(userIdentity.getFstatus()));
				userIdentity.setFtype_s(IdentityTypeEnum.getEnumString(userIdentity.getFtype()));
			}
			page.setData(list);
		}
		page.setTotalRows(count);
		
		return page;
	}

	@Override
	public Boolean updateIdentity(FUserIdentity identity) throws Exception {
		int i = mapper.updateByPrimaryKey(identity);
		if(i > 0){
			FUser user = userMapper.selectByPrimaryKey(identity.getFuid());
			if(user != null){
				if(identity.getFstatus().equals(IdentityStatusEnum.PASS.getCode())){
					user.setFhasrealvalidate(true);
					user.setFrealname(identity.getFname());
					user.setFidentityno(identity.getFcode());
					user.setFhasrealvalidatetime(new Date());
				}else{
					user.setFhasrealvalidate(false);
					user.setFrealname(null);
					user.setFidentityno(null);
					user.setFhasrealvalidatetime(null);
				}
				int j = userMapper.updateByPrimaryKey(user);
				return j > 0 ? true : false;
			}else{
				throw new Exception("user not fount");
			}
		}
		return false;
	}

	@Override
	public FUserIdentity selectById(Integer fid) {
		return mapper.selectByPrimaryKey(fid);
	}
	
	

}
