package com.qkwl.service.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.daylog.FDayTradeCoin;

/**
 * 每日交易数据
 * @author TT
 */
@Mapper
public interface FDayTradeCoinMapper {
	
    /**
	 * 新增数据
	 * @param record
	 * @return
	 */
    int insert(FDayTradeCoin record);

    /**
     * 根据时间查找
     * @param fid
     * @return
     */
    List<FDayTradeCoin> selectByDate(@Param("fcoinid") Integer fcoinid, @Param("time") String time, @Param("fagentid") Integer fagentid);

    /**
     * 查找所有
     * @return
     */
    List<FDayTradeCoin> selectAll();
    /**
     * 更新
     * @param record
     * @return
     */
    int updateByModel(FDayTradeCoin record);
}