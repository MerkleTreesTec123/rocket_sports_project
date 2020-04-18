package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.activity.ActivitySendStatistics;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivitySendStatisticsMapper {

    /**
     * 查询活动分页
     */
    List<ActivitySendStatistics> selectByPage(Map<String, Object> map);

    /**
     * 查询活动分页总记录数
     */
    int countByPage(Map<String, Object> map);

    /**
     * 归档活动数据
     * @param id
     * @return
     */
    int archiveActivityData(Integer id);
}