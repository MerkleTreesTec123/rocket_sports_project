package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 运营数据查询接口
 * Created by ZKF on 2017/9/12.
 */
@Mapper
public interface OperationDataMapper {

    /**
     * 邮件数量
     * @param map
     * @return
     */
    Integer selectEmailNum(Map<String, Object> map);

    /**
     * 短信数量
     * @param map
     * @return
     */
    Integer selectSmsNum(Map<String, Object> map);
}
