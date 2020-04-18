package com.qkwl.common.rpc.admin;


import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;

public interface IAdminSystemTradeTypeService {
	/**
	 * 分页查询交易信息
	 * @param page 分页数据
 	 * @param tradeType 交易信息
	 * @return
	 */
	public Pagination<SystemTradeType> selectSystemTradeTypeList(Pagination<SystemTradeType> page, SystemTradeType tradeType);
	
	/**
	 * 查询交易信息
	 * @param tradeId 交易ID
	 * @return
	 */
	public SystemTradeType selectSystemTradeType(Integer tradeId);
	
	/**
	 * 新增交易信息
	 * @param tradeType
	 * @return
	 */
	public boolean insertSystemTradeType(SystemTradeType tradeType);
	
	/**
	 * 更新交易信息
	 * @param tradeType
	 * @return
	 */
	public boolean updateSystemTradeType(SystemTradeType tradeType);
	
	/**
	 * 删除交易信息
	 * @param tradeId
	 * @return
	 */
	public boolean deleteSystemTradeType(Integer tradeId);

	SystemTradeType selectSystemTradeTypeByCoinId(Integer sellCoinId,Integer buyCoinId);
	
}
