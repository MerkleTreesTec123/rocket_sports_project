package com.qkwl.common.rpc.admin;

import java.util.Map;

import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.daylog.FDayCapitalCoin;
import com.qkwl.common.dto.daylog.FDayCapitalRmb;
import com.qkwl.common.dto.daylog.FDayOperat;
import com.qkwl.common.dto.daylog.FDaySum;
import com.qkwl.common.dto.daylog.FDayTradeCoin;

/**
 * 报表统计接口
 * @author ZKF
 */
public interface IAdminDayLogService {

	/**
	 * 分页查询币种日统计
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 查询记录列表
	 */
	public Pagination<FDayCapitalCoin> selectDayCapitalCoinList(Pagination<FDayCapitalCoin> pageParam, FDayCapitalCoin filterParam);
	
	
	/**
	 * 分页查询人民币日统计
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 查询记录列表
	 */
	public Pagination<FDayCapitalRmb> selectDayCapitalRmbList(Pagination<FDayCapitalRmb> pageParam, FDayCapitalRmb filterParam);
	
	
	/**
	 * 分页查询运营日统计
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 查询记录列表
	 */
	public Pagination<FDayOperat> selectDayOperatList(Pagination<FDayOperat> pageParam, FDayOperat filterParam);
	
	
	/**
	 * 分页查询交易日统计
	 * @param pageParam 分页查询
	 * @param filterParam 实体参数
	 * @return 查询记录列表
	 */
	public Pagination<FDayTradeCoin> selectDayTradeCoinList(Pagination<FDayTradeCoin> pageParam, FDayTradeCoin filterParam);


	/**
	 * 分页查询资产存量统计
	 * @param page 分页参数
	 * @param sum 实体参数
	 * @return 查询记录列表
	 */
	public Pagination<FDaySum> selectDaySumList(Pagination<FDaySum> page, FDaySum sum);

	/**
	 * 手动更新资产存量统计
	 * @return 是否执行成功
	 * @throws BCException 更新失败
	 */
	public boolean updateDaySum() throws BCException;
	

	/**
	 * 手动刷新交易日统计
	 * @return 是否执行成功
	 * @throws BCException 刷新失败
	 */
	public boolean updateDayTrade() throws BCException;
	
	/**
	 * 获取单个币种的存量
	 * @param coinid 币种id
	 * @return 存量键值对
	 */
	public Map<String, Object> selectCoinSum(int coinid);
}

