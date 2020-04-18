package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.report.ReportCapital;
import com.qkwl.common.dto.report.ReportTrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 资产充提统计数据接口
 */
@Mapper
public interface ReportCapitalMapper {

    /**
     * 查询充提小时统计数据
     * @param record
     * @return
     */
    List<ReportCapital> selectReportByHour(Map<String, Object> map);

    /**
     * 查询充提日统计数据
     * @param record
     * @return
     */
    List<ReportCapital> selectReportByDay(Map<String, Object> map);

    /**
     * 查询收益统计数据
     */
    List<ReportCapital> selectIncomeReportByHours(Map<String, Object> map);

}