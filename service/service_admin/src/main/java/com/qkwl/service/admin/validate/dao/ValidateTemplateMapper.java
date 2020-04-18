package com.qkwl.service.admin.validate.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.service.admin.validate.model.ValidateTemplateDO;

import java.util.List;
import java.util.Map;

/**
 * 消息模板数据操作接口
 */
@Mapper
public interface ValidateTemplateMapper {

    /**
     * 查询
     * @param id 主键ID
     * @return ValidateTemplateDO
     */
    ValidateTemplateDO select(Integer id);

    /**
     * 查询所有
     * @return
     */
    List<ValidateTemplateDO> selectAll();

    /**
     * 修改
     * @param record 实体
     * @return 成功条数
     */
    Integer update(ValidateTemplateDO record);

    /**
     * 分页查询
     * @param map 查询条件
     * @return 分页列表
     */
    List<ValidateTemplateDO> listPage(Map<String, Object> map);

    /**
     * 总记录数
     * @param map 查询条件
     * @return 总记录数
     */
    Integer countPage(Map<String, Object> map);
}