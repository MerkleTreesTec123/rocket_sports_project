package com.qkwl.service.user.dao;

import com.qkwl.service.user.model.FUserFinancesDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 存币理财数据操作接口
 * 
 * @author LY
 *
 */
@Mapper
public interface FUserFinancesMapper {
	/**
	 * 插入
	 * 
	 * @param record
	 * @return
	 */
	int insert(FUserFinancesDO record);

	/**
	 * 查询
	 *
	 * @param fid
	 * @return
	 */
	FUserFinancesDO select(Integer fid);
	/**
	 * 更新
	 *
	 * @param record
	 * @return
	 */
	int update(FUserFinancesDO record);

	/**
     * 分页查询列表
     * @param map
     * @return
     */
	List<FUserFinancesDO> selectUserFinancesList(Map<String, Object> map);

	/**
	 * 查询总条数
	 * @param map
	 * @return
	 */
	int selectUserFinancesCount(Map<String, Object> map);
}