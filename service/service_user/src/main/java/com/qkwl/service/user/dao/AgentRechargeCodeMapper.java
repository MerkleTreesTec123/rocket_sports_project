package com.qkwl.service.user.dao;


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
     * 根据id查询
     * @param id
     * @return
     */
    AgentRechargeCode selectByPrimaryKey(Integer id);

    /**
     * 根据code查询
     * @param code
     * @return
     */
    AgentRechargeCode selectByCode(String code);


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