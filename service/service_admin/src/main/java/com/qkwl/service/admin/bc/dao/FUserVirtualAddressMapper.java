package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;

/**
 * 用户虚拟币地址-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FUserVirtualAddressMapper {

    /**
	 * 根据用户查询用户的所有地址
	 * @param map 参数map
	 * @return 虚拟地址列表
	 */
    List<FUserVirtualAddressDTO> selectByMap(Map<String, Object> map);
    
    /**
	 * 检测提现地址是否平台地址
	 * @param address 地址
	 * @return 查询记录数
	 */
    int getAddressNum(@Param("address") String address);
    
    /**
	 * 根据充值地址查找用户
	 * @param address 地址
	 * @return 地址列表
	 */
    List<FUserVirtualAddressDTO> getUserByAddress(@Param("address") String address);

}