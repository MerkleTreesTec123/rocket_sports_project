package com.qkwl.service.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.daylog.FDayCapitalRmb;

/**
 * 每日人民币充提数据
 * @author TT
 */
@Mapper
public interface FDayCapitalRmbMapper {

	/**
	 * 新增数据
	 * @param record
	 * @return
	 */
    int insert(FDayCapitalRmb record);

    /**
     * 根据时间查找
     * @param fid
     * @return
     */
    List<FDayCapitalRmb> selectByDate(@Param("time") String time, @Param("fagentid") Integer fagentid);

    /**
     * 查找所有
     * @return
     */
    List<FDayCapitalRmb> selectAll();
    /**
     * 更新
     * @param record
     * @return
     */
    int updateByModel(FDayCapitalRmb record);
}