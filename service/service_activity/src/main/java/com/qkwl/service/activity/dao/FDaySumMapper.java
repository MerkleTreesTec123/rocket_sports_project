package com.qkwl.service.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.daylog.FDaySum;

@Mapper
public interface FDaySumMapper {
	
    /**
     * 插入
     * @param record
     * @return
     */
    int insert(FDaySum record);

    /**
     * 根据时间查找
     * @param fid
     * @return
     */
    List<FDaySum> selectByDate(@Param("fcoinid") Integer fcoinid, @Param("time") String time);

    /**
     * 查找所有
     * @return
     */
    List<FDaySum> selectAll();

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByModel(FDaySum record);
}