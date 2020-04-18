package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.entrust.FEntrustHistory;

/**
 * 历史委单-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FEntrustHistoryMapper {

	/*********** 控台部分 *************/
	/**
	 * 分页查询数据
	 * 
	 * @param map 参数map
	 * @return 历史委单列表
	 */
	List<FEntrustHistory> getAdminPageList(Map<String, Object> map);
	
	
	/**
	 * 分页查询数据总条数
	 * 
	 * @param map 参数map
	 * @return 查询记录数
	 */
	int countAdminPage(Map<String, Object> map);
	
	
    /**
     * daysum统计委单
     * @param coinId 币种
     * @param fcreatetime 统计时间
     * @return 统计记录列表
     */
    Map<String, Object> selectTotal(@Param("ftradeid") int coinId, @Param("fcreatetime") String fcreatetime);
    
    
    /**
     * daysum统计委单人数
     * @param coinId 币种
     * @param fcreatetime 统计时间
     * @return 统计记录列表
     */
    Map<String, Object> selectPerson(@Param("ftradeid") int coinId, @Param("fcreatetime") String fcreatetime);
    
    
    /**
     * 根据类型查询委单统计
     * @param map 参数map
     * @return 委单统计
     */
    FEntrustHistory getTotalAmountByType(Map<String, Object> map);
}