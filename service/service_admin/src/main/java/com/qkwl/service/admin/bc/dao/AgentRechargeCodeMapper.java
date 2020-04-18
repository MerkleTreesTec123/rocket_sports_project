package com.qkwl.service.admin.bc.dao;


import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.capital.AgentRechargeCode;

import java.util.List;
import java.util.Map;

/**
 * 代理充值数据操作接口
 */
@Mapper
public interface AgentRechargeCodeMapper {
    /**
     * 删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增
     * @param record
     * @return
     */
    int insert(AgentRechargeCode record);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    AgentRechargeCode selectByPrimaryKey(Integer id);

    /**
     * 修改
     * @param record
     * @return
     */
    int updateByPrimaryKey(AgentRechargeCode record);

    /**
     * 分页查询
     * @param map
     * @return
     */
    List<AgentRechargeCode> selectByPage(Map<String, Object> map);

    /**
     * 分页总记录数
     * @param map
     * @return
     */
    int countByPage(Map<String, Object> map);
}