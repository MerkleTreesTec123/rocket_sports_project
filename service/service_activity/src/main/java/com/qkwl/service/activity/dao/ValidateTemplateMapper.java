package com.qkwl.service.activity.dao;

import com.qkwl.service.activity.model.ValidateTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ValidateTemplateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ValidateTemplateDO record);

    ValidateTemplateDO selectByPrimaryKey(Integer id);

    List<ValidateTemplateDO> selectAll();

    int updateByPrimaryKey(ValidateTemplateDO record);

    List<ValidateTemplateDO> selectListByPage(Map<String, Object> map);

    Integer countListByPage(Map<String, Object> map);

    ValidateTemplateDO selectTemplateByParams(Map<String, Object> map);
}