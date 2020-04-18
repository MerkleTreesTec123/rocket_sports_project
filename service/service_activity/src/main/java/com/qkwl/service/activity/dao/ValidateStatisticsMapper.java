package com.qkwl.service.activity.dao;

import com.qkwl.service.activity.model.ValidateStatisticsDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ValidateStatisticsMapper {

    int insert(ValidateStatisticsDO record);

    ValidateStatisticsDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(ValidateStatisticsDO record);
}