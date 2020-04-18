package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.coin.FPool;

import java.util.List;
import java.util.Map;

/**
 * 虚拟币地址池-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FPoolMapper {
	
    /**
     * 插入
     * @param record 地址实体
     * @return	插入记录数
     */
    int insert(FPool record);

    /**
     * 获取剩余数量
     * @param map 参数map
     * @return 查询记录列表
     */
    List<Map<String, Object>> getVirtualCoinAddressNumList(Map<String, Object> map);
    
    /**
     * 获取剩余数量总条数
     * @param map 参数map
     * @return 查询记录数
     */
    int countVirtualCoinAddressNumList(Map<String, Object> map);
    
    
}