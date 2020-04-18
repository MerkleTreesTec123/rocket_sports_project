package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.log.FLogUserAction;

/**
 * 用户日志-数据库访问接口
 * @author TT
 */
@Mapper
public interface FLogUserActionMapper {

	/**
	 * 插入日志
	 * @param record 日志实体
	 * @return 插入记录数
	 */
    int insert(FLogUserAction record);
    
    /**
     * 分页查询用户日志
     * @param map 参数map
     * @return 查询记录列表
     */
    List<FLogUserAction> getPageList(Map<String, Object> map);
    
    /**
     * 分页查询用户日志的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countPageList(Map<String, Object> map);
    
    /**
     * 根据user查询购买vip6的记录
     * @return
     */
    FLogUserAction selectVIP6ByUser(Map<String, Object> map);

    /**
     * 分页查询用户日志
     * @param map 参数map
     * @return 查询记录列表
     */
    List<FLogUserAction> getLoginIpPageList(Map<String, Object> map);

    /**
     * 分页查询用户日志的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countLoginIpPageList(Map<String, Object> map);
}