package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.service.admin.bc.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.log.FLogAdminAction;
import com.qkwl.common.dto.log.FLogUserAction;
import com.qkwl.common.dto.log.FLogUserScore;
import com.qkwl.common.rpc.admin.IAdminLogService;

/**
 * 日志管理接口实现
 * @author ZKF
 */
@Service("adminLogService")
public class AdminLogServiceImpl implements IAdminLogService{
	
	@Autowired
	private FLogAdminActionMapper logAdminActionMapper;
	@Autowired
	private FLogUserActionMapper logUserActionMapper;
	@Autowired
	private FLogUserScoreMapper logUserScoreMapper;
	@Autowired
	private FAdminMapper adminMapper;

	/**
	 * 分页查询管理员日志
	 * @param page 分页参数
	 * @param log 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminLogService#getAdminPageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.log.FLogAdminAction)
	 */
	@Override
	public Pagination<FLogAdminAction> selectAdminPageList(Pagination<FLogAdminAction> page, FLogAdminAction log) {
		Map<String, Object> map = new HashMap<String, Object>();
		//基础参数
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());		
		map.put("beginDate", page.getBegindate());
		map.put("endDate", page.getEnddate());

		int count = logAdminActionMapper.countPageList(map);
		if(count > 0) {
			List<FLogAdminAction> list = logAdminActionMapper.getPageList(map);
			for(FLogAdminAction adminLog : list){
				FAdmin admin = adminMapper.selectByPrimaryKey(adminLog.getFadminid());
				adminLog.setFadmin_s(admin.getFname());
			}
			page.setData(list);
		}
		page.setTotalRows(count);
		return page;
	}

	
	/**
	 * 分页查询用户日志
	 * @param page 分页参数
	 * @param log 实体参数
	 * @return 分页查询记录实体
	 * @see com.qkwl.common.rpc.admin.IAdminLogService#getUserScorePageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.log.FLogUserScore)
	 */
	@Override
	public Pagination<FLogUserScore> selectUserScorePageList(Pagination<FLogUserScore> page, FLogUserScore log) {
		Map<String, Object> map = new HashMap<String, Object>();
		//基础参数
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderField", page.getOrderField());
		map.put("orderDirection", page.getOrderDirection());		
		map.put("begindate", page.getBegindate());
		map.put("enddate", page.getEnddate());
		map.put("type", log.getFtype());

		int count = logUserScoreMapper.countPageList(map);
		if(count > 0) {
			List<FLogUserScore> list = logUserScoreMapper.getPageList(map);
			page.setData(list);
		}
		page.setTotalRows(count);
		return page;
	}
	

	public FLogUserAction selectVip6ByUser(Integer fuid, Integer type){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", fuid);
		map.put("ftype", type);
		return logUserActionMapper.selectVIP6ByUser(map);
	}

	/**
	 * 分页查询用户IP排序
	 * @param page 分页参数
	 * @param log 实体参数
	 * @return 分页查询记录列表
	 */
	@Override
	public Pagination<FLogUserAction> selectIpLoginRankPageList(Pagination<FLogUserAction> page, FLogUserAction log) {
		Map<String, Object> map = new HashMap<>();
		//基础参数
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("beginDate", page.getBegindate());
		map.put("endDate", page.getEnddate());

		int count = logUserActionMapper.countLoginIpPageList(map);
		if(count > 0) {
			List<FLogUserAction> list = logUserActionMapper.getLoginIpPageList(map);
			page.setData(list);
		}
		page.setTotalRows(count);
		return page;
	}
}
