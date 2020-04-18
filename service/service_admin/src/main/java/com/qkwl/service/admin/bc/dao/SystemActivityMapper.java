package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.activity.SystemActivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 系统活动
 * PS:暂用于归档
 */
@Mapper
public interface SystemActivityMapper {

    /**
     * 删除活动
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增活动
     * @param record
     * @return
     */
    int insert(SystemActivity record);

    /**
     * 查询单个活动
     * @param id
     * @return
     */
    SystemActivity selectByPrimaryKey(Integer id);

    /**
     * 查询所有活动
     * @return
     */
    List<SystemActivity> selectAll();

    /**
     * 更新活动
     * @param record
     * @return
     */
    int updateByPrimaryKey(SystemActivity record);

    /**
     * 查询活动分页
     */
    List<SystemActivity> selectByPage(Map<String, Object> map);

    /**
     * 查询活动分页总记录数
     */
    int countByPage(Map<String, Object> map);
}