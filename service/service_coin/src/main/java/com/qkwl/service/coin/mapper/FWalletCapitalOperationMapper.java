package com.qkwl.service.coin.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 钱包充值提现记录-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FWalletCapitalOperationMapper {
	
    /**
     * 查询记录的记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countWalletCapitalOperation(Map<String, Object> map);

}