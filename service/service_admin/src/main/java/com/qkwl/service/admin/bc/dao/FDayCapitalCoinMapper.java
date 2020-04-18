package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.daylog.FDayCapitalCoin;

/**
 * 虚拟币日统计-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FDayCapitalCoinMapper {
    
	/**
	 * 分页统计
	 * @param map 参数map
	 * @return 统计记录列表
	 */
    List<FDayCapitalCoin> getAdminPageList(Map<String, Object> map);
    
    /**
     * 分页查询记录
     * @param map 参数map
     * @return 查询记录数
     */
    int countAdminPageList(Map<String, Object> map);
}