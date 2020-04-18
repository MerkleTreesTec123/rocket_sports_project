package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.daylog.FDayTradeCoin;

/**
 * 交易统计-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FDayTradeCoinMapper {

	/**
	 * 分页查询
	 * @param map 参数map
	 * @return 查询记录列表
	 */
    List<FDayTradeCoin> getPageList(Map<String, Object> map);
    
    /**
     * 查询记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countPageList(Map<String, Object> map);
    
    /**
	 * 新增数据
	 * @param record 统计实体
	 * @return 插入记录数
	 */
    int insert(FDayTradeCoin record);

    /**
     * 根据时间查找
     * @param fcoinid 币种
	 * @param time 统计时间
     * @return 统计实体
     */
    FDayTradeCoin selectByDate(@Param("fcoinid") Integer fcoinid, @Param("time") String time);

    /**
     * 更新
     * @param record 统计实体
     * @return 更新记录数
     */
    int updateByModel(FDayTradeCoin record);
}