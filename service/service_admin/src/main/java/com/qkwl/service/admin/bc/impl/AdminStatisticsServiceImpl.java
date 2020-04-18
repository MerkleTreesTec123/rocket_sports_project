package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.service.admin.bc.dao.FStatisticsMapper;
import com.qkwl.common.rpc.admin.IAdminStatisticsService;

@Service("adminStatisticsService")
public class AdminStatisticsServiceImpl implements IAdminStatisticsService{
	
	@Autowired
	private FStatisticsMapper statisticsMapper;
	
	public BigDecimal sumRWrmb(Integer type, Map<String, Object> map){
		map.put("ftype", type);
		return statisticsMapper.sumRWrmb(map);
	}

	@Override
	public BigDecimal sumOtherRmb(Map<String, Object> map) {
		return statisticsMapper.sumOtherRmb(map);
	}

	public BigDecimal sumRWcoin(Integer type, Map<String, Object> map, Integer coinid){
		map.put("ftype", type);
		map.put("fcoinid", coinid);
		
		return statisticsMapper.sumRWcoin(map);
	}
	
	public BigDecimal sumBSrmb(Integer type, Map<String, Object> map){
		map.put("ftype", type);
		return statisticsMapper.sumBSrmb(map);
	}
	
	public BigDecimal sumBScoin(Integer type, Map<String, Object> map, Integer coinid){
		map.put("ftype", type);
		map.put("fcoinid", coinid);
		
		return statisticsMapper.sumBScoin(map);
	}

}
