package com.qkwl.service.admin.validate.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.service.admin.validate.model.ValidatePlatformDO;

import java.util.List;
import java.util.Map;

/**
 * 平台数据数据操作接口
 */
@Mapper
public interface ValidatePlatformMapper {

    /**
     * 新增
     *
     * @param record 实体
     * @return 成功条数
     */
    Integer insert(ValidatePlatformDO record);

    /**
     * 查询
     *
     * @param id 主键ID
     * @return 实体
     */
    ValidatePlatformDO select(Integer id);

    /**
     * 查询所有
     * @return
     */
    List<ValidatePlatformDO> selectAll();

    /**
     * 修改
     *
     * @param record 实体
     * @return 成功条数
     */
    Integer update(ValidatePlatformDO record);

    /**
     * 分页查询
     *
     * @param map 查询条件
     * @return 分页列表
     */
    List<ValidatePlatformDO> listPage(Map<String, Object> map);

    /**
     * 总记录数
     *
     * @param map 查询条件
     * @return 总记录数
     */
    Integer countPage(Map<String, Object> map);
}