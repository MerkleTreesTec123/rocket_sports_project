package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.capital.WalletCapitalBalanceDTO;
import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.capital.FWalletCapitalOperationDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 钱包充值提现记录-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FWalletCapitalOperationMapper {
	

    /**
     * 新增记录
     * @param record 钱包操作实体
     * @return 插入记录数
     */
    int insert(FWalletCapitalOperationDTO record);

    /**
     * 根据id查询记录
     * @param fid 操作id
     * @return 操作实体
     */
    FWalletCapitalOperationDTO selectByPrimaryKey(Integer fid);

    /**
     * 更新记录
     * @param record 操作实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FWalletCapitalOperationDTO record);
    
    /**
     * 分页查询记录的记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countWalletCapitalOperation(Map<String,Object> map);

	/*********** 控台部分 *************/
	/**
	 * 分页查询数据	
	 * @param map 参数map
	 * @return 操作列表
	 */
	List<FWalletCapitalOperationDTO> getAdminPageList(Map<String, Object> map);

	/**
	 * 分页查询数据总条数	
	 * @param map 参数map
	 * @return 查询记录数
	 */
	int countAdminPage(Map<String, Object> map);
	
	/**
	 * 根据类型查询操作记录总金额
	 * @param map 参数map
	 * @return 总金额
	 */
	BigDecimal getTotalAmountByType(Map<String, Object> map);


	/**
	 * 根据流水号查询
	 * @param fserialno
	 * @return
	 */
	List<FWalletCapitalOperationDTO> selectBySerialno(@Param(value = "fserialno") String fserialno);

	/**
	 * 匹配充值记录
	 */
	List<FWalletCapitalOperationDTO> selectMatchRecord(
			@Param(value = "fuid") Integer fuid, @Param(value = "famount") BigDecimal famount
			, @Param(value = "rechargeType") Integer rechargeType);

	/**
	 * 匹配充值记录
	 */
	List<FWalletCapitalOperationDTO> selectMatchRecordByAccountName(
			@Param(value = "faccount") String faccount, @Param(value = "frealname") String frealname,
			@Param(value = "famount") BigDecimal famount, @Param(value = "rechargeType") Integer rechargeType);

	/**
	 * 根据流水号查询记录
	 * @param serialNumber 流水号
	 * @return 实体对象
	 */
	FWalletCapitalOperationDTO selectBySerialNumber(String serialNumber);

}