package com.qkwl.service.user.dao;

import com.qkwl.service.user.model.FVirtualCapitalOperationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 虚拟币充值提现记录 数据操作接口
 * @author ZKF
 */
@Mapper
public interface FVirtualCapitalOperationMapper {
	
	/**
	 * 新增记录
	 * @param record 实体对象
	 * @return 成功条数
	 */
    int insert(FVirtualCapitalOperationDO record);

    /**
     * 根据id查询记录
     * @param fid 主键ID
     * @return 实体对象
     */
    FVirtualCapitalOperationDO selectByPrimaryKey(Integer fid);

    /**
     * 更新记录
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(FVirtualCapitalOperationDO record);
    
    /**
     * 分页查询记录
     * @param map 查询条件MAP
     * @return 实体对象列表
     */
    List<FVirtualCapitalOperationDO> getPageVirtualCapitalOperation(Map<String, Object> map);
    
    /**
     * 分页查询记录总条数
     * @param map 查询条件MAP
     * @return 记录总数
     */
    int countVirtualCapitalOperation(Map<String, Object> map);

    /**
     * 根据用户查询提现次数
     * @param fuid 用户ID
     * @return 提现次数
     */
	int getVirtualWalletWithdrawTimes(int fuid);

    /**
     * 根据交易id查询记录
     * @param funiquenumber 交易ID
     * @return 实体对象
     */
    FVirtualCapitalOperationDO selectByTxid(@Param("funiquenumber") String funiquenumber);

    /**
     * 查询用户今日提现金额
     * @param fuid 用户ID
     * @return 今日提现金额
     */
    BigDecimal getDayWithdrawCoin(@Param("fuid") int fuid, @Param("fcoinid") int fcoinid);
}