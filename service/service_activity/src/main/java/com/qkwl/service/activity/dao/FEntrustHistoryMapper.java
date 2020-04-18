package com.qkwl.service.activity.dao;

import java.util.Map;

import com.qkwl.common.dto.report.ReportTrade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FEntrustHistoryMapper {

    /**
     * daysum统计委单
     * @return
     */
    Map<String, Object> selectTotal(@Param("ftradeId") int ftradeId, @Param("fcreatetime") String fcreatetime);
    
    /**
     * daysum统计委单人数
     * @return
     */
    Map<String, Object> selectPerson(@Param("ftradeId") int ftradeId, @Param("fcreatetime") String fcreatetime);

    /**
     * 数据采集
     * @param map
     * @return
     */
    ReportTrade selectReport(Map<String, Object> map);
}