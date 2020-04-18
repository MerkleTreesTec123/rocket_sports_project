package com.qkwl.common.rpc.admin;

import java.util.List;
import java.util.Map;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.finances.FUserFinancesDTO;

public interface IAdminUserFinances {
	
	/**
	 * 分页查询理财记录
	 * @param page 分页参数
	 * @param userFinances 理财实体
	 * @return
	 */
	Pagination<FUserFinancesDTO> selectUserFinancesByPage(Pagination<FUserFinancesDTO> page, FUserFinancesDTO userFinances);
	
	/**
	 * 查询理财资产平衡
	 * @param fuid 用户ID
 	 * @param fcoinid 币种ID
 	 * @param fstate 状态
	 * @return
	 */
	FUserFinancesDTO selectUserFinancesBalance(Integer fuid, Integer fcoinid, Integer fstate);

	/**
	 * 查询存币理财总计
	 * @return
	 */
	List<Map<String,Object>> selectUserFinancesTotal();

	/**
	 * 撤销存币理财
	 * @param fid
	 * @return
	 * @throws Exception
	 */
	Boolean updateUserFinances(Integer fid) throws Exception;

	/**
	 * 查询存币理财
	 * @param fid
	 * @return
	 * @throws Exception
	 */
	FUserFinancesDTO selectUserFinances(Integer fid);
}
