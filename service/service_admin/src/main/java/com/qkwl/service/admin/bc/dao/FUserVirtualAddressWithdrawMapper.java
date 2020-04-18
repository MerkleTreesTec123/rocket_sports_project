package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.capital.FUserVirtualAddressWithdrawDTO;

import java.util.List;
import java.util.Map;


/**
 * 用户虚拟币提现地址-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FUserVirtualAddressWithdrawMapper {
    
    /**
     * 根据参数查询地址列表
     * @param map 参数map
     * @return 提现地址列表
     */
    List<FUserVirtualAddressWithdrawDTO> selectByMap(Map<String, Object> map);
    
}