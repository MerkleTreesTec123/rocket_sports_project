package com.qkwl.service.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.daylog.FDayOperat;

/**
 * 每日运营数据
 * @author TT
 */
@Mapper
public interface FDayOperatMapper {
	
	
    /**
	 * 新增数据
	 * @param record
	 * @return
	 */
    int insert(FDayOperat record);
    
    /**
     * 根据时间查找
     * @param fid
     * @return
     */
	List<FDayOperat> selectByDate(@Param("time") String time, @Param("fagentid") Integer fagentid);

    /**
     * 查找所有
     * @return
     */
    List<FDayOperat> selectAll();
    /**
     * 更新
     * @param record
     * @return
     */
    int updateByModel(FDayOperat record);
}