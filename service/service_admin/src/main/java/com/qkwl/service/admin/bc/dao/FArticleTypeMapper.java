package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.news.FArticleType;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 新闻类型-数据库访问接口
 * @author ZKF
 */
@Mapper
public interface FArticleTypeMapper {
	
	/**
	 * 删除
	 * @param fid 新闻类型id
	 * @return 删除记录数
	 */
    int deleteByPrimaryKey(Integer fid);

    /**
     * 插入
     * @param record 新闻类型实体
     * @return 插入记录数
     */
    int insert(FArticleType record);

    /**
     * 查询
     * @param fid 新闻类型id
     * @return 新闻实体
     */
    FArticleType selectByPrimaryKey(Integer fid);

    /**
     * 查询所有
     * @return 新闻实体列表
     */
    List<FArticleType> selectAll(@Param("lanId") Integer lanId);

    /**
     * 更新
     * @param record 新闻类型实体
     * @return 更新记录数
     */
    int updateByPrimaryKey(FArticleType record);
    
    /**
     * 查询
     * @param map 参数map
     * @return 新闻类型列表
     */
    List<FArticleType> getArticleTypePageList(Map<String, Object> map);
    
    /**
     * 查询总数
     * @param map 参数map
     * @return 查询记录数
     */
    int countArticleTypePageList(Map<String, Object> map);
}