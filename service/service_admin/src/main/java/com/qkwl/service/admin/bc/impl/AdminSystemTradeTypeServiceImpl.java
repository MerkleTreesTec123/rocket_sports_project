package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.service.admin.bc.comm.SystemRedisInit;
import com.qkwl.service.admin.bc.dao.SystemTradeTypeMapper;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminSystemTradeTypeService;

@Service("adminSystemTradeTypeService")
public class AdminSystemTradeTypeServiceImpl implements IAdminSystemTradeTypeService {

	@Autowired
	private SystemTradeTypeMapper systemTradeTypeMapper;

	@Autowired
	private SystemRedisInit systemRedisInit;

	@Override
	public Pagination<SystemTradeType> selectSystemTradeTypeList(Pagination<SystemTradeType> page,
			SystemTradeType tradeType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderfield", page.getOrderField());
		map.put("orderdirection", page.getOrderDirection());
		map.put("agentid", tradeType.getAgentId());
		int count = systemTradeTypeMapper.selectSystemTradeTypeCount(map);
		if(count > 0) {
			List<SystemTradeType> articleList = systemTradeTypeMapper.selectSystemTradeTypeList(map);
			page.setData(articleList);
		}
		page.setTotalRows(count);
		return page;
	}

	@Override
	public SystemTradeType selectSystemTradeType(Integer tradeId) {
		return systemTradeTypeMapper.selectByPrimaryKey(tradeId);
	}

	@Override
	public boolean insertSystemTradeType(SystemTradeType tradeType) {
		if (systemTradeTypeMapper.insert(tradeType) > 0) {
			systemRedisInit.initSystemTradeType();
			return true;
		}
		return false;
	}

	@Override
	public boolean updateSystemTradeType(SystemTradeType tradeType) {
		if (systemTradeTypeMapper.updateByPrimaryKey(tradeType) > 0) {
			systemRedisInit.initSystemTradeType();
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteSystemTradeType(Integer tradeId) {
		if (systemTradeTypeMapper.deleteByPrimaryKey(tradeId) > 0) {
			systemRedisInit.initSystemTradeType();
			return true;
		}
		return false;
	}

	@Override
	public SystemTradeType selectSystemTradeTypeByCoinId(Integer sellCoinId, Integer buyCoinId) {
		return systemTradeTypeMapper.selectTradeType(sellCoinId,buyCoinId);
	}

}
