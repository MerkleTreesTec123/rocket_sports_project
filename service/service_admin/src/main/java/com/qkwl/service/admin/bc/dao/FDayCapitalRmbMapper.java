package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.daylog.FDayCapitalRmb;

/**
 * 人民币日统计-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FDayCapitalRmbMapper {
    
	/**
	 * 分页查询
	 * @param map 参数map
	 * @return 查询记录列表
	 */
    List<FDayCapitalRmb> getPageList(Map<String, Object> map);
    
    /**
     * 查询记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countPageList(Map<String, Object> map);
}