package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.finances.FVirtualFinances;

/**
 * 存币理财数据操作接口
 * 
 * @author LY
 *
 */
@Mapper
public interface FVirtualFinancesMapper {
	/**
	 * 删除
	 * 
	 * @param fid
	 * @return
	 */
	int deleteByPrimaryKey(Integer fid);

	/**
	 * 新增
	 * 
	 * @param record
	 * @return
	 */
	int insert(FVirtualFinances record);

	/**
	 * 查询
	 * 
	 * @param fid
	 * @return
	 */
	FVirtualFinances selectByPrimaryKey(Integer fid);

	/**
	 * 查询所有
	 * 
	 * @return
	 */
	List<FVirtualFinances> selectAll();

	/**
	 * 更新
	 * 
	 * @param record
	 * @return
	 */
	int updateByPrimaryKey(FVirtualFinances record);

	/**
	 * 按币种查询
	 * 
	 * @param fcoinid
	 * @return
	 */
	List<FVirtualFinances> selectByCoinId(@Param("fcoinid") Integer fcoinid, @Param("fstate") Integer fstate);
}