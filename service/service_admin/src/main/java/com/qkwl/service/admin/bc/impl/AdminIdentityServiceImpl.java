package com.qkwl.service.admin.bc.impl;

import com.qkwl.service.admin.bc.dao.FIdentityInfoMapper;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FIdentityInfo;
import com.qkwl.common.rpc.admin.IAdminIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 身份认证
 */
@Service("adminIdentityService")
public class AdminIdentityServiceImpl implements IAdminIdentityService {

    @Autowired
    private FIdentityInfoMapper identityInfoMapper;

    @Override
    public Pagination<FIdentityInfo> selectByPage(Pagination<FIdentityInfo> page, FIdentityInfo info) {
        Map<String, Object> map = new HashMap<String, Object>();
        //基础参数
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        map.put("keyword", page.getKeyword());
        map.put("fisok", info.getFisok());
        map.put("orderField", page.getOrderField());
        map.put("orderDirection", page.getOrderDirection());

        int count = identityInfoMapper.countByPage(map);
        if(count > 0) {
            List<FIdentityInfo> list = identityInfoMapper.selectByPage(map);
            page.setData(list);
        }
        page.setTotalRows(count);
        return page;
    }

    @Override
    public FIdentityInfo selectById(Integer fid) {
        return identityInfoMapper.selectByPrimaryKey(fid);
    }

    @Override
    public Boolean updateIdentity(FIdentityInfo info) {
        return identityInfoMapper.updateByPrimaryKey(info) > 0;
    }
}
