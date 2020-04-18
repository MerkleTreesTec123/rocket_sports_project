package com.qkwl.service.user.dao;

import com.qkwl.common.dto.log.FLogConsoleVirtualRecharge;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 虚拟币手工充值日志-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FLogConsoleVirtualRechargeMapper {
	

	/**
	 * 分页查询数据	
	 * @param map 参数map
	 * @return 查询记录列表
	 */
	List<FLogConsoleVirtualRecharge> listPage(Map<String, Object> map);

	/**
	 * 分页查询数据总条数	
	 * @param map 参数map
	 * @return 查询记录数
	 */
	int countPage(Map<String, Object> map);

}