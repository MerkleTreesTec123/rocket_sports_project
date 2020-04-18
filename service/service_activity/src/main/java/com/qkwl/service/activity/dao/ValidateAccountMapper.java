package com.qkwl.service.activity.dao;

import com.qkwl.service.activity.model.ValidateAccountDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ValidateAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ValidateAccountDO record);

    ValidateAccountDO selectByPrimaryKey(Integer id);

    List<ValidateAccountDO> selectAll();

    int updateByPrimaryKey(ValidateAccountDO record);

    List<ValidateAccountDO> selectListByPage(Map<String, Object> map);

    Integer countListByPage(Map<String, Object> map);

}