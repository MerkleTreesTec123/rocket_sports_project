package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.finances.FUserFinancesDTO;

/**
 * 存币理财数据操作接口
 * 
 * @author LY
 *
 */
@Mapper
public interface FUserFinancesMapper {

	/**
     * 分页查询列表
     * @param map
     * @return
     */
	List<FUserFinancesDTO> selectUserFinancesList(Map<String, Object> map);

	/**
	 * 查询总条数
	 * @param map
	 * @return
	 */
	int selectUserFinancesCount(Map<String, Object> map);
	
	/**
	 * 查询理财平衡
	 * @param map
	 * @return
	 */
	FUserFinancesDTO selectUserFinancesBalance(@Param("fuid") Integer fuid, @Param("fcoinid")  Integer fcoinid, @Param("fstate")  Integer fstate);
	
	/**
	 * 查询存币理财总计
	 * @return
	 */
	List<Map<String, Object>> selectUserFinancesTotal();

	/**
	 * 查询
	 *
	 * @param fid
	 * @return
	 */
	FUserFinancesDTO select(Integer fid);
	/**
	 * 更新
	 *
	 * @param record
	 * @return
	 */
	int update(FUserFinancesDTO record);
}