package com.qkwl.service.user.dao;

import com.qkwl.service.user.model.FWalletCapitalOperationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 钱包充值提现记录数据操作接口
 * @author ZKF
 */
@Mapper
public interface FWalletCapitalOperationMapper {

    /**
     * 新增记录
     * @param record 实体对象
     * @return 成功条数
     */
    int insert(FWalletCapitalOperationDO record);

    /**
     * 根据id查询记录
     * @param fid 主键ID
     * @return 实体对象
     */
    FWalletCapitalOperationDO selectByPrimaryKey(Integer fid);

    /**
     * 根据流水号查询记录
     * @param serialNumber 流水号
     * @return 实体对象
     */
    FWalletCapitalOperationDO selectBySerialNumber(String serialNumber);
    /**
     * 更新记录
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(FWalletCapitalOperationDO record);

    /**
     * 更新记录状态
     * @param record 实体对象
     * @return 成功条数
     */
    int updateStatusByPrimaryKey(FWalletCapitalOperationDO record);
    
    /**
     * 分页查询记录
     * @param map 查询条件MAP
     * @return 实体对象列表
     */
    List<FWalletCapitalOperationDO> getPageWalletCapitalOperation(Map<String, Object> map);

    /**
     * 分页查询记录的记录数
     * @param map 查询条件MAP
     * @return 记录总数
     */
    int countWalletCapitalOperation(Map<String, Object> map);

    /**
     * 根据用户查询人民币提现次数
     * @param fuid 用户ID
     * @return 提现次数
     */
	int getWalletWithdrawTimes(int fuid);

	/**
	 * 查询用户今日提现金额
	 * @param fuid 用户ID
	 * @return 今日提现金额
	 */
    BigDecimal getDayWithdrawCny(@Param("fuid") int fuid);

    /**
     * 根据条件查询数量
     * @param map 查询条件MAP
     * @return 记录总数
     */
    List<FWalletCapitalOperationDO> getAmountWalletCapitalOperation(Map<String, Object> map);
}