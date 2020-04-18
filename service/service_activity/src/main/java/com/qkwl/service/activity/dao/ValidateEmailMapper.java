package com.qkwl.service.activity.dao;

import com.qkwl.service.activity.model.ValidateEmailDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ValidateEmailMapper {

    int insert(ValidateEmailDO record);

    ValidateEmailDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(ValidateEmailDO record);
}