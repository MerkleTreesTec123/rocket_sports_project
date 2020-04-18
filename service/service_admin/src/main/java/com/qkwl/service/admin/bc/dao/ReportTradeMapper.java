package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.report.ReportTrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 交易统计数据接口
 */
@Mapper
public interface ReportTradeMapper {

    /**
     * 查询交易小时统计数据
     * @param record
     * @return
     */
    List<ReportTrade> selectReportByHour(Map<String, Object> map);

    /**
     * 查询交易日统计数据
     * @param record
     * @return
     */
    List<ReportTrade> selectReportByDay(Map<String, Object> map);

}