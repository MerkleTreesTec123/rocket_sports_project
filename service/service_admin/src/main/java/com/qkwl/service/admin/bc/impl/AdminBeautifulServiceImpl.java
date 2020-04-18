package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.service.admin.bc.dao.FBeautifulMapper;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FBeautiful;
import com.qkwl.common.rpc.admin.IAdminBeautifulService;

@Service("adminBeautifulService")
public class AdminBeautifulServiceImpl implements IAdminBeautifulService{

	@Autowired
	private FBeautifulMapper beautifulMapper;

	@Override
	public Pagination<FBeautiful> selectBeautifulPageList(Pagination<FBeautiful> page, boolean isUse) {
		Map<String, Object> map = new HashMap<String, Object>();
		//基础参数
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());
		map.put("isUse", isUse);

		int count = beautifulMapper.getBeautifulPageCount(map);
		if(count > 0) {
			List<FBeautiful> beautifuls = beautifulMapper.getBeautifulPageList(map);
			page.setData(beautifuls);
		}
		page.setTotalRows(count);
		
		return page;

	}

	@Override
	public FBeautiful selectBeautiful(Integer fid) {		
		return beautifulMapper.selectByFid(fid);
	}

	@Override
	public boolean insertBeautiful(FBeautiful beautiful) {
		int result = beautifulMapper.insert(beautiful);
		return result>0?true:false;
	}

	@Override
	public boolean updateBeautiful(FBeautiful beautiful) {
		int result = beautifulMapper.update(beautiful);
		return result>0?true:false;
	}

	@Override
	public boolean deleteBeautiful(Integer fid) {
		int result = beautifulMapper.delete(fid);
		return result>0?true:false;
	}

}
