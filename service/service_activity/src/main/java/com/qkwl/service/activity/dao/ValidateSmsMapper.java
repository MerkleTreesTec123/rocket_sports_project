package com.qkwl.service.activity.dao;

import com.qkwl.service.activity.model.ValidateSmsDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ValidateSmsMapper {

    int insert(ValidateSmsDO record);

    ValidateSmsDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(ValidateSmsDO record);

    List<ValidateSmsDO> selectBlackList(Integer times);
}