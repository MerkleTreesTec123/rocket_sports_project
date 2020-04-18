package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.log.FLogAdminAction;

/**
 * 管理员日志-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FLogAdminActionMapper {
	
	/**
	 * 插入数据
	 * @param record 日志实体
	 * @return 插入记录数
	 */
    int insert(FLogAdminAction record);
    
    
    /**
     * 分页查询管理员日志
     * @param map 参数map
     * @return 查询记录列表
     */
    List<FLogAdminAction> getPageList(Map<String, Object> map);
    
    
    /**
     * 分页查询管理员日志的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countPageList(Map<String, Object> map);
}