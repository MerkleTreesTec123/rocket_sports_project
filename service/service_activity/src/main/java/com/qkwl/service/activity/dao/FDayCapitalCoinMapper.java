package com.qkwl.service.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.daylog.FDayCapitalCoin;

/**
 * 每日虚拟币充提数据
 * @author TT
 */
@Mapper
public interface FDayCapitalCoinMapper {
	
	/**
	 * 新增数据
	 * @param record
	 * @return
	 */
    int insert(FDayCapitalCoin record);

    /**
     * 根据时间查找
     * @param fid
     * @return
     */
	List<FDayCapitalCoin> selectByDate(@Param("fcoinid") Integer fcoinid, @Param("time") String time, @Param("fagentid") Integer fagentid);

    /**
     * 查找所有
     * @return
     */
    List<FDayCapitalCoin> selectAll();

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByModel(FDayCapitalCoin record);
}