package com.qkwl.service.admin.bc.dao;

import java.util.List;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.agent.FAgent;

/**
 * 券商-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FAgentMapper {
	
	/**
	 * 增加券商
	 * @param agent 券商实体
	 * @return	插入记录数
	 */
	int insert(FAgent agent);
	
	/**
	 * 更新券商
	 * @param agent 券商实体
	 * @return 更新记录数
	 */
	int update(FAgent agent);
	
	/**
	 * 删除券商
	 * @param fid 券商id	
	 * @return 删除记录数
	 */
	int delete(int fid);

	/**
	 * 查询券商列表
	 * @return 券商列表
	 */
    List<FAgent> selectAll();
    
    /**
     * 查询单个券商
     * @param fid 券商id
     * @return	券商实体
     */
    FAgent selectByPrimaryKey(Integer fid);

    /**
     * 分页查询券商
     * @param map 参数map
     * @return 券商列表
     */
    List<FAgent> getPageList(Map<String, Object> map);
    
    /**
     * 分页查询的券商总记录数
     * @param map 参数map
     * @return 券商查询的总记录数
     */
    int countPageList(Map<String, Object> map);
}