package com.qkwl.service.activity.dao;

import com.qkwl.service.activity.model.ValidatePlatformDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ValidatePlatformMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ValidatePlatformDO record);

    ValidatePlatformDO selectByPrimaryKey(Integer id);

    List<ValidatePlatformDO> selectAll();

    int updateByPrimaryKey(ValidatePlatformDO record);

    List<ValidatePlatformDO> selectListByPage(Map<String, Object> map);

    Integer countListByPage(Map<String, Object> map);
}