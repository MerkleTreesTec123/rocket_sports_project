package com.qkwl.service.admin.bc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.log.FLogConsoleVirtualRecharge;

/**
 * 虚拟币手工充值日志-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FLogConsoleVirtualRechargeMapper {
	
	/**
	 * 删除
	 * @param fid 日志id              
	 * @return 删除记录数
	 */
	int deleteByPrimaryKey(Integer fid);

	/**
	 * 新增
	 * @param record 日志实体
	 * @return 插入记录数
	 */
	int insert(FLogConsoleVirtualRecharge record);

	/**
	 * 查询
	 * @param fid 日志id
	 * @return 日志实体
	 */
	FLogConsoleVirtualRecharge selectByPrimaryKey(Integer fid);

	/**
	 * 更新
	 * @param record 日志实体
	 * @return 更新记录数
	 */
	int updateByPrimaryKey(FLogConsoleVirtualRecharge record);
	
	/*********** 控台部分 *************/
	/**
	 * 分页查询数据	
	 * @param map 参数map
	 * @return 查询记录列表
	 */
	List<FLogConsoleVirtualRecharge> getAdminPageList(Map<String, Object> map);

	/**
	 * 分页查询数据总条数	
	 * @param map 参数map
	 * @return 查询记录数
	 */
	int countAdminPage(Map<String, Object> map);
	
	
	BigDecimal getTotalAmountByStatus(Map<String, Object> map);

	/**
	 * 历史活动数据关联
	 * @param record
	 * @return
	 */
	int updateByHistoryActivity(FLogConsoleVirtualRecharge record);
}