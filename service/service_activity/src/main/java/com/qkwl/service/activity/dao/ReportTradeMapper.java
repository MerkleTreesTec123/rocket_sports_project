package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.report.ReportTrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 交易统计数据接口
 */
@Mapper
public interface ReportTradeMapper {

    /**
     * 新增交易统计数据
     * @param record
     * @return
     */
    int insert(ReportTrade record);

}