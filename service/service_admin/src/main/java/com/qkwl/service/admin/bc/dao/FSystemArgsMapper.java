package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.system.FSystemArgs;

/**
 * 系统参数-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FSystemArgsMapper {

    /**
     * 插入
     * @param record 系统参数
     * @return 插入记录数
     */
    int insert(FSystemArgs record);

    /**
     * 查询
     * @param fid 系统参数id
     * @return 系统参数实体
     */
    FSystemArgs selectByPrimaryKey(Integer fid);

    /**
     * 查找所有
     * @return 系统参数列表
     */
    List<FSystemArgs> selectAll();

    /**
     * 更新
     * @param record 系统参数实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FSystemArgs record);
    
    
    /*****Admin*****/
    
    /**
     * 分页查询系统参数
     * @param map 参数map
     * @return 查询记录列表
     */
    List<FSystemArgs> getSystemArgsPageList(Map<String, Object> map);
    
    /**
     * 查询系统参数的记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countSystemArgsByParam(Map<String, Object> map);
}