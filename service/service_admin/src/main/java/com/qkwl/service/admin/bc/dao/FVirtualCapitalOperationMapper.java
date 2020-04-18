package com.qkwl.service.admin.bc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;

/**
 * 虚拟币充值提现记录-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FVirtualCapitalOperationMapper {
	
	/**
	 * 新增记录
	 * @param record 虚拟币操作记录
	 * @return 插入记录数
	 */
    int insert(FVirtualCapitalOperationDTO record);

	/**
	 * 新增虚拟币充值订单
	 */
	int insertRecharge(FVirtualCapitalOperationDTO record);

    /**
     * 更新记录
     * @param record 虚拟币操作记录
     * @return 更新记录数
     */
    int updateByPrimaryKey(FVirtualCapitalOperationDTO record);
    
    /**
     * 分页查询记录总条数
     * @param map 参数map
     * @return 查询记录数
     */
	int countVirtualCapitalOperation(Map<String, Object> map);

	
	/*********** 控台部分 *************/
	/**
	 * 分页查询数据	
	 * @param map 参数map
	 * @return 查询列表
	 */
	List<FVirtualCapitalOperationDTO> getAdminPageList(Map<String, Object> map);

	/**
	 * 分页查询数据总条数	
	 * @param map 参数map
	 * @return 查询记录数
	 */
	int countAdminPage(Map<String, Object> map);
	
	/**
     * 根据id查询记录(包括扩展字段)
     * @param fid 操作id
     * @return 操作实体
     */
    FVirtualCapitalOperationDTO selectAllById(@Param("fid") int fid);
    
    /**
	 * 根据类型查询操作记录总数量
	 * @param map 参数map
	 * @return 总数量
	 */
    BigDecimal getTotalAmountByType(Map<String, Object> map);

    /**
	 * 查询nonce
     * @return
     */
    Integer selectNonce(@Param("fcoinid") int fcoinid);

	/**
	 * 查询交易记录数
	 */
	Integer selectByTx(String txid);
}