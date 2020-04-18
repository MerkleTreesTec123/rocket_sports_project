package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.activity.ActivityFinancialStatistics;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityFinancialStatisticsMapper {

    List<ActivityFinancialStatistics> selectByPage(Map<String, Object> map);

    int countByPage(Map<String, Object> map);

    int archiveActivityData(Integer id);
}