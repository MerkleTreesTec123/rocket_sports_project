package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.report.ReportCapital;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 资产充提统计数据接口
 */
@Mapper
public interface ReportCapitalMapper {

    /**
     * 新增充提统计数据
     * @param record
     * @return
     */
    int insert(ReportCapital record);

}