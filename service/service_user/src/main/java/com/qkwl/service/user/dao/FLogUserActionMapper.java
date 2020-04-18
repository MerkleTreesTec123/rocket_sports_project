package com.qkwl.service.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.log.FLogUserAction;

/**
 * 用户日志数据接口
 * @author TT
 */
@Mapper
public interface FLogUserActionMapper {
	/**
	 * 查询用户登陆记录
	 * @param map 条件MAP
	 * @return 实体对象列表
	 */
	List<FLogUserAction> selectByType(Map<String, Object> map);
	
	
	/**
	 * 根据用户查询用户日志记录数
	 * @param map 条件MAP 
	 * @return 记录条数
	 */
	int countListByUser(Map<String, Object> map);


	/**
	 * 根据时间查询用户日志记录数
	 * @param map 条件MAP
	 * @return 记录条数
	 */
	int countByTime(Map<String, Object> map);
}