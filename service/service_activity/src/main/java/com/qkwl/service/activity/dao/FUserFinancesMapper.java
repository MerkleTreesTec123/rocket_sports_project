package com.qkwl.service.activity.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
	 * 查询存币理财待发放记录
	 * @param fstate
	 * @param fupdatetime
	 * @return
	 */
	List<FUserFinancesDTO> selectUserFinancesByState(@Param("fstate") Integer fstate, @Param("fupdatetime") Date fupdatetime);

	/**
	 * 更新记录
	 * @param record
	 * @return
	 */
	int updateByPrimaryKey(FUserFinancesDTO record);
}